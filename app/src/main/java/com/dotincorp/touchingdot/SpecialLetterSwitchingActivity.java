package com.dotincorp.touchingdot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by wjddk on 2017-03-21.
 */

public class SpecialLetterSwitchingActivity extends Activity implements View.OnClickListener{

    TextView show;
    Button previous;
    Button next;
    Button dictation;

    TextToSpeech TTS;
    Vibrator m_vibrator;
    BleApplication bleApplication;
    
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
        bleApplication = (BleApplication) getApplication();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.letter_switching);
        show = (TextView)findViewById(R.id.show);
        previous = (Button)findViewById(R.id.previous);
        previous.setOnClickListener(this);
        next= (Button)findViewById(R.id.next);
        next.setOnClickListener(this);
        dictation = (Button)findViewById(R.id.dictation);
        dictation.setOnClickListener(this);

        updateView();

        TTS.speak(show.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);

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
            case R.id.dictation:
                presentLetter = show.getText().toString();
                Intent writeIntent = new Intent(getApplicationContext(),BrailleInsertActivity.class);
                startActivity(writeIntent);
                break;

        }
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
                bleApplication.sendMessage(Character.toString(punctuation[position]));
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
                bleApplication.sendMessage(Integer.toString(numeral[position]));
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
                bleApplication.sendMessage(special_sign[position]);
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
