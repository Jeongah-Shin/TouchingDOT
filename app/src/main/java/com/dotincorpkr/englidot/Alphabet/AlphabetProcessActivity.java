package com.dotincorpkr.englidot.Alphabet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.dotincorpkr.englidot.BaseActivity;
import com.dotincorpkr.englidot.JsonThread;
import com.dotincorpkr.englidot.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wjddk on 2017-01-23.
 */

public class AlphabetProcessActivity extends BaseActivity {
    //btn - learning, test, review
    //Imageview - englidot

    @Bind(R.id.englidot_logo)
    ImageView englidot_logo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.alphabet_process);
        ButterKnife.bind(this);

        Thread reviewThr = new JsonThread("testResultJson.php");
        reviewThr.start();

    }

    @OnClick({R.id.learning, R.id.test, R.id.review})
    void onClick(View view){
        switch(view.getId()){
            case R.id.learning:
                Intent lIntent = new Intent(getApplicationContext(), AlphabetLearningActivity.class);
                startActivity(lIntent);
                break;
            case R.id.test:
                Intent tIntent = new Intent(getApplicationContext(), AlphabetTestLevelActivity.class);
                startActivity(tIntent);
                break;
            case R.id.review:
                Intent rIntent = new Intent(getApplicationContext(), AlphabetReviewListActivity.class);
                startActivity(rIntent);
                break;
            default:
                break;

        }
    }


}