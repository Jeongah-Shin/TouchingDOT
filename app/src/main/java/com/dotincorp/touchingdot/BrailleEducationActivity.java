package com.dotincorp.touchingdot;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.Arrays;
import java.util.Locale;


public class BrailleEducationActivity extends Activity implements View.OnClickListener{
    CheckBox br1;
    CheckBox br2;
    CheckBox br3;
    CheckBox br4;
    CheckBox br5;
    CheckBox br6;
    ImageButton send_image;
    Button send;
    Button previous;
    Button next;
    LinearLayout braille_grid;

    AlertDialog.Builder choiceBuilder;

    TextToSpeech TTS;
    String speakWords;
    BleApplication bleApplication;

    int step;

    String[] matrix = {"1,2,3","4,5,6","1,4","2,5","3,6"};
    String[] matrix_noti ={"line1","line2","row1","row2","row3"};
    Integer[] braille_send = {1,2,4,8,16,32,1,3,7,15,31,63,7,56,9,18,36};


    @Override
    protected void onResume() {
        super.onResume();

        bleApplication = (BleApplication) getApplication();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.braille_grid);
        viewBind();
        TTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    TTS.setLanguage(Locale.ENGLISH);
                }
            }
        });

        Handler h = new Handler();
        Runnable educationStart = new Runnable(){
            public void run(){
                step = 0;
                brailleLearning();
            }
        };
        h.postDelayed(educationStart,3000);

    }

    public void onClick(View view){
        int id = view.getId();
        switch (id){
            case R.id.previous:
                step = step-1;
                break;
            case R.id.next:
                step = step+1;
                break;
        }
        brailleLearning();
    }
    private String loadDeviceInfo(String index){
        SharedPreferences pref = getSharedPreferences("device", Activity.MODE_APPEND);
        if(index=="address"){
            String mac = pref.getString("mac","0");
            return mac;
        }else if(index == "name"){
            String device = pref.getString("name","device");
            return device;
        }else{
            return "null";
        }
    }
    public void brailleLearning(){
        if ((0<=step)&&(step<=5)){
            brailleCleanUp();
            singleBrailleShow(step+1);
            sendBraille(Integer.toHexString(braille_send[step]));
            TTS.speak("Dot"+"  "+(step+1), TextToSpeech.QUEUE_FLUSH, null);
        } else if ((6<=step)&&(step<=11)) {
            if (step==6) brailleCleanUp();
            singleBrailleShow(step-5);
            sendBraille(Integer.toHexString(braille_send[step]));
            int[] stack = new int[step-5];
            for (int i=1;i<=step-5;i++){
                stack[i-1] = i;
            }
            TTS.speak("Dot"+"  "+ Arrays.toString(stack), TextToSpeech.QUEUE_FLUSH, null);
        } else if((12<=step)&&(step<=16)){
            brailleCleanUp();
            multipleBrailleShow(matrix[step-12]);
             sendBraille(Integer.toHexString(braille_send[step]));
            TTS.speak(matrix_noti[step-12], TextToSpeech.QUEUE_FLUSH, null);
        }else{
            step = 0;
            brailleLearning();
        }

    }
    private void singleBrailleShow(int input){
                switch(input){
                    case 1:
                        br1.setChecked(true);
                        break;
                    case 2:
                        br2.setChecked(true);
                        break;
                    case 3:
                        br3.setChecked(true);
                        break;
                    case 4:
                        br4.setChecked(true);
                        break;
                    case 5:
                        br5.setChecked(true);
                        break;
                    case 6:
                        br6.setChecked(true);
                        break;
                }
    }
    private void multipleBrailleShow(String input){
        String input_string[] = input.split(",");
        for (int j =0; j<input_string.length;j++){
            switch(Integer.parseInt(input_string[j])){
                case 1:
                    br1.setChecked(true);
                    break;
                case 2:
                    br2.setChecked(true);
                    break;
                case 3:
                    br3.setChecked(true);
                    break;
                case 4:
                    br4.setChecked(true);
                    break;
                case 5:
                    br5.setChecked(true);
                    break;
                case 6:
                    br6.setChecked(true);
                    break;
            }
        }
    }
    public void brailleCleanUp(){
        br1.setChecked(false);
        br2.setChecked(false);
        br3.setChecked(false);
        br4.setChecked(false);
        br5.setChecked(false);
        br6.setChecked(false);
    }

    public void sendBraille(String Hex){
        if (Hex.length() == 1) {
            bleApplication.sendBraille("0" + Hex + "000000");
        } else {
            bleApplication.sendBraille(Hex + "000000");
        }
    }

    public AlertDialog.Builder getChoiceAlert(String title, String content, String positive, String negative) {

        choiceBuilder = new AlertDialog.Builder(this);
        choiceBuilder.setTitle(title)        // 제목 설정
                .setMessage(content)        // 콘텐츠 설정
                .setCancelable(false)        // 뒤로 버튼 눌러도 취소되지 않음.
                .setPositiveButton(positive, new DialogInterface.OnClickListener() {
                    // 오른쪽 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton) {
                        step = 0;
                        brailleLearning();
                    }
                })
                .setNegativeButton(negative, new DialogInterface.OnClickListener() {
                    // 왼쪽 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                    }
                });

        return choiceBuilder;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.i("test", "onDestroy()");
        TTS.shutdown();
        super.onDestroy();
    }

    protected void viewBind(){

        br1 = (CheckBox)findViewById(R.id.br1);
        br1.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        br1.setClickable(false);
        br1.setFocusable(false);
        br2 = (CheckBox)findViewById(R.id.br2);
        br2.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        br2.setClickable(false);
        br2.setFocusable(false);
        br3 = (CheckBox)findViewById(R.id.br3);
        br3.setClickable(false);
        br3.setFocusable(false);
        br3.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        br4 = (CheckBox)findViewById(R.id.br4);
        br4.setClickable(false);
        br4.setFocusable(false);
        br4.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        br5 = (CheckBox)findViewById(R.id.br5);
        br5.setClickable(false);
        br5.setFocusable(false);
        br5.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        br6 = (CheckBox)findViewById(R.id.br6);
        br6.setClickable(false);
        br6.setFocusable(false);
        br6.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);

        send = (Button)findViewById(R.id.send);
        send.setVisibility(View.GONE);
        send.setOnClickListener(this);

        send_image = (ImageButton)findViewById(R.id.send_image);
        send_image.setVisibility(View.GONE);

        previous = (Button)findViewById(R.id.previous);
        previous.setVisibility(View.VISIBLE);
        previous.setOnClickListener(this);

        next = (Button)findViewById(R.id.next);
        next.setVisibility(View.VISIBLE);
        next.setOnClickListener(this);

        braille_grid = (LinearLayout)findViewById(R.id.braille_gird);
        braille_grid.setClickable(false);
        braille_grid.setFocusable(false);
    }





}
