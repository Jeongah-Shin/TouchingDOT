package com.dotincorp.watchservice;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import static android.content.ContentValues.TAG;

/**
 * Service for managing connection and data communication with a GATT server hosted on a given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {


    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private String deviceAddress;
    private BluetoothGatt bluetoothGatt;
    private int connectionState = STATE_DISCONNECTED;

    private BluetoothGattCharacteristic characteristicNotiSend;
    private BluetoothGattCharacteristic characteristicNotiReceive;
    private BluetoothGattCharacteristic mMsgSendCharacter;
    private BluetoothGattCharacteristic mMsgRecvCharacter;

    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED = "com.dotincorp.watchservice.ble.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.dotincorp.watchservice.ble.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.dotincorp.watchservice.ble.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.dotincorp.watchservice.ble.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = "com.dotincorp.watchservice.ble.EXTRA_DATA";
    public final static String ACTION_GATT_OBSERVER_SETTED = "com.dotincorp.watchservice.ble.ACTION_GATT_OBSERVER_SETTED";


    private LinkedBlockingQueue<byte[]> messageList;
    private LinkedBlockingQueue<byte[]> brailleList;

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                connectionState = STATE_CONNECTED;

                // Send broadcast ACTION_GATT_CONNECTED
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");

                // Attempts to discover services after successful connection.
                boolean discoverServiceStarted = bluetoothGatt.discoverServices();
                Log.i(TAG, "Attempting to start service discovery:" + discoverServiceStarted);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                connectionState = STATE_DISCONNECTED;

                // Send broadcast ACTION_GATT_DISCONNECTED
                broadcastUpdate(intentAction);
                Log.i(TAG, "Disconnected from GATT server.");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                setGattObserver(); // gatt 변화 감지
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);

                final byte[] data = characteristic.getValue();
                if (data != null && data.length > 0) {
                    final StringBuilder stringBuilder = new StringBuilder(data.length);
                    for (byte byteChar : data)
                        stringBuilder.append(String.format("%02X ", byteChar));
                    Log.i(TAG, "onCharacteristicRead: " + stringBuilder.toString());
                }
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
//            Log.i(TAG,"onCharacteristicChanged");
            Log.i(TAG, "APP <<<<  Received  <<<< WATCH : " + byteToString(characteristic.getValue()));

            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);

//            response(characteristic.getValue()); // 받은 데이터에 대한 해석과 반응
            handleReceive(characteristic.getValue());
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);

            Log.i(TAG, "RSSI : " + rssi);
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        if (action.equals(ACTION_DATA_AVAILABLE)) {
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, stringBuilder.toString());
            }
        }

        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (bluetoothManager == null) {
            bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(final String address) {
        if (bluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (deviceAddress != null && address.equals(deviceAddress)
                && bluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing bluetoothGatt for connection.");
            if (bluetoothGatt.connect()) {
                connectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        bluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        deviceAddress = address;
        connectionState = STATE_CONNECTING;

//        setGattObserver(); // 응답 받을 gatt 등록

        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (bluetoothAdapter == null || bluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        bluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (bluetoothGatt == null) {
            return;
        }
        bluetoothGatt.close();
        bluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (bluetoothAdapter == null || bluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return false;
        }
        Log.i(TAG, "APP >>>>>   Send   >>>>> WATCH : " + byteToString(characteristic.getValue()));
        return bluetoothGatt.writeCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (bluetoothAdapter == null || bluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        bluetoothGatt.setCharacteristicNotification(characteristic, enabled);


        // UUID_INIT_NOTI_RECV 수신 설정
        // callback method 등록하는 것 처럼
        // 이 부분이 반드시 있어야 함
        // --------중요 시작--------
        if ((GattAttributes.getUUID(GattAttributes.ATTR_INIT_NOTI_RECV).equals(characteristic.getUuid())) || (GattAttributes.getUUID(GattAttributes.ATTR_MSG_RECV).equals(characteristic.getUuid()))) {
            Log.i(TAG, "UUID_INIT_NOTI_RECV");
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(GattAttributes.getUUID(GattAttributes.DESC_NOTI_DESC));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            bluetoothGatt.writeDescriptor(descriptor);
        }


//        if ( (UUID_INIT_NOTI_SEND.equals(characteristic.getUuid())) || (UUID_MSG_SEND.equals(characteristic.getUuid())) )  {
//            Log.i(TAG, "UUID_INIT_NOTI_RECV");
//            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
//                    UUID.fromString(GattAttributes.NOTI_DESC));
//            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            bluetoothGatt.writeDescriptor(descriptor);
//        }

//        if (UUID_MSG_RECV.equals(characteristic.getUuid())) {
//            Log.i(TAG, "UUID_MSG_RECV");
////            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
////                    UUID.fromString(GattAttributes.NOTI_DESC));
////            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
////            descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
//            for(BluetoothGattDescriptor descriptor : characteristic.getDescriptors()){
//                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                bluetoothGatt.writeDescriptor(descriptor);
//            }
//        }
        // --------중요 끝--------
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (bluetoothGatt == null) return null;

        return bluetoothGatt.getServices();
    }

    public void setGattObserver() {
        boolean serviceConnected = false;
        for (BluetoothGattService gattService : getSupportedGattServices()) {
            if (gattService.getUuid().equals(GattAttributes.getUUID(GattAttributes.SERVICE_DOT))) {
                for (BluetoothGattCharacteristic gattCharacteristic : gattService.getCharacteristics()) {
                    for (BluetoothGattDescriptor descriptor : gattCharacteristic.getDescriptors()) {
                        Log.i(TAG, gattCharacteristic.getUuid().toString() + " descriptor UUID :" + descriptor.getUuid().toString());
                    }

                    if (gattCharacteristic.getUuid().equals(GattAttributes.getUUID(GattAttributes.ATTR_INIT_NOTI_RECV)))// ATTR_INIT_NOTI_RECV
                    {
                        setCharacteristicNotification(gattCharacteristic, true); // 수신 등록 (callback 개념)
                        characteristicNotiReceive = gattCharacteristic;
                    }

                    if (gattCharacteristic.getUuid().equals(GattAttributes.getUUID(GattAttributes.ATTR_INIT_NOTI_SEND))) // ATTR_INIT_NOTI_SEND
                    {
                        characteristicNotiSend = gattCharacteristic;
                    }

                    if (gattCharacteristic.getUuid().equals(GattAttributes.getUUID(GattAttributes.ATTR_MSG_RECV))) // ATTR_MSG_RECV
                    {
                        setCharacteristicNotification(gattCharacteristic, true); // 수신 등록 (callback 개념)
                        mMsgRecvCharacter = gattCharacteristic;
                    }

                    if (gattCharacteristic.getUuid().equals(GattAttributes.getUUID(GattAttributes.ATTR_MSG_SEND))) // MSG_SEND
                    {
                        mMsgSendCharacter = gattCharacteristic;
                    }
                }

                serviceConnected = true;
            }
        }

        if (serviceConnected) {
            broadcastUpdate(ACTION_GATT_OBSERVER_SETTED);
        } else {
            disconnect();
        }
    }

    /***********************************
     *             Send Byte           *
     ***********************************/

    private void sendCommand(byte[] command) {
        if ((characteristicNotiSend != null) && (characteristicNotiSend.getUuid().equals(UUID.fromString(GattAttributes.ATTR_INIT_NOTI_SEND)))) {
            characteristicNotiSend.setValue(command);
            writeCharacteristic(characteristicNotiSend);
        }
    }

    public void sendGenAck(byte header) { // GEN_ACK(OK) - 0x00 0x02 0x02 0x00
        byte outputByte[] = new byte[4];
        outputByte[0] = 0x00;
        outputByte[1] = 0x02;
        outputByte[2] = header;
        outputByte[3] = 0x00;

        sendCommand(outputByte);
    }

    private void consumeBrailleList() {
        try {
            byte[] command = brailleList.take();

            if (command == null || command.length == 0) {
                return;
            }
            sendCommand(command);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean sendBrailleBytes(byte[] bytes) {
        if (bytes == null || bytes.length > 4) {
            return false;
        }

        byte[] command = new byte[bytes.length + 4];
        command[0] = 0x06;
        command[1] = (byte) (bytes.length + 2);
        command[2] = 1;
        command[3] = 1;

        System.arraycopy(bytes, 0, command, 4, bytes.length);

        brailleList.add(command);
        consumeBrailleList();

        return true;
    }


    /**
     * Send Hex format string
     * format: 31323334:
     * length: < 8
     *
     * @param hexString hex String
     * @return send status
     */
    public boolean sendBrailleHex(String hexString) {
        // 메세지 내용이 없을 때 전송하지 않음
        if (hexString == null || hexString.isEmpty() || hexString.length() > 8) {
            return false;
        }

        // remove space
        hexString = hexString.replace(" ", "");

        byte[] byteMessage = hexStringToByte(hexString);
        return sendBrailleBytes(byteMessage);
    }

    private void consumeMessageList() {
        try {
            byte[] command = messageList.take();
            if (command == null || command.length == 0) {
                return;
            }
            sendCommand(command);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean sendMessageBytes(byte[] bytes) {
        if (bytes == null || bytes.length > 256) {
            return false;
        }

        int lenTotal = bytes.length / 16;
        int remainByte = bytes.length % 16;

        // 메시지를 16바이트로 나누어 처리한다.
        for (int i = 0; i < lenTotal; i++) {
            byte[] byteSliceMessage = new byte[16];
            System.arraycopy(bytes, i * 16, byteSliceMessage, 0, 16);
            byte[] command = makeMessageCommand(i + 1, lenTotal + 1, byteSliceMessage);
            messageList.add(command);
        }

        // 마지막 남은 바이트 처리
        if (remainByte > 0) {
            byte[] byteSliceMessage = new byte[remainByte];
            System.arraycopy(bytes, lenTotal * 16, byteSliceMessage, 0, remainByte);
            byte[] command = makeMessageCommand(lenTotal + 1, lenTotal + 1, byteSliceMessage);
            messageList.add(command);
        }

        consumeMessageList();

        return true;
    }

    private byte[] makeMessageCommand(int index, int totalIndex, byte[] message) {
        byte cmdByte[] = new byte[message.length + 4];
        cmdByte[0] = 0x0A;
        cmdByte[1] = (byte) (message.length + 2);
        cmdByte[2] = (byte) index;
        cmdByte[3] = (byte) totalIndex;

        System.arraycopy(message, 0, cmdByte, 4, message.length);

        return cmdByte;
    }

    public boolean sendMessage(String message) {
        // 메세지 내용이 없을 때 전송하지 않음
        if (message == null || message.isEmpty()) {
            return false;
        }

        byte[] byteMessage = message.getBytes(Charset.forName("UTF-8"));
        return sendMessageBytes(byteMessage);
    }


    /***********************************
     *           Response Byte         *
     ***********************************/

    // 받은 데이터에 대한 해석과 반응
    public void handleReceive(final byte[] receivedData) {
        if (receivedData.length < 1) { // 데이터 없을 때. 종료됨.
            return;
        }

        // Ack
        if (receivedData[0] == 0x00) {
            handleAck(receivedData);
        } else if (receivedData[0] == 0x06) {
            sendGenAck(receivedData[0]);
            consumeBrailleList();
        } else if (receivedData[0] == 0x0A) {
            sendGenAck(receivedData[0]);
            consumeMessageList();
        }
    }

    private void handleAck(byte[] receivedData) {

    }


    /***********************************
     *           Hex Controll          *
     ***********************************/

    // byte 보여주기 위해서 string으로 변환
    public String byteToString(byte[] dataArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte data : dataArray) {
            stringBuilder.append(String.format("%02X ", data));
        }
        return stringBuilder.toString();
    }

    // hex형태로 입력된 string을 byte로 바꿈
    public byte[] hexStringToByte(String hexString) {
        ArrayList<String> strings = new ArrayList<>();

        for (int i = 0; i < hexString.length(); i += 2) {
            strings.add(hexString.substring(i, i + 2));
        }

        byte[] outData = new byte[strings.size()]; // 20개로 제한
        for (int i = 0; i < strings.size(); i++) {
            outData[i] = (byte) Integer.parseInt(strings.get(i), 16);
        }

        return outData;
    }


    /***********************************
     *          Service Event          *
     ***********************************/

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        messageList = new LinkedBlockingQueue<>();
        brailleList = new LinkedBlockingQueue<>();
    }

    /***********************************
     *         Setter & Getter         *
     ***********************************/
}
