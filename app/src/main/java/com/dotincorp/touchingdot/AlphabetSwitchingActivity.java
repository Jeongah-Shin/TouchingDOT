package com.dotincorp.touchingdot;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.dotincorp.touchingdot.BrailleInsertActivity.dictation;
import static com.dotincorp.touchingdot.BrailleInsertActivity.presentLetter;

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
    AlertDialog.Builder noticeBuilder;

    int position = 0;
    int stage = 1;
    int[] min_num = {0, 10, 20};
    int[] max_num = {9, 19, 24};

    //The "x" and "y" position of the "Menu" on screen.
    Point p;

    boolean stageClear = false;
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
        ButterKnife.bind(this);

        show.setText(Character.toString(alphabet[position]));

        noticeBuilder = new AlertDialog.Builder(this);
        noticeBuilder.setTitle("Stage Clear")        // 제목 설정
                .setMessage("Alphabet Learning process complete! Press [Test] button to check your skills or press [Review] button to study once again.")        // 메세지 설정
                .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                .setPositiveButton("Test", new DialogInterface.OnClickListener(){
                    // 오른쪽 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton){
                            Intent tIntent = new Intent(getApplicationContext(),AlphabetTestMultipleActivity.class);
                            startActivity(tIntent);
                            finish();
                    }
                })
                .setNegativeButton("Review", new DialogInterface.OnClickListener(){
                    // 왼쪽 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton){
                        dialog.cancel();
                        position = min_num[0];
                        show.setText(String.valueOf(alphabet[position]));
                    }
                });

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

    }

    @OnClick(R.id.write)
    void goToDictation(){
        presentLetter = alphabet[position];
        Intent dictationIntent = new Intent(getApplicationContext(), BrailleInsertActivity.class);
        startActivity(dictationIntent);
    }
    @OnClick(R.id.previous)
    void btnPrevious(){
        position --;
        overCheck();
        show.setText(Character.toString(alphabet[position]));
        TTS.speak(String.valueOf(alphabet[position]), TextToSpeech.QUEUE_FLUSH, null);
    }
    @OnClick(R.id.next)
    void btnNext(){
        if(dictation) {
            position++;
            overCheck();
            show.setText(Character.toString(alphabet[position]));
            TTS.speak(String.valueOf(alphabet[position]), TextToSpeech.QUEUE_FLUSH, null);
        }else{
            m_vibrator.vibrate(500);
            Toast dictate=Toast.makeText(getApplicationContext(), "Dictate the letter on th screen", Toast.LENGTH_SHORT);
            dictate.show();
        }
    }
    public void overCheck(){
        if (position>alphabet.length-1){
            AlertDialog test = noticeBuilder.create();
            test.show();
        }else if(position<0){
            position = 0;
            Toast alert = Toast.makeText(getApplicationContext(),"cannot move this way",Toast.LENGTH_SHORT);
            alert.show();
        }
        switch(stage){
            case 1:
                if(position>max_num[stage-1]){
                    //Open popup window
                    if (p != null)
                        showPopup(AlphabetSwitchingActivity.this, p);
                }
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
        }
    }

    // Get the x and y position after the button is draw on screen
// (It's important to note that we can't get the position in the onCreate(),
// because at that stage most probably the view isn't drawn yet, so it will return (0, 0))
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        int[] location = new int[2];

        // Get the x, y location and store it in the location[] array
        // location[0] = x, location[1] = y.
        findViewById(R.id.menu).getLocationOnScreen(location);

        //Initialize the Point with x, and y positions
        p = new Point();
        p.x = location[0];
        p.y = location[1];
    }

    // The method that displays the popup.
    private void showPopup(final Activity context, Point p) {
        int popupWidth = 200;
        int popupHeight = 150;

        // Inflate the popup_layout.xml
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.LinearLayout);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.comment, viewGroup);

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);
        popup.setFocusable(true);

        // Some offset to align the popup a bit to the right, and a bit down, relative to button's position.
        int OFFSET_X = 30;
        int OFFSET_Y = 30;

        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);

        // Getting a reference to Close button, and close the popup when clicked.
        Button close = (Button) layout.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });
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
