package com.dotincorp.touchingdot;

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
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import static com.dotincorp.touchingdot.BrailleInsertActivity.presentLetter;

/**
 * Created by wjddk on 2017-02-13.
 */

public class AlphabetSwitchingActivity extends Activity implements  View.OnClickListener{
    /**
     * 알파벳 문자를 switching 하면서 닷 워치로 학습하고, dictation 해볼 수 있는 Activity
     */
    TextView title;
    TextView show;
    Button previous;
    Button next;
    Button dictation;

    Vibrator m_vibrator;
    TextToSpeech TTS;
    AlertDialog.Builder choiceBuilder;
    AlertDialog.Builder generalBuilder;

    int position = 0;
    int stage = 1;
    final int[] min_num = {0, 10, 20};
    final int[] max_num = {9, 19, 24};
    BleApplication bleApplication;

    char[] alphabet = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'X', 'Y', 'Z',
            'W'
    };

    static String[] alphabet_braille_table =
    {
        "01", "03", "09", "19", "11", "0B", "1B", "13", "0A", "1A", "05", "07", "0D",
                "1D", "15", "0F", "1F", "17", "0e", "1e", "25", "27", "3a", "2d", "3d", "35"
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.letter_switching);
        viewBinder();

        m_vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        TTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    TTS.setLanguage(Locale.ENGLISH);
                    TTS.speak(String.valueOf(alphabet[position]), TextToSpeech.QUEUE_FLUSH, null);
                    bleApplication.sendBraille(alphabet_braille_table[position]+"000000");
                }
            }
        });

    }

    @Override
    protected void onResume() {
        bleApplication = (BleApplication) getApplication();
        super.onResume();
    }

    public void onClick(View view){
        int id = view.getId();
        switch (id){
            //dictation 버튼을 누르면 화면 위 점자를 따라 쓸 수 있는 BrailleInsertActivity가 소환된다.
            // 화면 위 알파벳의 점형을 올바르게 입력해야만 다시 AlphabetSwitchingActivity(학습 화면)로 돌아올 수 있다.
            case R.id.dictation:
                presentLetter = alphabet[position];
                Intent dictationIntent = new Intent(getApplicationContext(), BrailleInsertActivity.class);
                startActivity(dictationIntent);
                break;
            // 이전, 이후로 가는 버튼을 누르게 되면 닷 워치에 이전, 이후에 해당하는 문제를 보내고 화면에 띄움과 동시에 position(인덱스 역할)이 선언해놓은 배열의 크기 넘어가지 않는지 판단하는 overChceck() 함수를 호출한다.
           case R.id.previous:
                position--;
                bleApplication.sendBraille(alphabet_braille_table[position]+"000000");
                overCheck();
                show.setText(Character.toString(alphabet[position]));
                TTS.speak(String.valueOf(alphabet[position]), TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.next:
                if (BrailleInsertActivity.dictation) {
                    position++;
                    bleApplication.sendBraille(alphabet_braille_table[position]+"000000");
                    overCheck();
                    show.setText(Character.toString(alphabet[position]));
                    TTS.speak(String.valueOf(alphabet[position]), TextToSpeech.QUEUE_FLUSH, null);
                    BrailleInsertActivity.dictation = false;
                } else {
                    //BrailleInsertActivity를 통해 올바른 점형을 입력하지 않으면 다음 위치로 넘어갈 수 없다.
                    m_vibrator.vibrate(500);
                    Toast dictate = Toast.makeText(getApplicationContext(), "Dictate the letter on th screen", Toast.LENGTH_SHORT);
                    dictate.show();
                }
                break;
        }
    }

    public void overCheck() {
        if (position < 0) {
            position = 0;
            Toast alert = Toast.makeText(getApplicationContext(), "cannot move this way", Toast.LENGTH_SHORT);
            alert.show();
        }
        //Stage 별로 클리어 했을 때, 알림 소리 송출
        if (position > max_num[stage - 1]) {
            switch (stage) {
                case 1:
                    TTS.speak("Stage 1 Clear! Keep your tip for the next step.", TextToSpeech.QUEUE_FLUSH, null);
                    stage++;
                    getGeneralAlert("Tip", "Braille form of of K to T is same as A to J with dot 3.", "Okay");
                    break;
                case 2:
                    TTS.speak("Stage 2 Clear! Keep your tip for the next step.", TextToSpeech.QUEUE_FLUSH, null);
                    stage++;
                    getGeneralAlert("Tip", "Braille form of of U,V,X,Y,Z is same as A to E with dot 3 and 6.", "Okay");
                    break;
                case 3:
                    TTS.speak("Stage 3 Clear! Keep your tip for the next step.", TextToSpeech.QUEUE_FLUSH, null);
                    stage++;
                    getGeneralAlert("Tip", "W is the most special letter among 26 letters.", "Okay");
                    break;
                case 4:
                    if (position > alphabet.length - 1) {
                        getChoiceAlert("Complete", "Alphabet Learning process complete! Press [Test] button to check your skills or press [Review] button to study once again.", "Test", "Review");
                    }
                    break;
            }
        }
    }
    //TODO getChoiceAlert, getGeneralAlert 함수를 호출해도 해당 알림창이 나오지 않음.
    public AlertDialog.Builder getChoiceAlert(String title, String content, String positive, String negative) {

        choiceBuilder = new AlertDialog.Builder(this);
        choiceBuilder.setTitle(title)        // 제목 설정
                .setMessage(content)        // 콘텐츠 설정
                .setCancelable(false)        // 뒤로 버튼 눌러도 취소되지 않음.
                .setPositiveButton(positive, new DialogInterface.OnClickListener() {
                    // 오른쪽 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent tIntent = new Intent(getApplicationContext(), AlphabetTestMultipleActivity.class);
                        startActivity(tIntent);
                        finish();
                    }
                })
                .setNegativeButton(negative, new DialogInterface.OnClickListener() {
                    // 왼쪽 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                        position = min_num[0];
                        show.setText(String.valueOf(alphabet[position]));
                    }
                });

        return choiceBuilder;
    }

    public AlertDialog.Builder getGeneralAlert(String title, String content, String quit) {

        generalBuilder = new AlertDialog.Builder(this);
        generalBuilder.setTitle(title)
                .setMessage(content)
                .setCancelable(false)
                .setNeutralButton(quit, new DialogInterface.OnClickListener() {
                    // 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

        return generalBuilder;
    }

    public void viewBinder(){

        title = (TextView)findViewById(R.id.title);
        title.setText("Alphabet Learning");
        show = (TextView)findViewById(R.id.show);
        previous = (Button)findViewById(R.id.previous);
        previous.setOnClickListener(this);
        next = (Button)findViewById(R.id.next);
        next.setOnClickListener(this);
        dictation =(Button)findViewById(R.id.dictation);
        dictation.setOnClickListener(this);

        show.setText(Character.toString(alphabet[position]));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (TTS != null) {
            TTS.stop();
            TTS.shutdown();
        }
    }

}
