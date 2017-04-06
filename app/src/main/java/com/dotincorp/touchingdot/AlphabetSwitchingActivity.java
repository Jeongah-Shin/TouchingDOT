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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.letter_switching);
        show = (TextView)findViewById(R.id.show);
        previous = (Button)findViewById(R.id.previous);
        previous.setOnClickListener(this);
        next = (Button)findViewById(R.id.next);
        next.setOnClickListener(this);
        dictation =(Button)findViewById(R.id.dictation);
        dictation.setOnClickListener(this);

        show.setText(Character.toString(alphabet[position]));

        m_vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        TTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    TTS.setLanguage(Locale.ENGLISH);
                    TTS.speak(String.valueOf(alphabet[position]), TextToSpeech.QUEUE_FLUSH, null);
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
            case R.id.dictation:
                presentLetter = alphabet[position];
                Intent dictationIntent = new Intent(getApplicationContext(), BrailleInsertActivity.class);
                startActivity(dictationIntent);
                break;
            case R.id.previous:
                position--;
                bleApplication.sendMessage(Character.toString(alphabet[position]));
                overCheck();
                show.setText(Character.toString(alphabet[position]));
                TTS.speak(String.valueOf(alphabet[position]), TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.next:
                if (BrailleInsertActivity.dictation) {
                    position++;
                    bleApplication.sendMessage(Character.toString(alphabet[position]));
                    overCheck();
                    show.setText(Character.toString(alphabet[position]));
                    TTS.speak(String.valueOf(alphabet[position]), TextToSpeech.QUEUE_FLUSH, null);
                } else {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (TTS != null) {
            TTS.stop();
            TTS.shutdown();
        }
    }

}
