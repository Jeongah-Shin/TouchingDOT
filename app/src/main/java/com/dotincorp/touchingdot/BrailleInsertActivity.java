package com.dotincorp.touchingdot;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import java.util.Locale;

/**
 * Created by wjddk on 2017-01-23.
 */

public class BrailleInsertActivity extends Activity implements View.OnClickListener, RadioButton.OnCheckedChangeListener {

    /**
     * 점자 dictation을 연습할 수 있는 Activity
     */

    //액티비티 구성 요소
    CheckBox br1;
    CheckBox br2;
    CheckBox br3;
    CheckBox br4;
    CheckBox br5;
    CheckBox br6;
    Button send;
    Button previous;
    Button next;
    LinearLayout braille_learning;

    TextToSpeech TTS;

    int sum;
    public static boolean dictation = false;
    char letter_result;
    int correct,incorrect;
    String speakWords;

    static public char presentLetter;
    Vibrator m_vibrator;
    SoundPool soundPool;

    protected void onResume(){
        super.onResume();
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        correct = soundPool.load(this, R.raw.correct, 1);
        incorrect = soundPool.load(this, R.raw.incorrect, 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("점자를 입력해주세요.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.braille_grid);
        viewBinder();

        m_vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        TTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    TTS.setLanguage(Locale.ENGLISH);
                }
            }
        });

    }

    public void onClick(View v){
        int id = v.getId();
        if (id ==R.id.send){
            brailleResult();
            answerCheck();
            if (dictation){
                finish();
            }
        }
    }

    //체크 상태가 바뀔 때 마다 음성으로 알려준다.
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.br1:
                if (br1.isChecked()) {
                    speakWords = "dot 1 checked";
                } else speakWords = "dot 1 canceled";
                break;
            case R.id.br2:
                if (br2.isChecked()) speakWords = "dot 2 checked";
                else speakWords = "dot 2 canceled";
                break;
            case R.id.br3:
                if (br3.isChecked()) speakWords = "dot 3 checked";
                else speakWords = "dot 3 canceled";
                break;
            case R.id.br4:
                if (br4.isChecked()) speakWords = "dot 4 checked";
                else speakWords = "dot 4 canceled";
                break;
            case R.id.br5:
                if (br5.isChecked()) speakWords = "dot 5 checked";
                else speakWords = "dot 5 canceled";
                break;
            case R.id.br6:
                if (br6.isChecked()) speakWords = "dot 6 checked";
                else speakWords = "dot 6 canceled";
                break;
        }
        TTS.speak(speakWords, TextToSpeech.QUEUE_FLUSH, null);
    }



    public void brailleResult(){
        sum = 0;

        if (br1.isChecked()) {
            sum += 1;
        }

        if (br2.isChecked()) {
            sum += 2;

        }
        if (br3.isChecked()) {
            sum += 4;

        }
        if (br4.isChecked()) {
            sum += 8;
        }
        if (br5.isChecked()) {
            sum += 16;
        }
        if (br6.isChecked()) {
            sum += 32;
        }

        letter_result = Character.toUpperCase(ASCIIcharacter.brailleResult(sum));

    }

    public void answerCheck(){
        if (presentLetter==letter_result){
            correctSound();
            dictation = true;
        }
        else{
            incorrectSound();
            checkedReset();
            dictation = false;

        }
    }

    public void correctSound(){
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(correct, 50, 50, 0, 0, 1);
            }
        });
    }
    public void incorrectSound(){
        m_vibrator.vibrate(1000);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(incorrect, 50, 50, 0, 0, 1);
            }
        });
    }
    public void checkedReset(){
        br1.setChecked(false);
        br2.setChecked(false);
        br3.setChecked(false);
        br4.setChecked(false);
        br5.setChecked(false);
        br6.setChecked(false);
    }

    public void viewBinder(){

        br1 = (CheckBox) findViewById(R.id.br1);
        br1.setOnCheckedChangeListener(this);
        br2 = (CheckBox) findViewById(R.id.br2);
        br2.setOnCheckedChangeListener(this);
        br3 = (CheckBox) findViewById(R.id.br3);
        br3.setOnCheckedChangeListener(this);
        br4 = (CheckBox) findViewById(R.id.br4);
        br4.setOnCheckedChangeListener(this);
        br5 = (CheckBox) findViewById(R.id.br5);
        br5.setOnCheckedChangeListener(this);
        br6 = (CheckBox) findViewById(R.id.br6);
        br6.setOnCheckedChangeListener(this);

        send = (Button)findViewById(R.id.send);
        send.setOnClickListener(this);
        previous = (Button)findViewById(R.id.previous);
        next = (Button)findViewById(R.id.next);
        previous.setVisibility(View.GONE);
        next.setVisibility(View.GONE);
        braille_learning = (LinearLayout)findViewById(R.id.braille_leraning);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_vibrator.cancel();
        soundPool.release();
        TTS.shutdown();
    }
}

