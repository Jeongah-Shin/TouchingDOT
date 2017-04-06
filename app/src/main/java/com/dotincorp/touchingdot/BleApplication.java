package com.dotincorp.touchingdot;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.dotincorp.watchservice.BluetoothLeService;

/**
 * Created by wjddk on 2017-03-29.
 *
 * Application Class 의 수명 주기 이벤트
 * onCreate() -> 애플리케이션이 생성될 때 마다 호출, 모든 상태변수와 리소스 초기화
 * onTerminate() -> 애플리케이션 객체가 종료될 때 호출되는데 항상 보증하지 않는다.
 * onLowMemory() -> 시스템이 리소스가 부족할 때 마다 발생한다.
 * onConfigurationChanged() -> 애플리케이션은 구성변경을 위해 재시작하지 않는다. 변경이 필요하다면 여기서 Handler 적용!
 */

public class BleApplication extends Application {

    String deviceName;
    String deviceAddress;
    public int connectionStatus = BluetoothLeService.STATE_DISCONNECTED;

    static int REQUEST_SCAN = 0x00010001;

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
                //finish();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bluetoothService = null;
        }

    };


    @Override
    public void onCreate(){
        super.onCreate();

        // 서비스에 연결
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        unbindService(serviceConnection);
        bluetoothService = null;
    }

    /**
     * 사용 함수들: connect(), disconnect(), sendMessage(), sendBraille(), raiseAll(), lowerAll()
     */

    public boolean connect() {
        if (bluetoothService != null) {
            return bluetoothService.connect(deviceAddress);
        }else{
            return false;
        }

    }

    public void disconnect() {
        if (bluetoothService != null) {
            bluetoothService.disconnect();
        }
    }

    // @param message 보낼 메시지
    public void sendMessage(String message) {
        if (bluetoothService != null) {
            bluetoothService.sendMessage(message);
        }
    }

    //     점자 코드 보내기
    //     최대 4개의 점자 코드를 보낼 수 있다. 00 ~ 3F 까지 가능.
    //     @param brailleHex 보낼 점자 코드
    public void sendBraille(String brailleHex) {
        if (bluetoothService != null) {
            bluetoothService.sendBrailleHex(brailleHex);
        }
    }

    // 모든 점 올리기
    public void raiseAll() {
        if (bluetoothService != null) {
            bluetoothService.sendBrailleHex("3F3F3F3F");
        }
    }

    // 모든 점 내리기
    public void lowerAll() {
        if (bluetoothService != null) {
            bluetoothService.sendBrailleHex("00000000");
        }
    }
}
