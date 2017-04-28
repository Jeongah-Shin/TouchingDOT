package com.dotincorp.touchingdot;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

/**
 * Created by wjddk on 2017-02-09.
 */

public class AlphabetTestMultipleActivity extends Activity implements View.OnClickListener {
    /**
     * 알파벳 테스트를 진행하는 Activity
     * 4개의 각기 다른 알파벳을 랜덤으로 추출해서 닷 워치에 올리고, 해당 알파벳들이 담고 있는 정답 선지를 찾는다.
     * ArrayList<Integer> integerRand - 0-25 범위의 26개 정수 중 4개의 난수를 추출하여 삽입한다. 만약 추출된 정수가 {0,4,5,8} 이라고 한다면 A,E,F,I 가 정답이 된다.
     * ArrayList<String> example_set - 실제 user에게 보여주는 선지 목록. 정답이 A,E,F,I라고 한다면 유사 정답을 배치해서 난이도를 조절한다.
     */
    int correct, incorrect;
    int streamId;
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
    int wrongAnswer = 0;
    String selected;
    String answer;
    ArrayList<Integer> integerRand = new ArrayList<>();
    ArrayList<String> example_set = new ArrayList<>();

    BleApplication bleApplication;
    Vibrator m_vibrator;
    SoundPool soundPool;
    TextToSpeech TTS;

    AlertDialog.Builder choiceBuilder;

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
        viewBinder();

        m_vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        correct = soundPool.load(this, R.raw.correct, 1);
        incorrect = soundPool.load(this, R.raw.incorrect, 1);
        TTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    TTS.setLanguage(Locale.ENGLISH);
                }
            }
        });
        Handler h = new Handler();
        Runnable firstSetRunnable = new Runnable() {
            @Override
            public void run() {
                problemSet();
            }
        };
        h.postDelayed(firstSetRunnable,3000);
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
        answer = "";
        for (int i = 0; i < 26; i++) {
            integerRand.add(i);
        }
        Collections.shuffle(integerRand);
        for (int j = 0; j < 4; j++) {
            answer = answer + Character.toString(alphabet[integerRand.get(j)]);
        }
//        problem_num.setText(answer); 정답이 잘 생성되나 확인하기 위한 코드
        bleApplication.sendMessage(answer.toLowerCase()); //닷워치에 정답 글자 모음을 보낸다.

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
                //TODO 알림창 생성되지 않음.
                getChoiceAlert("Test Result: "+wrongAnswer,"Press [Home] button to go back to home screend and press [Again] button to try once again  ","Home","Again");
            }
            integerRand.clear();
            example_set.clear();
            problemSet();

        }else {
            incorrectSound();
            wrongAnswer++;
            integerRand.clear();
            example_set.clear();
            problemSet();
        }
    }

    public void correctSound() {
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                streamId = soundPool.play(correct, 1, 1, 0, 0, 1);
            }
        });
    }

    public void incorrectSound() {
        m_vibrator.vibrate(1000);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                streamId = soundPool.play(incorrect, 1, 1, 0, 0, 1);
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

    public void viewBinder(){
        // 액티비티 내 타이틀 설정
        title = (TextView)findViewById(R.id.title);
        title.setText("TEST");

        problem_num = (TextView)findViewById(R.id.problem_num);

        _1 = (Button)findViewById(R.id._1);
        _1.setOnClickListener(this);
        _2 = (Button)findViewById(R.id._2);
        _2.setOnClickListener(this);
        _3 = (Button)findViewById(R.id._3);
        _3.setOnClickListener(this);
        _4 = (Button)findViewById(R.id._4);
        _4.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TTS.shutdown();
    }
}
