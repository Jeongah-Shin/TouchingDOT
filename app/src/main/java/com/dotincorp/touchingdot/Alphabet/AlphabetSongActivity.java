package com.dotincorp.touchingdot.Alphabet;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import com.dotincorp.touchingdot.MenuActivity;
import com.dotincorp.touchingdot.R;
import com.dotincorp.watchservice.BluetoothLeService;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;
import static com.dotincorp.touchingdot.BluetoothScanning.MainActivity.bluetoothService;

// ---------------------------------------------------------------------------------------
public class AlphabetSongActivity extends MenuActivity implements Runnable, SeekBar.OnSeekBarChangeListener {
    public int connectionStatus = BluetoothLeService.STATE_DISCONNECTED;

    //imgBtn - btn_play/pause/btnstop, previous_area, next_area
    //SeekBar - seekBar

    @Bind(R.id.seekBar)
    SeekBar seekBar;

    MediaPlayer mPlayer;
    int[] area = {8290, 11250, 14200, 17500, 19000, 20120, 23120, 26090, 49100, 52000, 55050, 58550, 60000, 61120, 64050, 68000, 90000, 92550, 96020, 99520, 100800, 101200, 104400, 107200};
    String[] br = {"01030919","110b1b00", "130a1a05", "070d1d15", "0f000000", "1f170e00", "le252700", "3a2d3d35"};
    int a=0; //current area position
    int max=area.length-1; //whole parts number

    /**
     * 서비스 연결
     */
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bluetoothService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!bluetoothService.initialize()) {
                finish();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bluetoothService = null;

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

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) { // 연결 해제 되었을 때
                Log.i(TAG, "Received ACTION_GATT_DISCONNECTED");
                connectionStatus = BluetoothLeService.STATE_DISCONNECTED;

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
    protected void onResume() {
        super.onResume();
        // 리시버 등록
        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());

        // 서비스에 연결
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alphabet_song);
        ButterKnife.bind(this);

        Handler h = new Handler();
        Runnable a = new Runnable(){
            public void run(){
                connect(loadDeviceInfo("address"));
            }
        };
        h.postDelayed(a,4000);

    }

    @OnClick({R.id.btn_play,R.id.btn_stop, R.id.btn_pause, R.id.previous_area, R.id.next_area})
    public void musicController(View v){
        if (v.getId() == R.id.btn_play) {
            if (mPlayer != null && mPlayer.isPlaying()) return;
            if(seekBar.getProgress() > 0) {
                mPlayer.start();
                new Thread(this).start();
                return;
            }
            mPlayer = MediaPlayer.create(AlphabetSongActivity.this, R.raw.abcsong);
            mPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mPlayer.getDuration());
            new Thread(this).start();
        }

        if ((v.getId() ==R.id.btn_pause) && (mPlayer!=null)) {
            mPlayer.pause();
        }
        if ((v.getId() ==R.id.btn_stop) && (mPlayer!=null)) {
            mPlayer.stop();
            try {
                mPlayer.prepare();
            }
            catch(IOException ie) {
                ie.printStackTrace();
            }
            mPlayer.seekTo(0);
        }
        if(v.getId() ==R.id.previous_area&& (mPlayer!=null)){
            a--;
            if (a<0){
                a = max;
            }
            mPlayer.seekTo(area[a]-500);
            brailleSync();
            new Thread(this).start();
        }
        if (v.getId() ==R.id.next_area&& (mPlayer!=null)){
            a++;
            if (a>max){
                a=0;
            }
            mPlayer.seekTo(area[a]-500);
            brailleSync();
            new Thread(this).start();
        }

    }

    public void run() {
        int currentPosition= 0;
        int total = mPlayer.getDuration();
        while (mPlayer!=null && currentPosition<total) {
            try {
                Thread.sleep(1000);
                currentPosition= mPlayer.getCurrentPosition();
            } catch (InterruptedException e) {
                return;
            } catch (Exception e) {
                return;
            }
            seekBar.setProgress(currentPosition);
        }

    }

    public void brailleSync(){
        int cp = mPlayer.getCurrentPosition();
        if((area[a]-500<=cp)&&(cp<=area[a+1]-500)){
            sendBraille(br[a]);
        }
    }

    private void sendBraille(String brailleHex) {
        if (bluetoothService != null) {
            // TODO: 펌웨어 업데이트 되어야 동작함
            bluetoothService.sendBrailleHex(brailleHex);
        }
    }
    /**
     * 장치 연결
     *
     * @return 연결 성공 여부
     */
    private boolean connect(String address) {
        if (bluetoothService != null) {
            return bluetoothService.connect(address);
        }else{
            return false;
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

    // arbitary moving of SeekBar by users
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        if(fromUser){
            seekBar.setProgress(progress);
            mPlayer.seekTo(progress/mPlayer.getDuration());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.release();
    }

    @Override
    protected void onPause() {
        mPlayer.release();
        //  리시버 해제
        unregisterReceiver(gattUpdateReceiver);

        // 서비스 연결 해제
        unbindService(serviceConnection);
        bluetoothService = null;

        super.onPause();
    }
}