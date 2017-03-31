package com.dotincorp.touchingdot;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Switch;

import com.dotincorp.watchservice.BluetoothLeService;

import java.util.Arrays;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.dotincorp.touchingdot.R.id.braille;


public class BrailleEducationActivity extends MenuActivity implements TextToSpeech.OnInitListener{

    public int connectionStatus = BluetoothLeService.STATE_DISCONNECTED;

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
    public static int interval = 8000;
    private static TimerTask mTask;
    private static Timer mTimer;

    int step = 0;
    boolean t;

    String[] matrix = {"1,2,3","4,5,6","1,4","2,5","3,6"};
    String[] matrix_noti ={"line1","line2","row1","row2","row3"};
    Integer[] braille_send = {1,2,4,8,16,32,1,3,7,15,31,63,7,56,9,18,36};


    @Override
    protected void onResume() {
        super.onResume();
        interval = loadSpeedSetting();
        t = loadTutorialSetting();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.braille_learning);
        ButterKnife.bind(this);

        mTTS = new TextToSpeech(this,this);

        educationStart();


    }

    public void educationStart(){
        Handler h = new Handler();
        Runnable getConnection = new Runnable(){
            public void run(){
                //connect(loadDeviceInfo("address"));
            }
        };

        Runnable educatingAction = new Runnable(){
            public void run(){
                if(t){
                    tutorialPlay();

                }else if(!t){
                    brailleLearning();
                }else{}

            }
        };

        h.postDelayed(getConnection,5000);
        h.postDelayed(educatingAction, 8000);
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

    private boolean loadTutorialSetting(){
        SharedPreferences pref = getSharedPreferences("setting", Activity.MODE_APPEND);
        boolean tutorial = pref.getBoolean("tutorial",true);
        return tutorial;
    }
    private int loadSpeedSetting(){
        SharedPreferences pref = getSharedPreferences("setting", Activity.MODE_APPEND);
        int speed = pref.getInt("speed",8000);
        return speed;
    }


    @OnClick(R.id.setting)
    public void speedSetting(View button){

        //커스텀 뷰를 사용하고 싶으면 popup_window 사용하면 된다.
        //클릭시 팝업 윈도우 생성
        PopupWindow settingPopup = new PopupWindow(button);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //팝업으로 띄울 커스텀뷰를 설정하고
        View view = inflater.inflate(R.layout.setting, null);
        settingPopup.setContentView(view);

        final SeekBar speedSeekbar = (SeekBar) view.findViewById(R.id.speedSeekbar);
        speedSeekbar.setProgress(interval/2000);
        speedSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                interval = progress*2*1000;
                speedSeekbar.setContentDescription(interval/1000+"seconds");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(mTimer!=null) {
                    mTimer.cancel();
                    mTask.cancel();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences pref = getSharedPreferences("setting", Activity.MODE_APPEND);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("speed", interval);
                editor.commit();
            }
        });
        final Switch tutorial = (Switch)view.findViewById(R.id.tutorial);
        tutorial.setChecked(t);
        tutorial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            SharedPreferences pref = getSharedPreferences("setting",Activity.MODE_APPEND);
            SharedPreferences.Editor editor = pref.edit();
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(tutorial.isChecked()){
                    t= true;
                }else{
                    t=false;
                }
                editor.putBoolean("tutorial",t);
                editor.commit();
            }
        });

        //팝업의 크기 설정
        settingPopup.setWindowLayoutMode(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //팝업 뷰 터치 되도록
        settingPopup.setTouchable(true);
        //팝업 뷰 포커스도 주고
        settingPopup.setFocusable(true);
        //팝업 뷰 이외에도 터치되게 (터치시 팝업 닫기 위한 코드)
        settingPopup.setOutsideTouchable(true);
        settingPopup.setBackgroundDrawable(new BitmapDrawable());
        //인자로 넘겨준 v 아래로 보여주기
        settingPopup.showAsDropDown(menu);


    }

    public void tutorialPlay(){
        step=0;
        mTask = new TimerTask(){
            @Override
            public void run(){
                if(step==0){
                    //raiseAll();
                    speakWords= "Dot watch has 4 cells.";
                    onInit(1);
                    step++;
                }else if((1<=step)&&(step<=4)){
                    switch (step){
                        case 1:
                            //sendBraille("3f000000");
                            break;
                        case 2:
                            //sendBraille("003f0000");
                            break;
                        case 3:
                            //sendBraille("00003f00");
                            break;
                        case 4:
                            //sendBraille("0000003f");
                            break;
                    }
                    speakWords = "cell" + step;
                    onInit(1);
                    step++;
                }else if((5<=step)&&(step<=6)){
                    switch (step){
                        case 5:
                            speakWords="Each cell implicates one letter.";
                            break;
                        case 6:
                            speakWords= "And now we will gonna use cell 1 to learn the braille. Now let's begin.";
                            mTimer.cancel();
                            mTask.cancel();
                            break;
                    }
                    onInit(1);
                }
            }
        };
        mTimer = new Timer();
        mTimer.schedule(mTask, 1000, 5000);

    }


    public void brailleLearning(){
        step=0;
        //lowerAll();
        brailleCleanUp();
        mTask = new TimerTask(){
            @Override
            public void run(){
                if ((0<=step)&&(step<=5)){
                    runOnUiThread (new Thread(new Runnable() {
                        public void run() {
                            brailleCleanUp();
                            singleBrailleShow(step);
                        }
                    }));
                   // sendToCell_1();
                    speakWords = "Dot"+"  "+(step+1);
                    onInit(1);
                } else if ((6<=step)&&(step<=11)) {
                    runOnUiThread (new Thread(new Runnable() {
                        public void run() {
                            if (step==6) brailleCleanUp();
                            singleBrailleShow(step-6);
                        }
                    }));
                   // sendToCell_1();
                    int[] stack = new int[step-5];
                    for (int i=1;i<=step-5;i++){
                        stack[i-1] = i;
                    }
                    speakWords = "Dot"+"  "+ Arrays.toString(stack);
                    onInit(1);
                } else if((12<=step)&&(step<=16)){
                    if (step==16){
                        mTimer.cancel();
                        mTask.cancel();
                    }
                    runOnUiThread (new Thread(new Runnable() {
                        public void run() {
                            if (step==12) brailleCleanUp();
                            brailleCleanUp();
                            multipleBrailleShow(matrix[step-12]);
                        }
                    }));
                   // sendToCell_1();
                    speakWords = matrix_noti[step-12];
                    onInit(1);
                    step++;
                }
            }
        };
        mTimer = new Timer();
        mTimer.schedule(mTask, 1000, interval);
    }
    private void singleBrailleShow(int input){
                switch(input+1){
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
                step++;
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

    public void onInit (int status){
        mTTS.setLanguage(Locale.ENGLISH);
        mTTS.speak(speakWords, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuItem brailleMenu = menu.findItem(braille);
        brailleMenu.setEnabled(false);

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.i("test", "onDestroy()");
        if((mTask!=null)&&(mTimer!=null)) {
            mTimer.cancel();
            mTask.cancel();
        }
        mTTS.shutdown();
        super.onDestroy();
    }





}
