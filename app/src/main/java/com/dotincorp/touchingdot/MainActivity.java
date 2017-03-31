package com.dotincorp.touchingdot;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dotincorp.watchservice.BluetoothLeService;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.dotincorp.touchingdot.BleApplication.bluetoothService;

public class MainActivity extends Activity{
    String deviceName;
    String deviceAddress;
    public int connectionStatus = BluetoothLeService.STATE_DISCONNECTED;
    private static final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;
    Handler mHandler;

    static int REQUEST_SCAN = 0x00010001;
    private static final int MY_REQUEST_PERMISSION_CODE = 2;

    BleApplication bleApplication = (BleApplication)getApplicationContext();

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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


    @OnClick({R.id.button_select_device, R.id.button_connect_disconnect})
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.button_select_device:
                startActivityForResult(new Intent(this, DeviceScanActivity.class), REQUEST_SCAN);
                break;
            case R.id.button_connect_disconnect:
                    if(text_connection_state.getText()=="DISCONNECTED"){
                        bleApplication.connect();
                        bleApplication.mTTS.speak("Please wait a second until the dot watch gets connection.", TextToSpeech.QUEUE_FLUSH, null);
                        mHandler.postDelayed(new Runnable(){
                            @Override
                            public void run(){
                                deviceCheckDialog();
                            }

                        },2000);
                    }else{
                        bleApplication.disconnect();
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
                bleApplication.disconnect();
                startActivityForResult(new Intent(MainActivity.this, DeviceScanActivity.class), REQUEST_SCAN);
            }
        });
        deviceCheck.show();
    }

    public void permissionCheck(){

        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                ||ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED
//                ||ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS)!=PackageManager.PERMISSION_GRANTED
                ){

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                    ||ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)
//                    ||ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS)!=PackageManager.PERMISSION_GRANTED
                    ){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.READ_CONTACTS,
                                                    Manifest.permission.READ_EXTERNAL_STORAGE}, MY_REQUEST_PERMISSION_CODE);

            }
            else{
                ActivityCompat.requestPermissions(this, new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                //Manifest.permission.READ_CONTACTS
                        },
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





}
