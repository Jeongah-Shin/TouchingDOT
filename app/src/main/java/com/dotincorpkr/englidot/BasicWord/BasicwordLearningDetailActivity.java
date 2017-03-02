package com.dotincorpkr.englidot.BasicWord;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.TextView;

import com.dotincorpkr.englidot.BaseActivity;
import com.dotincorpkr.englidot.R;
import com.dotincorpkr.englidot.Singleton;
import com.dotincorpkr.englidot.TagData;

/**
 * Created by wjddk on 2017-02-22.
 */

public class BasicwordLearningDetailActivity extends BaseActivity implements TextToSpeech.OnInitListener {

    TextView word_box;
    TextView wordclass_box;
    TextView mean_box;

    String word;
    String wordClass;
    String mean;

    TextToSpeech TTS;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basicword_learning_detail);

        word_box = (TextView)findViewById(R.id.word);
        wordclass_box = (TextView)findViewById(R.id.wordclass);
        mean_box = (TextView)findViewById(R.id.mean);

        TTS = new TextToSpeech(this, this);

        Intent Lintent = getIntent();
        int index = Lintent.getIntExtra("position", -1);
        TagData tagData = Singleton.getInstance().getSingleton_List().get(index);
        //aq.id(R.id.review_info).text(tagData.getTestTime() + "\n"+ tagData.getTestResult() +"\n"+ tagData.getWrongAnswer()  ).textColor(Color.BLACK).textSize(30);

        word = tagData.getWord();
        wordClass = tagData.getWordClass1();
        mean = tagData.getMean1();

        word_box.setText(word);
        wordclass_box.setText(wordClass);
        mean_box.setText(mean);
        onInit(1);

    }

    @Override
    public void onInit(int status) {
        TTS.speak("품사:"+wordClass + word +mean,TextToSpeech.QUEUE_FLUSH,null);

    }
}