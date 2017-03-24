package com.dotincorp.touchingdot.Alphabet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dotincorp.touchingdot.R;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wjddk on 2017-02-20.
 */

public class AlphabetReviewActivity extends Activity implements TextToSpeech.OnInitListener{
//ImgBtn - previous, next
//TV - current
    int position = 0;
    AlertDialog.Builder builder;

    public static char[] reviewLetter;

    TextToSpeech TTS;
    Vibrator m_vibrator;

    @Bind(R.id.show)
    TextView show;
    @Bind(R.id.previous)
    ImageButton previous;
    @Bind(R.id.next)
    ImageButton next;
    @Bind(R.id.write)
    Button write;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.letter_switching);
        ButterKnife.bind(this, this);

        TTS =new TextToSpeech(this,this);
        TTS.setLanguage(Locale.ENGLISH);
        m_vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        show.setText(Character.toString(reviewLetter[position]));

        builder = new AlertDialog.Builder(this);
        builder.setTitle("Review Complete")        // 제목 설정
                .setMessage("If you would like to review this course again, press [Review Again] button.\n" +
                        "Otherwise, press [Home] button to go back to Alphabet Learning page.")        // 메세지 설정
                .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                .setPositiveButton("Review Again", new DialogInterface.OnClickListener(){
                    // 오른쪽 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton){
                        position = 0;
                        show.setText(Character.toString(reviewLetter[position]));
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Home", new DialogInterface.OnClickListener(){
                    // 왼쪽 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton){
                        dialog.cancel();
                        Intent homeIntent = new Intent(getApplicationContext(), AlphabetSwitchingActivity.class);
                        startActivity(homeIntent);
                        finish();
                    }
                });

    }
    @OnClick({R.id.previous,R.id.next})
    public void onChangeClick(View v){
        switch(v.getId()){
            case R.id.previous:
                position -= 1;
                break;
            case R.id.next:
                position +=1;
                break;
        }
        overCheck();
        onInit(1);
        show.setText(Character.toString(reviewLetter[position]));
    }

    public void overCheck(){

        if (position == reviewLetter.length){
            position = reviewLetter.length-1;

            AlertDialog dialog = builder.create();    // 알림창 객체 생성
            dialog.show();    // 알림창 띄우기
        }
        else if (position<0){
            position = 0;
            m_vibrator.vibrate(1000);
            Toast show=Toast.makeText(this, "cannot move to this way", Toast.LENGTH_SHORT);
            show.show();
        }

    }

    public void onInit (int status){
        TTS.speak(reviewLetter[position]+"", TextToSpeech.QUEUE_FLUSH, null);
    }
}
