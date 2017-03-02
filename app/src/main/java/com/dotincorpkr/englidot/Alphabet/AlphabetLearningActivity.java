package com.dotincorpkr.englidot.Alphabet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dotincorpkr.englidot.BaseActivity;
import com.dotincorpkr.englidot.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wjddk on 2017-01-23.
 */

public class AlphabetLearningActivity extends BaseActivity {

    // btn - song, alphabet_learning

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alphabet_learning);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.song, R.id.alphabet_learning})
    void onClick(View view){
        switch(view.getId()){
            case R.id.song:
                Intent sIntent = new Intent(getApplicationContext(), AlphabetSongActivity.class);
                startActivity(sIntent);
                break;
            case R.id.alphabet_learning:
                Intent aIntent = new Intent(getApplicationContext(), AlphabetSwitchingActivity.class);
                startActivity(aIntent);
                break;
            default:
                break;

        }
    }



}
