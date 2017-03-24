package com.dotincorp.touchingdot.X_DoNotUse;

/**
 * Created by wjddk on 2017-03-14.
 */

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.dotincorp.touchingdot.R;

public class MusicService extends Service {

    public MediaPlayer mp;

    public IBinder onBind(Intent arg0) {
        // Service 객체와 (화면단 Activity 사이에서)
        // 통신(데이터를 주고받을) 때 사용하는 메서드
        // 데이터를 전달할 필요가 없으면 return null;
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 서비스에서 가장 먼저 호출됨(최초에 한번만)
        Log.d("test", "Service onCreate");
        mp = MediaPlayer.create(this, R.raw.br_learning_bgm);
        mp.setLooping(false); // 반복재생
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("touchingDOT", "Service onStartCommand()");
        mp.start(); //음악 재생
        return super.onStartCommand(intent, flags, startId);
    }
    public void onDestroy() {
        Log.d("touchingDOT", "Service onDestroy()");
        super.onDestroy();
        mp.stop(); //음악 정지
    }
}