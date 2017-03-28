package com.dotincorp.touchingdot.Alphabet;

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

import com.dotincorp.touchingdot.MenuActivity;
import com.dotincorp.touchingdot.R;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.dotincorp.touchingdot.Alphabet.BrailleInsertActivity.dictation;
import static com.dotincorp.touchingdot.Alphabet.BrailleInsertActivity.presentAlphabet;

/**
 * Created by wjddk on 2017-02-13.
 */

public class AlphabetSwitchingActivity extends MenuActivity {
    // TV - show
    // ImgBtn - previous, next
    // Btn - write

    @Bind(R.id.show)
    TextView show;
    @Bind(R.id.previous)
    ImageButton previous;
    @Bind(R.id.next)
    ImageButton next;
    @Bind(R.id.write)
    Button write;

    Vibrator m_vibrator;
    TextToSpeech TTS;
    AlertDialog.Builder builder;

    static int position = 0;
    int[] min_num = {0, 9, 18};
    int[] max_num = {8, 17, 25};
    int k = 0;
    int stage=1;
    boolean stageClear = false;
    char[] alphabet = {
            'A', 'B', 'C', 'D',
            'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X',
            'Y', 'Z'
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.letter_switching);
        ButterKnife.bind(this);

        m_vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        TTS =new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    TTS.setLanguage(Locale.ENGLISH);
                    TTS.speak(String.valueOf(alphabet[position]), TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

        builder = new AlertDialog.Builder(this);
        // 여기서 부터는 알림창의 속성 설정
        builder.setTitle("STAGE CLEAR")        // 제목 설정
                .setMessage("학습을 완료하였습니다. 전 단계를 다시 학습하려면 [다시 공부], 다음 단계로 가려면 [다음 단계] 버튼을 눌러주세요")        // 메세지 설정
                .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                .setPositiveButton("다음 단계", new DialogInterface.OnClickListener(){
                    // 오른쪽 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton){
                        if (stage>2){
                            Intent tIntent = new Intent(getApplicationContext(),AlphabetTestMultipleActivity.class);
                            startActivity(tIntent);
                            finish();
                        }else{
                        stage+=1;
                        show.setText(String.valueOf(alphabet[position]));
                        dialog.cancel();
                        k=0;
                        }
                    }
                })
                .setNegativeButton("다시 공부", new DialogInterface.OnClickListener(){
                    // 왼쪽 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton){
                        dialog.cancel();
                        position = min_num[stage-1];
                        show.setText(String.valueOf(alphabet[position]));
                        k=0;
                    }
                });
        show.setText(String.valueOf(alphabet[position]));
    }

    @OnClick(R.id.write)
    void goToDictation(){
        presentAlphabet = alphabet[position];
        Intent dIntent = new Intent(getApplicationContext(),BrailleInsertActivity.class );
        startActivity(dIntent);
    }

    @OnClick({R.id.previous, R.id.next})
    void onClickChange(View view){
        switch (view.getId()){
            case R.id.previous:
                position -= 1;
                break;
            case R.id.next:
                if(!dictation){
                    Toast show=Toast.makeText(getApplicationContext(), "Dictate the letter on th screen", Toast.LENGTH_SHORT);
                    show.show();
                    m_vibrator.vibrate(1000);
                }else{
                    position+=1;
                }
                break;
        }
        overCheck();
        show.setText(String.valueOf(alphabet[position]));
        dictation = false;
        TTS.speak(String.valueOf(alphabet[position]), TextToSpeech.QUEUE_FLUSH, null);
    }

    public void overCheck(){

        if (position > max_num[stage-1]){
            k += 1;
            if (k==2){
                stageClear = true;
                position = min_num[stage-1];
                AlertDialog dialog = builder.create();    // 알림창 객체 생성
                dialog.show();    // 알림창 띄우기
                TTS.speak("Stage" + stage + "학습 완료", TextToSpeech.QUEUE_FLUSH, null);
            }
        }
        else if (position<min_num[stage-1]){
            position = min_num[stage-1];
            Toast show=Toast.makeText(getApplicationContext(), "cannot move to this way", Toast.LENGTH_SHORT);
            show.show();
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
