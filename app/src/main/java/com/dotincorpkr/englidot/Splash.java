package com.dotincorpkr.englidot;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.widget.LinearLayout;

/**
 * Created by wjddk on 2017-01-23.
 */
public class Splash extends Activity implements TextToSpeech.OnInitListener {

    private TextToSpeech myTTS;
    String logo = "englidot";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        // 첫 화면에서 로고명을 출력하기 위한 Text-to-Speech 객체를 선언합니다.
        myTTS = new TextToSpeech(this, this);

        // splash.xml의 구성요소 선언부
        LinearLayout englidot_logo = (LinearLayout)findViewById(R.id.englidot_logo);

        //로고의 문자열 받아오기
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                finish();
            }
        }, 3000);// 3 초

    }

   /*TextToSpeech 기능을 사용하기 위한 onInit 함수를 호출한다.
    로고를 출력한다.*/

    public void onInit(int status){
        myTTS.speak(logo, TextToSpeech.QUEUE_FLUSH, null);
    }

    // 화면이 없어지면(destroy) myTTS 객체도 없앤다(shut down).
    @Override
    protected void onDestroy() {
        super.onDestroy();
        myTTS.shutdown();
    }
}


