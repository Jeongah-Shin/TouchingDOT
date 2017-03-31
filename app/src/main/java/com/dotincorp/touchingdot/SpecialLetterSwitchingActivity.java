package com.dotincorp.touchingdot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wjddk on 2017-03-21.
 */

public class SpecialLetterSwitchingActivity extends Activity implements TextToSpeech.OnInitListener {

    @Bind(R.id.show)
    TextView show;
    @Bind(R.id.previous)
    ImageButton previous;
    @Bind(R.id.next)
    ImageButton next;
    @Bind(R.id.write)
    Button write;

    TextToSpeech mTTS;
    Vibrator m_vibrator;
    String speakWords;
    
    String type;

    int position = 0;
    String presentLetter;

    char[] punctuation = {',',':',':','.','!','(',')','?','*','"','"','\'','-'};
    int[] numeral = {1,2,3,4,5,6,7,8,9,0};

    String [] special_sign = {"letter","capital","numeral","numercial index","literal","italic"};
    int[] special_sign_braille = {48,32,60,8,24,40};

    @Override
    protected void onResume() {
        super.onResume();
        Intent inputIntent = new Intent();
        type = inputIntent.getExtras().getString("type");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.letter_switching);
        ButterKnife.bind(this);

        updateView();
        speakWords = show.getText().toString();
        onInit(1);

        m_vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        mTTS = new TextToSpeech(this,this);

    }
    @OnClick(R.id.next)
    public void nextBtnClicked(){
        position++;
        updateView();
        speakWords = show.getText().toString();
        onInit(1);
    }
    @OnClick(R.id.previous)
    public void previousBtnClicked(){
        position--;
        updateView();
        speakWords = show.getText().toString();
        onInit(1);
    }
    @OnClick(R.id.write)
    public void writeBtnClicked(){
        presentLetter = show.getText().toString();
        Intent writeIntent = new Intent(getApplicationContext(),BrailleInsertActivity.class);
        startActivity(writeIntent);

    }
    public void updateView(){
        switch (type){
            case "punctuation":
                if (position>punctuation.length-1){
                    position = 0;
                }else if (position<0){
                    position = 0;
                    m_vibrator.vibrate(1000);
                    Toast alert=Toast.makeText(getApplicationContext(), "cannot move to this way", Toast.LENGTH_SHORT);
                    alert.show();
                }
                show.setText(Character.toString(punctuation[position]));
                break;
            case "number":
                if (position>numeral.length-1){
                    position = 0;
                }else if (position<0){
                    position = 0;
                    m_vibrator.vibrate(1000);
                    Toast alert=Toast.makeText(getApplicationContext(), "cannot move to this way", Toast.LENGTH_SHORT);
                    alert.show();
                }
                show.setText(Integer.toString(numeral[position]));
                break;
            case "special_sign":
                if (position>special_sign.length-1){
                    position = 0;
                }else if (position<0){
                    position = 0;
                    m_vibrator.vibrate(1000);
                    Toast alert=Toast.makeText(getApplicationContext(), "cannot move to this way", Toast.LENGTH_SHORT);
                    alert.show();
                }
                show.setText(special_sign[position]+"\n"+"sign");
                break;
        }
    }

    public void onInit (int status){
        mTTS.setLanguage(Locale.ENGLISH);
        mTTS.speak(speakWords, TextToSpeech.QUEUE_FLUSH, null);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mTTS !=null){
            mTTS.stop();
            mTTS.shutdown();
        }
    }


}
