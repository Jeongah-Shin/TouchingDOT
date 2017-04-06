package com.dotincorp.touchingdot;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by wjddk on 2017-01-23.
 */

public class BrailleInsertActivity extends Activity implements View.OnClickListener {

    //액티비티 구성 요소
    CheckBox br1;
    CheckBox br2;
    CheckBox br3;
    CheckBox br4;
    CheckBox br5;
    CheckBox br6;
    Button send;
    LinearLayout braille_learning;

    int sum;
    public static boolean dictation = false;
    char letter_result;
    int correct,incorrect;

    static public char presentLetter;
    Vibrator m_vibrator;
    SoundPool soundPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("점자를 입력해주세요.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.braille_grid);

        br1 = (CheckBox)findViewById(R.id.br1);
        br2 = (CheckBox)findViewById(R.id.br2);
        br3 = (CheckBox)findViewById(R.id.br3);
        br4 = (CheckBox)findViewById(R.id.br4);
        br5 = (CheckBox)findViewById(R.id.br5);
        br6 = (CheckBox)findViewById(R.id.br6);
        send = (Button)findViewById(R.id.send);
        send.setOnClickListener(this);
        braille_learning = (LinearLayout)findViewById(R.id.braille_leraning);

        m_vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC,0);

    }
    public void onClick(View v){
        int id = v.getId();
        if (id ==R.id.send){
            brailleResult();
            Toast show=Toast.makeText(getApplicationContext(), "바이너리:"+Integer.toBinaryString(sum)+"헥스:"+Integer.toHexString(sum)+"문자:"+letter_result, Toast.LENGTH_SHORT);
            show.show();
            answerCheck();
            if (dictation){
                finish();
            }
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_vibrator.cancel();
        soundPool.release();
    }
}

