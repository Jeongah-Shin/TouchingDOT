package com.dotincorp.touchingdot;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by wjddk on 2017-03-21.
 */

public class SpecialLetterSwitchingActivity extends Activity implements View.OnClickListener{
    /**
     * 특수 문자를 switching 하면서 닷 워치로 학습하고, dictation 해볼 수 있는 Activity
     */

    TextView show;
    TextView title;
    Button previous;
    Button next;
    Button dictation;
    ImageView dictation_img;

    TextToSpeech TTS;
    Vibrator m_vibrator;
    BleApplication bleApplication;


    int position = 0;
    String presentLetter;

    char[] punctuation = {',',':',':','.','!','(',')','?','*','"','"','\'','-'};
    int[] punctuation_braille = {2,6,18,50,22,54,54,38,20,38,52,4,36};

    int[] numeral = {1,2,3,4,5,6,7,8,9,0};
    String[] numeral_braille = { "01", "03", "09", "19", "11", "0B", "1B", "13", "0A", "1A"};

    String [] special_sign = {"letter","capital","numeral","numercial index","literal","italic"};
    int[] special_sign_braille = {48,32,60,8,24,40};

    @Override
    protected void onResume() {
        super.onResume();
        bleApplication = (BleApplication) getApplication();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.letter_switching);
        title = (TextView)findViewById(R.id.title);
        show = (TextView)findViewById(R.id.show);
        previous = (Button)findViewById(R.id.previous);
        previous.setOnClickListener(this);
        next= (Button)findViewById(R.id.next);
        next.setOnClickListener(this);
        dictation = (Button)findViewById(R.id.dictation);
        dictation_img = (ImageView)findViewById(R.id.dictation_img);
        dictation.setVisibility(View.GONE);
        dictation_img.setVisibility(View.GONE);
        Handler h = new Handler();
        Runnable firstSetRunnable = new Runnable() {
            @Override
            public void run() {
                updateView();
                TTS.speak(show.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            }
        };
        h.postDelayed(firstSetRunnable,3000);
        TTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    TTS.setLanguage(Locale.ENGLISH);
                }
            }
        });
        TTS.speak(show.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
        m_vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void onClick(View v){
        int id = v.getId();
        switch (id){
            case R.id.next:
                position++;
                updateView();
                TTS.speak(show.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.previous:
                position--;
                updateView();
                TTS.speak(show.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                break;

        }
    }
    public void updateView(){
        switch (MainActivity.type){
            case "punctuation":
                title.setText("punctuation");
                if (position>punctuation.length-1){
                    position = 0;
                }else if (position<0){
                    position = 0;
                    m_vibrator.vibrate(1000);
                    Toast alert=Toast.makeText(getApplicationContext(), "cannot move to this way", Toast.LENGTH_SHORT);
                    alert.show();
                }
                show.setText(Character.toString(punctuation[position]));
                bleApplication.sendBraille(Integer.toHexString(punctuation_braille[position])+"000000");
                break;
            case "number":
                title.setText("number");
                if (position>numeral.length-1){
                    position = 0;
                }else if (position<0){
                    position = 0;
                    m_vibrator.vibrate(1000);
                    Toast alert=Toast.makeText(getApplicationContext(), "cannot move to this way", Toast.LENGTH_SHORT);
                    alert.show();
                }
                show.setText(Integer.toString(numeral[position]));
                bleApplication.sendBraille("27"+numeral_braille[position]+"0000");
                break;
            case "special_sign":
                title.setText("special_sign");
                if (position>special_sign.length-1){
                    position = 0;
                }else if (position<0){
                    position = 0;
                    m_vibrator.vibrate(1000);
                    Toast alert=Toast.makeText(getApplicationContext(), "cannot move to this way", Toast.LENGTH_SHORT);
                    alert.show();
                }
                show.setText(special_sign[position]+"\n"+"sign");
                bleApplication.sendBraille(Integer.toHexString(special_sign_braille[position])+"000000");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(TTS !=null){
            TTS.stop();
            TTS.shutdown();
        }
    }


}
