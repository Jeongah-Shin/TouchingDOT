package com.dotincorp.touchingdot;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;

/**
 * Created by wjddk on 2017-02-09.
 */

public class AlphabetTestMultipleActivity extends Activity implements View.OnClickListener {

    int correct, incorrect;
    static char[] alphabet = {
            'A', 'B', 'C', 'D',
            'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X',
            'Y', 'Z'
    };
    int p_num = 1;
    int step = 0;
    int wrongAnswer = 0;
    Random example_generator = new Random();
    ArrayList<Integer> integerRand = new ArrayList<>();
    ArrayList<Character> answer = new ArrayList<>();
    ArrayList<String> example_set = new ArrayList<>();
    BleApplication bleApplication;
    Vibrator m_vibrator;
    SoundPool soundPool;
    TextToSpeech TTS;

    AlertDialog.Builder choiceBuilder;

    Timer mTimer;
    TimerTask mTask;

    String selected;

    TextView title;
    TextView problem_num;
    Button _1;
    Button _2;
    Button _3;
    Button _4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alphabet_test);
        ButterKnife.bind(this);

        title = (TextView)findViewById(R.id.title);
        problem_num = (TextView)findViewById(R.id.problem_num);
        _1 = (Button)findViewById(R.id._1);
        _1.setOnClickListener(this);
        _2 = (Button)findViewById(R.id._2);
        _2.setOnClickListener(this);
        _3 = (Button)findViewById(R.id._3);
        _3.setOnClickListener(this);
        _4 = (Button)findViewById(R.id._4);
        _4.setOnClickListener(this);

        m_vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        TTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    TTS.setLanguage(Locale.ENGLISH);
                }
            }
        });

        problemSet();
    }

    @Override
    protected void onResume() {
        bleApplication = (BleApplication) getApplication();
        super.onResume();
    }

    public void onClick(View v){
        switch (v.getId()) {
            case R.id._1:
                selected = _1.getText().toString();
                break;
            case R.id._2:
                selected = _2.getText().toString();
                break;
            case R.id._3:
                selected = _3.getText().toString();
                break;
            case R.id._4:
                selected = _4.getText().toString();
                break;
            default:
                break;
        }
        checkAnswer();
    }

    public void problemSet() {
        problem_num.setText("Problem" + " " + p_num);
        TTS.speak("Problem" + " " + p_num, TextToSpeech.QUEUE_FLUSH, null);

        for (int i = 0; i < 26; i++) {
            integerRand.add(i);
        }
        Collections.shuffle(integerRand);
        for (int j = 0; j < 4; j++) {
            answer.add(alphabet[integerRand.get(j)]);
        }
        bleApplication.sendMessage(answer.toString());

        /*정답*/
        example_set.add(alphabet[integerRand.get(0)] + "\n" + alphabet[integerRand.get(1)] + "\n" + alphabet[integerRand.get(2)] + "\n" + alphabet[integerRand.get(3)]);
        /*유사*/
        example_set.add(alphabet[integerRand.get(0)] + "\n" + alphabet[integerRand.get(1)] + "\n" + alphabet[integerRand.get(2)] + "\n" + alphabet[integerRand.get(25)]);
        /*조금 유사*/
        example_set.add(alphabet[integerRand.get(0)] + "\n" + alphabet[integerRand.get(1)] + "\n" + alphabet[integerRand.get(24)] + "\n" + alphabet[integerRand.get(25)]);
        /*다소 유사*/
        example_set.add(alphabet[integerRand.get(0)] + "\n" + alphabet[integerRand.get(23)] + "\n" + alphabet[integerRand.get(24)] + "\n" + alphabet[integerRand.get(25)]);

        Collections.shuffle(example_set);
        _1.setText(example_set.get(0));
        _2.setText(example_set.get(1));
        _3.setText(example_set.get(2));
        _4.setText(example_set.get(3));
    }

    public void checkAnswer() {
        String answer = alphabet[integerRand.get(0)] + "\n" + alphabet[integerRand.get(1)] + "\n" + alphabet[integerRand.get(2)] + "\n" + alphabet[integerRand.get(3)];
        if(answer.matches(selected)){
            correctSound();
            p_num++;
            if(p_num==11){
                problem_num.setText("Complete");
                getChoiceAlert("Test Result: "+wrongAnswer,"Press [Home] button to go back to home screend and press [Again] button to try once again  ","Home","Again");
            }
            problemSet();

        }else {
            incorrectSound();
            wrongAnswer++;
            mTask = new TimerTask(){
                @Override
                public void run(){
                    switch(step){
                        case 0:
                            TTS.speak(Character.toString(alphabet[integerRand.get(step)]), TextToSpeech.QUEUE_FLUSH, null);
                            bleApplication.sendMessage(Character.toString(alphabet[integerRand.get(0)]));
                            break;
                        case 1:
                            TTS.speak(Character.toString(alphabet[integerRand.get(step)]), TextToSpeech.QUEUE_FLUSH, null);
                            bleApplication.sendMessage(Character.toString(alphabet[integerRand.get(0)])+Character.toString(alphabet[integerRand.get(1)]));
                            break;
                        case 2:
                            TTS.speak(Character.toString(alphabet[integerRand.get(step)]), TextToSpeech.QUEUE_FLUSH, null);
                            bleApplication.sendMessage(Character.toString(alphabet[integerRand.get(0)])+Character.toString(alphabet[integerRand.get(1)])+Character.toString(alphabet[integerRand.get(2)]));
                            break;
                        case 3:
                            TTS.speak(Character.toString(alphabet[integerRand.get(step)]), TextToSpeech.QUEUE_FLUSH, null);
                            bleApplication.sendMessage(Character.toString(alphabet[integerRand.get(0)])+Character.toString(alphabet[integerRand.get(1)])+Character.toString(alphabet[integerRand.get(2)])
                                    +Character.toString(alphabet[integerRand.get(3)]));
                            break;
                        case 4:
                            problemSet();
                            mTimer.cancel();
                            mTask.cancel();
                            break;
                    }
                }
            };
            mTimer = new Timer();
            mTimer.schedule(mTask, 1000, 2000);
        }
    }

    public void correctSound() {
        correct = soundPool.load(this, correct, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(correct, 50, 50, 0, 0, 1);
            }
        });
    }

    public void incorrectSound() {
        m_vibrator.vibrate(1000);
        incorrect = soundPool.load(this, incorrect, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(incorrect, 50, 50, 0, 0, 1);
            }
        });
    }

    public AlertDialog.Builder getChoiceAlert(String title, String content, String positive, String negative) {

        choiceBuilder = new AlertDialog.Builder(this);
        choiceBuilder.setTitle(title)        // 제목 설정
                .setMessage(content)        // 콘텐츠 설정
                .setCancelable(false)        // 뒤로 버튼 눌러도 취소되지 않음.
                .setPositiveButton(positive, new DialogInterface.OnClickListener() {
                    // 오른쪽 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                    }
                })
                .setNegativeButton(negative, new DialogInterface.OnClickListener() {
                    // 왼쪽 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton) {
                        p_num = 1;
                        dialog.cancel();
                    }
                });

        return choiceBuilder;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TTS.shutdown();
    }
}
