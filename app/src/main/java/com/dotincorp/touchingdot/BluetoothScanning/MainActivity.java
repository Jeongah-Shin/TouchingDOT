package com.dotincorp.touchingdot.BluetoothScanning;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dotincorp.touchingdot.Braille.BrailleEducationActivity;
import com.dotincorp.touchingdot.Constants;
import com.dotincorp.touchingdot.R;
import com.dotincorp.watchservice.BluetoothLeService;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity implements View.OnClickListener,TextToSpeech.OnInitListener {
    String deviceName;
    String deviceAddress;
    public int connectionStatus = BluetoothLeService.STATE_DISCONNECTED;
    private static final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;
    Handler mHandler;

    static int REQUEST_SCAN = 0x00010001;
    private static final int MY_REQUEST_PERMISSION_CODE = 2;

    // UI Widgets
    @Bind(R.id.text_device_name)
    TextView text_device_name;
    @Bind(R.id.text_device_address)
    TextView text_device_address;
    @Bind(R.id.text_connection_state)
    TextView text_connection_state;
    @Bind(R.id.button_connect_disconnect)
    Button button_connect_disconnect;
    @Bind(R.id.button_select_device)
    Button button_select_device;

    TextToSpeech mTTS;
    String speakWords;

    /**
     * 블루투스 서비스
     */
    public static BluetoothLeService bluetoothService;

    /**
     * 서비스 연결
     */
    public final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bluetoothService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!bluetoothService.initialize()) {
                finish();
            }

            updateView();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bluetoothService = null;
            updateView();
        }

    };

    /**
     * 연결 상태 업데이트 리시버
     */
    private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) { // 연결되었을 때
                Log.i(TAG, "Received ACTION_GATT_CONNECTED");
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.i(TAG, "Received ACTION_GATT_SERVICES_DISCOVERED");
            } else if (BluetoothLeService.ACTION_GATT_OBSERVER_SETTED.equals(action)) {
                Log.i(TAG, "Received ACTION_GATT_OBSERVER_SETTED");
                connectionStatus = BluetoothLeService.STATE_CONNECTED;
                updateView();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) { // 연결 해제 되었을 때
                Log.i(TAG, "Received ACTION_GATT_DISCONNECTED");
                connectionStatus = BluetoothLeService.STATE_DISCONNECTED;
                updateView();
            }
        }
    };

    private IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_OBSERVER_SETTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        return intentFilter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mTTS = new TextToSpeech(this,this);
        mHandler = new Handler();
        permissionCheck();
        updateView();
    }


    /**
     * Update View states
     */
    @SuppressLint("SetTextI18n")
    private void updateView() {
        boolean serviceEnabled = bluetoothService != null;

        // 장치 선택 버튼 변경
        button_select_device.setEnabled(serviceEnabled);

        // 장치 이름 / 주소 변경
        if (deviceName != null && !deviceName.isEmpty()) {
            text_device_name.setText(deviceName);
        }
        if (deviceAddress != null && !deviceAddress.isEmpty()) {
            text_device_address.setText(deviceAddress);
        }

        // 상테 텍스트 변경
        if (connectionStatus == BluetoothLeService.STATE_CONNECTED) {
            text_connection_state.setText("CONNECTED");
        } else if (connectionStatus == BluetoothLeService.STATE_CONNECTING) {
            text_connection_state.setText("CONNECTING");
        } else {
            text_connection_state.setText("DISCONNECTED");
        }

        // 연결/해제 버튼 상태 변경
        boolean enableGroups = deviceName != null && !deviceName.isEmpty() && deviceAddress != null && !deviceAddress.isEmpty() && serviceEnabled;

        if (connectionStatus == BluetoothLeService.STATE_CONNECTED) {
            button_connect_disconnect.setText("DISCONNECT");
        } else if (connectionStatus == BluetoothLeService.STATE_CONNECTING) {
            button_connect_disconnect.setText("CONNECTING");
            enableGroups = false;
        } else {
            button_connect_disconnect.setText("CONNECT");
        }

        button_connect_disconnect.setEnabled(enableGroups);
    }

    @Override
    protected void onResume() {

        super.onResume();
        // 리시버 등록
        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());

        // 서비스에 연결
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //  리시버 해제
        unregisterReceiver(gattUpdateReceiver);
        // 서비스 연결 해제
        unbindService(serviceConnection);
        bluetoothService = null;
    }

    @Override
    protected void onDestroy() {
        mTTS.shutdown();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SCAN && resultCode == RESULT_OK) {
            String deviceName = data.getStringExtra(Constants.EXTRA_DEVICE_NAME);
            String deviceAddress = data.getStringExtra(Constants.EXTRA_DEVICE_ADDRESS);

            if (deviceName != null && !deviceName.isEmpty() && deviceAddress != null && !deviceAddress.isEmpty()) {
                this.deviceName = deviceName;
                this.deviceAddress = deviceAddress;
                updateView();
                //clearEditTexts();
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 장치 연결
     *
     * @return 연결 성공 여부
     */
    private boolean connect() {
        if (bluetoothService != null) {
            return bluetoothService.connect(deviceAddress);
        }else{
            return false;
        }

    }


    /**
     * 장치 연결 종료
     */
    private void disconnect() {
        if (bluetoothService != null) {
            bluetoothService.disconnect();
        }
    }

    /**
     * 메시지 보내기
     * @param message 보낼 메시지
     */
    private void sendMessage(String message) {
        if (bluetoothService != null) {
            bluetoothService.sendMessage(message);
        }
    }

    /**f
     * 점자 코드 보내기
     * 최대 4개의 점자 코드를 보낼 수 있다.
     * 00 ~ 3F 까지 가능.
     *
     * @param brailleHex 보낼 점자 코드
     */
    private void sendBraille(String brailleHex) {
        if (bluetoothService != null) {
            // TODO: 펌웨어 업데이트 되어야 동작함
            bluetoothService.sendBrailleHex(brailleHex);
        }
    }

    /**
     * 모든 점 올리기
     */
    private void raiseAll() {
        if (bluetoothService != null) {
            // TODO: 펌웨어 업데이트 되면 sendBraille 로 수정
            bluetoothService.sendMessage("forforforfor"); // 펌웨어 업데이트 될때까지 for의 약어로 처리
            // bluetoothService.sendBraille("3F3F3F3F");
        }
    }

    /**
     * 모든 점 내리기
     */
    private void lowerAll() {
        if (bluetoothService != null) {
            // TODO: 펌웨어 업데이트 되면 sendBraille 로 수정
            bluetoothService.sendMessage("    "); // 펌웨어 업데이트 될때까지 공백 네개를 보내는것으로 처리
            // bluetoothService.sendBraille("00000000");
        }
    }

    @OnClick({R.id.button_select_device, R.id.button_connect_disconnect})
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.button_select_device:
                startActivityForResult(new Intent(this, DeviceScanActivity.class), REQUEST_SCAN);
                break;
            case R.id.button_connect_disconnect:
                    if(text_connection_state.getText()=="DISCONNECTED"){
                        connect();
                        speakWords = "Please wait a second until the dot watch gets connection.";
                        onInit(1);
                        mHandler.postDelayed(new Runnable(){
                            @Override
                            public void run(){
                                deviceCheckDialog();
                            }

                        },2000);
                    }else{
                        disconnect();
                    }

                break;
        }
    }

    private void deviceCheckDialog(){
        AlertDialog.Builder deviceCheck = new AlertDialog.Builder(this);
        deviceCheck.setMessage("["+deviceName+"]"+"\n"+"Please check your pairing status.");
        deviceCheck.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent brIntent = new Intent(getApplicationContext(),BrailleEducationActivity.class);
                startActivity(brIntent);
                finish();
            }
        });

        deviceCheck.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                disconnect();
                startActivityForResult(new Intent(MainActivity.this, DeviceScanActivity.class), REQUEST_SCAN);
            }
        });
        deviceCheck.show();
    }

    public void permissionCheck(){

        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                ||ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED
                ||ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS)!=PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    ||ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)
                    ||ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE}, MY_REQUEST_PERMISSION_CODE);

            }
            else{
                ActivityCompat.requestPermissions(this, new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.READ_CONTACTS},
                        MY_REQUEST_PERMISSION_CODE);
            }



        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MY_REQUEST_PERMISSION_CODE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(), "권한을 획득했습니다.",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MainActivity.this, "권한이 없습니다.",Toast.LENGTH_LONG);
                    finish();
                }

                return;
            }

        }
    }
    private String loadDeviceInfo(String index){
        SharedPreferences pref = getSharedPreferences("device", Activity.MODE_APPEND);
        if(index=="address"){
            String mac = pref.getString("mac","0");
            return mac;
        }else if(index == "name"){
            String device = pref.getString("name","device");
            return device;
        }else{
            return "null";
        }
    }

    public void onInit (int status){
        mTTS.setLanguage(Locale.ENGLISH);
        mTTS.speak(speakWords, TextToSpeech.QUEUE_FLUSH, null);
    }




}
