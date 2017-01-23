package com.dotincorpkr.englidot.Alphabet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dotincorpkr.englidot.R;

/**
 * Created by wjddk on 2017-01-23.
 */

public class Alphabet_switching extends Activity implements TextToSpeech.OnInitListener {

    int position = 0;
    char[] alphabet = {
            'A', 'B', 'C', 'D',
            'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X',
            'Y', 'Z'
    };
    private TextToSpeech TTS;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alphabet_2_switching);

        final TextView Show = (TextView) findViewById(R.id.Show);
        Button Previous = (Button) findViewById(R.id.Previous);
        Button Next = (Button) findViewById(R.id.Next);
        Button Whole_paging = (Button) findViewById(R.id.Whole_paging);
        Button Pass = (Button)findViewById(R.id.Pass);
        TTS =new TextToSpeech(this,this);

        Show.setText(String.valueOf(alphabet[position]));

        Previous.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                position -= 1;
                Show.setText(String.valueOf(alphabet[position]));
                onInit(1);
            }

        });

        Next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                position+=1;
                Show.setText(String.valueOf(alphabet[position]));
                onInit(1);
            }
        });

        Pass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Braille_insert.class );
                startActivity(intent);
                finish();
            }

        });


    }

    public void onInit ( int status){
        int i = 0;
        while(i<1) {
            if (findViewById(R.id.Show) != null) {
                TTS.speak(String.valueOf(alphabet[position]), TextToSpeech.QUEUE_ADD, null);
                i++;
            } else {
                TTS.shutdown();
            }
        }
    }





}



// position이 0_26 사이에 없을 때의 예외 처리 더 해야함