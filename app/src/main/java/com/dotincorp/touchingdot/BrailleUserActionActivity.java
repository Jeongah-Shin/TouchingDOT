package com.dotincorp.touchingdot.Braille;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.dotincorp.touchingdot.R;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import static com.dotincorp.touchingdot.BluetoothScanning.MainActivity.bluetoothService;

/**
 * Created by wjddk on 2017-03-21.
 */

public class BrailleUserActionActivity extends Activity {

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
    LinearLayout braille_learning;

    TextToSpeech mTTS;
    String speakWords;

    int sum;
    String letter_result;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.braille_learning);
        ButterKnife.bind(this);

        setting.setContentDescription("braille submition button");
        setting.setBackgroundResource(R.drawable.send);

    }
    @OnClick (R.id.setting)
    public void sendBtnClicked(){
        if (Integer.toHexString(sum).length()==1){
            sendBraille("0"+Integer.toHexString(brailleResult())+"000000");
        }else{
            sendBraille(Integer.toHexString(brailleResult())+"000000");
        }

    }

    public int brailleResult(){
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

    @OnCheckedChanged({R.id.br1, R.id.br2, R.id.br3, R.id.br4, R.id.br5, R.id.br6})
    public void checkedChanged(CompoundButton b){
            switch (b.getId()) {
                case R.id.br1:
                    if (br1.isChecked()){
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
        onInit(1);
    }

    private void sendBraille(String brailleHex) {
        if (bluetoothService != null) {
            bluetoothService.sendBrailleHex(brailleHex);
        }
    }

    public void onInit (int status){
        mTTS.setLanguage(Locale.ENGLISH);
        mTTS.speak(speakWords, TextToSpeech.QUEUE_FLUSH, null);
    }
}
