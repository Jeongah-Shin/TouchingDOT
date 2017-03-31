package com.dotincorp.touchingdot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static com.dotincorp.touchingdot.AlphabetTestMultipleActivity.wrongAnswer;
import static com.dotincorp.touchingdot.AlphabetTestMultipleActivity.wrongAnswer_length;


/**
 * Created by wjddk on 2017-02-09.
 */

public class MultipleTestResultActivity extends Activity {
    int testScore = 10-wrongAnswer_length;
    TextView result;
    TextView wrongABC;
    Button gotoReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alphabet_test_result);
        //테스트 결과 wrongAnswer 가 없다면 다시
        result= (TextView)findViewById(R.id.result);
        wrongABC=(TextView)findViewById(R.id.wrongABC);
        gotoReview=(Button)findViewById(R.id.gotoReview);

        result.setText(testScore+"/10");
        wrongABC.setText(wrongAnswer.toString());

        gotoReview.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AlphabetReviewActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }


}


