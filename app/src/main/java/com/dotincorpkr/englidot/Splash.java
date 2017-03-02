package com.dotincorpkr.englidot;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;

/**
 * Created by wjddk on 2017-01-23.
 */
public class Splash extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);


        // splash.xml의 구성요소 선언부
        LinearLayout englidot_logo = (LinearLayout)findViewById(R.id.englidot_logo);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                finish();
            }
        }, 2000);// 3 초

    }


}


