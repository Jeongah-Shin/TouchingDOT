package com.dotincorpkr.englidot.Alphabet;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.dotincorpkr.englidot.BaseActivity;
import com.dotincorpkr.englidot.R;

/**
 * Created by wjddk on 2017-02-20.
 */

public class ReviewCompleteActivity extends BaseActivity {
    TextView reviewComplete;

    SoundPool soundPool;
    int complete;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alphabet_review_complete);

        reviewComplete = (TextView)findViewById(R.id.reviewComplete);

        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        complete = soundPool.load(this, R.raw.pokemon, 0);
        soundPool.play(complete, 50, 50, 0, 0, 1);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), AlphabetReviewListActivity.class);
                startActivity(intent);
                finish();
            }
        }, 5000);// 5 초
    }
}
