package com.dotincorp.touchingdot;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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

import java.util.Locale;

import static com.dotincorp.touchingdot.BleApplication.bluetoothService;

public class GetConnectionActivity extends Activity implements View.OnClickListener {
    /**
     * Application class 내에 있는 요소에 접근하는 방법 2가지
     * 1) ((BleApplication) getApplication()).[함수 호출]; 이런 식으로 바로 끌어올 수 도 있다.
     * 2) onReusme()에 있는 Application 객체 생성이 적용되지 않으면 시도해볼 것.
     */

    private static final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;
    Handler mHandler;

    static int REQUEST_SCAN = 0x00010001;
    private static final int MY_REQUEST_PERMISSION_CODE = 2;
    BleApplication bleApplication;
    TextToSpeech TTS;
    TextToSpeech kTTS;

    TextView text_device_name;
    TextView text_device_address;
    TextView text_connection_state;
    Button button_connect_disconnect;
    Button button_select_device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        text_device_name = (TextView) findViewById(R.id.text_device_name);
        text_device_address = (TextView) findViewById(R.id.text_device_address);
        text_connection_state = (TextView) findViewById(R.id.text_connection_state);
        button_connect_disconnect = (Button) findViewById(R.id.button_connect_disconnect);
        button_connect_disconnect.setOnClickListener(this);
        button_select_device = (Button) findViewById(R.id.button_select_device);
        button_select_device.setOnClickListener(this);

        TTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    TTS.setLanguage(Locale.ENGLISH);
                }
            }
        });
        kTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    kTTS.setLanguage(Locale.KOREAN);
                }
            }
        });

        mHandler = new Handler();
        permissionCheck();
        //updateView();
    }

    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.button_select_device:
                startActivityForResult(new Intent(this, DeviceScanActivity.class), REQUEST_SCAN);
                break;
            case R.id.button_connect_disconnect:
                if (bleApplication.connectionStatus==BluetoothLeService.STATE_DISCONNECTED) {
                    bleApplication.connect();
                    kTTS.speak("닷 워치가 연결될 때 까지 잠시 기다려주세요.", TextToSpeech.QUEUE_FLUSH, null);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(mainIntent);
                        }
                    }, 4000);
                } else {
                    bleApplication.disconnect();
                }

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bleApplication = (BleApplication) getApplication();
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
        if (bleApplication.deviceName != null && !bleApplication.deviceName.isEmpty()) {
            text_device_name.setText(bleApplication.deviceName);
        }
        if (bleApplication.deviceAddress != null && !bleApplication.deviceAddress.isEmpty()) {
            text_device_address.setText(bleApplication.deviceAddress);
        }

        // 상테 텍스트 변경
        if (bleApplication.connectionStatus == BluetoothLeService.STATE_CONNECTED) {
            text_connection_state.setText("연결 완료");
        } else if (bleApplication.connectionStatus == BluetoothLeService.STATE_CONNECTING) {
            text_connection_state.setText("연결 중");
        } else {
            text_connection_state.setText("연결 끊김");
        }

        // 연결/해제 버튼 상태 변경
        boolean enableGroups = bleApplication.deviceName != null &&
                !bleApplication.deviceName.isEmpty() && bleApplication.deviceAddress != null &&
                !bleApplication.deviceAddress.isEmpty() && serviceEnabled;

        if (bleApplication.connectionStatus == BluetoothLeService.STATE_CONNECTED) {
            button_connect_disconnect.setText("연결 해제");
        } else if (bleApplication.connectionStatus == BluetoothLeService.STATE_CONNECTING) {
            button_connect_disconnect.setText("연결 중");
            enableGroups = false;
        } else {
            button_connect_disconnect.setText("연결 하기");
        }

        button_connect_disconnect.setEnabled(enableGroups);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SCAN && resultCode == RESULT_OK) {
            String deviceName = data.getStringExtra(Constants.EXTRA_DEVICE_NAME);
            String deviceAddress = data.getStringExtra(Constants.EXTRA_DEVICE_ADDRESS);

            if (deviceName != null && !deviceName.isEmpty() && deviceAddress != null && !deviceAddress.isEmpty()) {
                bleApplication.deviceName = deviceName;
                bleApplication.deviceAddress = deviceAddress;
                updateView();
                //clearEditTexts();
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void permissionCheck() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    ) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, MY_REQUEST_PERMISSION_CODE);

            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                        },
                        MY_REQUEST_PERMISSION_CODE);
            }


        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_REQUEST_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "권한을 획득했습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GetConnectionActivity.this, "권한이 없습니다.", Toast.LENGTH_LONG);
                    finish();
                }

                return;
            }

        }
    }

    private String loadDeviceInfo(String index) {
        SharedPreferences pref = getSharedPreferences("device", Activity.MODE_APPEND);
        if (index == "address") {
            String mac = pref.getString("mac", "0");
            return mac;
        } else if (index == "name") {
            String device = pref.getString("name", "device");
            return device;
        } else {
            return "null";
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TTS.shutdown();
        kTTS.shutdown();
    }


}
