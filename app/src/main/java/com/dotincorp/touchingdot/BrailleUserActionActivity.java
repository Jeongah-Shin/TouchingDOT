package com.dotincorp.touchingdot;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import java.util.Locale;

/**
 * Created by wjddk on 2017-03-21.
 */

public class BrailleUserActionActivity extends Activity implements View.OnClickListener, RadioButton.OnCheckedChangeListener {

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
    String speakWords;

    int sum;
    BleApplication bleApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.braille_grid);
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

        send = (Button) findViewById(R.id.send);
        send.setOnClickListener(this);

        previous = (Button)findViewById(R.id.previous);
        previous.setVisibility(View.GONE);
        next = (Button)findViewById(R.id.next);
        next.setVisibility(View.GONE);
        braille_learning = (LinearLayout) findViewById(R.id.braille_leraning);

        TTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    TTS.setLanguage(Locale.ENGLISH);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        bleApplication = (BleApplication) getApplication();
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.send) {
            if (Integer.toHexString(brailleResult()).length() == 1) {
                bleApplication.sendBraille("0" + Integer.toHexString(brailleResult()) + "000000");
            } else {
                bleApplication.sendBraille(Integer.toHexString(brailleResult()) + "000000");
            }
        }
    }

    public int brailleResult() {
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
        return sum;

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        TTS.shutdown();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.br1:
                if (br1.isChecked()) {
                    speakWords = "dot 1 checked";
                } else speakWords = "dot 1 canceled";
                break;
            case R.id.br2:
                if (br2.isChecked()) speakWords = "dot 2 checked";
                else speakWords = "dot 2 cnaceled";
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
}
