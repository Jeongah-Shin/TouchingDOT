package com.dotincorpkr.englidot.BasicWord;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dotincorpkr.englidot.BaseActivity;
import com.dotincorpkr.englidot.R;
import com.dotincorpkr.englidot.Singleton;
import com.dotincorpkr.englidot.TagData;

/**
 * Created by wjddk on 2017-02-22.
 */

public class BasicwordLearningSwitching extends BaseActivity implements TextToSpeech.OnInitListener{
    int index = 0;
    int max =68;

    TextView word;
    TextView word_class;
    TextView mean;

    ImageButton previous;
    ImageButton next;

    TextToSpeech mTTS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basicword_learning_switching);

        word = (TextView)findViewById(R.id.word);
        word_class =(TextView)findViewById(R.id.wordclass);
        mean = (TextView)findViewById(R.id.mean);

        previous = (ImageButton)findViewById(R.id.previous);
        next = (ImageButton)findViewById(R.id.next);

        mTTS = new TextToSpeech(this, this);

        IndexSet();

        previous.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                index-=1;
                overCheck();
                IndexSet();
                onInit(1);
            }

        });

        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                index+=1;
                overCheck();
                IndexSet();
                onInit(1);
            }
        });

    }

    public void IndexSet(){
        TagData tagData = Singleton.getInstance().getSingleton_List().get(index);
        word.setText(tagData.getWord());
        word_class.setText(tagData.getWordClass1());
        mean.setText(tagData.getMean1());
    }

    public void overCheck(){

        if (index > max){
            index = 0;
        }
        else if (index<0){
            index = 0;
            Toast show=Toast.makeText(getApplicationContext(), "cannot move to this way", Toast.LENGTH_SHORT);
            show.show();
        }

    }

    public void onInit (int status){

        mTTS.speak(word.getText().toString()+mean.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);

    }





}
