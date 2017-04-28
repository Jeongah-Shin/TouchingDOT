package com.dotincorpkr.englidot.Alphabet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dotincorpkr.englidot.BaseActivity;
import com.dotincorpkr.englidot.R;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.dotincorpkr.englidot.Alphabet.AlphabetReviewInfoActivity.reviewLetter;

/**
 * Created by wjddk on 2017-02-20.
 */

public class AlphabetReview1Activity extends BaseActivity implements TextToSpeech.OnInitListener{
//ImgBtn - previous, next
//TV - current
    int position = 0;
    AlertDialog.Builder builder;

    TextToSpeech TTS;
    Vibrator m_vibrator;

    @Bind(R.id.current)
    TextView current;

    @Bind(R.id.previous)
    ImageButton previous;

    @Bind(R.id.next)
    ImageButton next;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alphabet_review1_page);
        ButterKnife.bind(this, this);

        TTS =new TextToSpeech(this,this);
        TTS.setLanguage(Locale.ENGLISH);
        m_vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        current.setText(Character.toString(reviewLetter[position]));

        builder = new AlertDialog.Builder(this);
        builder.setTitle("복습 1단계 완료")        // 제목 설정
                .setMessage("1단계를 다시 학습하려면 [다시 공부], 따라쓰기 복습으로 가려면 [따라쓰기] 버튼을 눌러주세요")        // 메세지 설정
                .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                .setPositiveButton("따라쓰기", new DialogInterface.OnClickListener(){
                    // 오른쪽 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton){
                        dialog.cancel();
                        goToN();
                    }
                })
                .setNegativeButton("다시공부", new DialogInterface.OnClickListener(){
                    // 왼쪽 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton){
                        position = 0;
                        current.setText(Character.toString(reviewLetter[position]));
                        dialog.cancel();
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
        current.setText(Character.toString(reviewLetter[position]));
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
    public void goToN(){
        Intent dIntent = new Intent(AlphabetReview1Activity.this, AlphabetReview2Activity.class);
        startActivity(dIntent);
        finish();
    }

    public void onInit (int status){
        TTS.speak(reviewLetter[position]+"", TextToSpeech.QUEUE_FLUSH, null);
    }
}
