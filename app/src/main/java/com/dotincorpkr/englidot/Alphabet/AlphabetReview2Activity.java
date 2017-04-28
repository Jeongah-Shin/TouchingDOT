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

import static com.dotincorpkr.englidot.Alphabet.AlphabetReviewInfoActivity.reviewLetter;

/**
 * Created by wjddk on 2017-02-20.
 */

public class AlphabetReview2Activity extends BaseActivity implements TextToSpeech.OnInitListener {

    int sum;
    int position = 0;
    String letter_result;
    Button br_submit;
    //into a hexadecimal form
    CheckBox br1,br2,br3,br4,br5,br6;
    Vibrator m_vibrator;
    SoundPool soundPool;
    TextToSpeech TTS;
    Handler delayHandler;

    int correct;
    int incorrect;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alphabet_braille_insert);

        br1 = (CheckBox) findViewById(R.id.br1);
        br2 = (CheckBox) findViewById(R.id.br2);
        br3 = (CheckBox) findViewById(R.id.br3);
        br4 = (CheckBox) findViewById(R.id.br4);
        br5 = (CheckBox) findViewById(R.id.br5);
        br6 = (CheckBox) findViewById(R.id.br6);
        br_submit = (Button) findViewById(R.id.br_submit);

        m_vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        TTS = new TextToSpeech(this, this);
        delayHandler = new Handler();

        onInit(1);


        br_submit.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                brailleResult();
                Toast show=Toast.makeText(getApplicationContext(), "바이너리:"+Integer.toBinaryString(sum)+"헥스:"+Integer.toHexString(sum)+"문자:"+letter_result, Toast.LENGTH_SHORT);
                show.show();
                if(answerCheck()){
                    position +=1;
                    if(position==reviewLetter.length){
                        TTS.shutdown();
                        position = reviewLetter.length-1;
                        Intent intent = new Intent(getApplicationContext(), ReviewCompleteActivity.class);
                        startActivity(intent);
                        finish();

                    }
                    delayHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onInit(1);
                        }
                    }, 1500);
                    checkedReset();
                }
                else if(!answerCheck()){
                    delayHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onInit(1);
                        }
                    }, 1500);
                    checkedReset();
                }
            }
        });


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
        letter_result = Character.toString(ASCIIcharacter.brailleResult(sum)).toUpperCase();


    }

    public boolean answerCheck(){
        if (letter_result.matches(Character.toString(reviewLetter[position]))){
            correctSound();
            return true;
        }
        else{
            incorrectSound();
            return false;
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

    public void onInit (int status){

        TTS.speak(String.valueOf(reviewLetter[position]), TextToSpeech.QUEUE_FLUSH, null);

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