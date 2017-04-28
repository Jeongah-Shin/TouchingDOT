package com.dotincorpkr.englidot.Alphabet;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.dotincorpkr.englidot.ASCIIcharacter;
import com.dotincorpkr.englidot.BaseActivity;
import com.dotincorpkr.englidot.R;

import java.util.ArrayList;
import java.util.Random;

import static com.dotincorpkr.englidot.Alphabet.AlphabetTestMultipleActivity.alphabet;

/**
 * Created by wjddk on 2017-02-09.
 */

public class AlphabetTestShortAnswerActivity extends BaseActivity implements TextToSpeech.OnInitListener{
    static ArrayList<Character> shortAnswerWrong = new ArrayList<>();
    public static int shortWrongAnswer_length =0;
    int sum;
    int trial = 1;
    //char tv[] = new char[5];
    int[] randomValue = new int[6];
    int i=0;
    char letter_result;
    char testValue;

    private Handler delayHandler;

    Button br_submit;
    CheckBox br1, br2, br3, br4, br5, br6;
    Toast show;
    Vibrator m_vibrator;
    SoundPool soundPool;
    TextToSpeech TTS;
    int correct;
    int incorrect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.alphabet_braille_insert);

        br1 = (CheckBox) findViewById(R.id.br1);
        br2 = (CheckBox) findViewById(R.id.br2);
        br3 = (CheckBox) findViewById(R.id.br3);
        br4 = (CheckBox) findViewById(R.id.br4);
        br5 = (CheckBox) findViewById(R.id.br5);
        br6 = (CheckBox) findViewById(R.id.br6);
        br_submit = (Button) findViewById(R.id.br_submit);

        m_vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        TTS = new TextToSpeech(AlphabetTestShortAnswerActivity.this, (TextToSpeech.OnInitListener)this);


        delayHandler = new Handler();

        br_submit.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                brailleResult();
                answerCheck();
            }
        });
        problemSet();

    }

    public void brailleResult() {
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
        letter_result = ASCIIcharacter.brailleResult(sum);
        show.makeText(getApplicationContext(), letter_result+"", Toast.LENGTH_SHORT).show(); //내가 썼던 답안을 와치에 송출하는 과정. null 값이라면 모든 점자를 올린다.

    }

    public void answerCheck() {
        switch (trial) {
            case 1:
                if (Character.toUpperCase(letter_result)==testValue){
                    correctSound();
                    delayHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            i++;
                            finalCheck();
                            problemSet();
                        }
                    }, 2000);
                    break;
                    //다음 문제 불러옴
                } else {
                    incorrectSound();
                    TTS.speak("Try once again", TextToSpeech.QUEUE_FLUSH, null);
                    trial++;
                    delayHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onInit(1);
                        }
                    }, 1000);
                    break;
                    //똑같은 문자 다시 송출
                }
            case 2:
                if (Character.toUpperCase(letter_result)==testValue){
                    correctSound();
                    delayHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            i++;
                            finalCheck();
                            problemSet();
                        }
                    }, 2000);
                    break;
                } else {
                    incorrectSound();
                    shortWrongAnswer_length++;
                    shortAnswerWrong.add(testValue);
                    delayHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            i++;
                            finalCheck();
                            problemSet();
                        }
                    }, 1000);
                    break;
                }
        }
        checkedReset();
    }

    public void problemSet(){
        Random example_generator = new Random();
        randomValue[i] = example_generator.nextInt(25); // 0-25 까지
        if (i!=0){
            if(randomValue[i]==randomValue[i-1]) {
                randomValue[i] = example_generator.nextInt(25);
            }
        }else{
            //alphabet[randomValue[i]] = alphabet[randomValue[i]];
        }
        testValue = alphabet[randomValue[i]];
        onInit(1);
    }

    @Override
    public void onInit(int status) {
        TTS.speak((i+1)+"번 문제"+"    "+testValue, TextToSpeech.QUEUE_FLUSH, null);
    }
    public void correctSound(){
        correct = soundPool.load(this, R.raw.correct, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(correct, 50, 50, 0, 0, 1);
            }
        });
    }
    public void incorrectSound(){
        m_vibrator.vibrate(1000);
        incorrect = soundPool.load(this, R.raw.incorrect, 1);
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
    public void finalCheck(){
        if (i==5){
            finish();
            Intent intent = new Intent(getApplicationContext(), ShortTestResultActivity.class);
            startActivity(intent);
            super.onStop();
        }
    }

}
