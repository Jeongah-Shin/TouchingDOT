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
    /**
     * 튜토리얼 재생 Activity
     */

    BleApplication bleApplication;
    //    TextToSpeech TTS;
    TextToSpeech kTTS;
    TimerTask mTask;
    Timer mTimer;

    public static final String TUTORIAL_SCRIPT = "닷 워치에는 4개의 셀이 있습니다/첫번째 셀/두번째 셀/세번째 셀/네번째 셀/각각의 셀은 하나의 문자를 의미합니다/" +
            "        닷워치 점자 학습에서는 첫번째 셀을 이용해 점자를 학습합니다/점자 학습은 크게 세 분류로 나뉩니다/첫번째! 점자 탭/점자 학습 파트에서는 점형을 감별하는 연습을 하게 됩니다/" +
            "        점자 학습!에서는 1점에서 6점까지의 점자를 차례대로 올려보고 원하는 점자 올리기!에서는 원하는 점형을 입력하여 닷 워치에서 보여줍니다/점자 찾기!에서는 점형이 알고 싶은 글자를 입력하면 닷 워치에서 해당 글자를 출력합니다/" +
            "        두번째! 알파벳 탭.알파벳 학습에서는 기본적인 알파벳 스물 여섯자의 학습 및 알파벳 송을 통한 알파벳 암기 기능을 지원합니다/테스트 과정을 통해 학습 성취를 점검할 수 있습니다/마지막으로 특수문자 탭/" +
            "        특수 문자, 구두점과 닷 워치 시간을 읽기 위한 숫자 점자 기호를 학습할 수 있습니다/자! 이제 학습을 시작해볼까요?";

    int step = 0;
    String[] tutorial = TUTORIAL_SCRIPT.split("/");
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
    protected void onPause() {
        super.onPause();
        kTTS.shutdown();
        if ((mTimer != null) && (mTask != null)) {
            mTimer.cancel();
            mTask.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
