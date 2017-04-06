package com.dotincorp.touchingdot;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static android.speech.tts.TextToSpeech.QUEUE_ADD;

/**
 * Created by wjddk on 2017-04-04.
 */

public class DwTutorialActivity extends Activity {

    BleApplication bleApplication;
    //    TextToSpeech TTS;
    TextToSpeech kTTS;
    TimerTask mTask;
    Timer mTimer;

    int step = 0;
    String[] tutorial = new String[100];
    ImageView cell1;
    ImageView cell2;
    ImageView cell3;
    ImageView cell4;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ap_tutorial);
        title = (TextView) findViewById(R.id.title);
        title.setText("Dot Watch로 점자 배우기");

//        TTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                if (status != TextToSpeech.ERROR) {
//                    TTS.setLanguage(Locale.ENGLISH);
//                }
//            }
//        });
        kTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    kTTS.setLanguage(Locale.KOREAN);
                }
            }
        });

        tutorialPlay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tutorial = getString(R.string.tutorial).split(".");
        bleApplication = (BleApplication) getApplication();
    }

    public void tutorialPlay() {
        mTask = new TimerTask() {
            @Override
            public void run() {
                switch (step) {
                    case 1:
                        runOnUiThread(new Thread(new Runnable() {
                            public void run() {
                                cell1 = (ImageView) findViewById(R.id.cell1);
                                cell1.setVisibility(View.VISIBLE);
                            }
                        }));
                        bleApplication.sendBraille("3F000000");
                        break;
                    case 2:
                        runOnUiThread(new Thread(new Runnable() {
                            public void run() {
                                cell2 = (ImageView) findViewById(R.id.cell2);
                                cell2.setVisibility(View.VISIBLE);
                            }
                        }));
                        bleApplication.sendBraille("003F0000");
                        break;
                    case 3:
                        runOnUiThread(new Thread(new Runnable() {
                            public void run() {
                                cell3 = (ImageView) findViewById(R.id.cell3);
                                cell3.setVisibility(View.VISIBLE);
                            }
                        }));
                        bleApplication.sendBraille("00003F00");
                        break;
                    case 4:
                        runOnUiThread(new Thread(new Runnable() {
                            public void run() {

                                cell4 = (ImageView) findViewById(R.id.cell4);
                                cell4.setVisibility(View.VISIBLE);
                            }
                        }));
                        bleApplication.sendBraille("0000003F");
                        break;
                    case 18:
                        mTimer.cancel();
                        mTask.cancel();
                }
                kTTS.speak(tutorial[step], QUEUE_ADD, null);
                step++;
            }
        };
        mTimer = new Timer();
        mTimer.schedule(mTask, 1000, 5000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        kTTS.shutdown();
        if ((mTimer != null) && (mTask != null)) {
            mTimer.cancel();
            mTask.cancel();
        }
    }
}
