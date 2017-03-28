package com.dotincorp.touchingdot.Alphabet;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dotincorp.touchingdot.ASCIIcharacter;
import com.dotincorp.touchingdot.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wjddk on 2017-01-23.
 */

public class BrailleInsertActivity extends Activity {
    //액티비티 구성 요소
    @Bind(R.id.br1)
    CheckBox br1;
    @Bind(R.id.br2)
    CheckBox br2;
    @Bind(R.id.br3)
    CheckBox br3;
    @Bind(R.id.br4)
    CheckBox br4;
    @Bind(R.id.br5)
    CheckBox br5;
    @Bind(R.id.br6)
    CheckBox br6;
    @Bind(R.id.menu)
    Button menu;
    @Bind(R.id.setting)
    ImageButton setting;
    @Bind(R.id.braille_leraning)
    LinearLayout activity_main;

    int sum;
    static boolean dictation = false;
    char letter_result;
    int correct,incorrect;

    static public char presentAlphabet;
    Vibrator m_vibrator;
    SoundPool soundPool;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("점자를 입력해주세요.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.braille_learning);
        ButterKnife.bind(this);
        modifyView();

        m_vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC,0);

    }

    @OnClick(R.id.setting)
    public void submitAction(){
        brailleResult();
        Toast show=Toast.makeText(getApplicationContext(), "바이너리:"+Integer.toBinaryString(sum)+"헥스:"+Integer.toHexString(sum)+"문자:"+letter_result, Toast.LENGTH_SHORT);
        show.show();
        answerCheck();
        if (dictation){
            finish();
        }
    }

    public void modifyView(){
        menu.setVisibility(View.INVISIBLE);
        setting.setBackgroundResource(R.drawable.send);
        setting.setContentDescription("Submition button");
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
        if (presentAlphabet==letter_result){
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

}

