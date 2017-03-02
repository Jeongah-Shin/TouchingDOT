package com.dotincorpkr.englidot.Alphabet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dotincorpkr.englidot.BaseActivity;
import com.dotincorpkr.englidot.JsonThread;
import com.dotincorpkr.englidot.R;
import com.dotincorpkr.englidot.Singleton;
import com.dotincorpkr.englidot.TagData;

/**
 * Created by wjddk on 2017-02-02.
 */

public class AlphabetReviewInfoActivity extends BaseActivity {
    // AQuery aq;

    public static char[] reviewLetter;
    String review;

    TextView review_info;
    Button gotoReview;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alphabet_review_detail);
        //aq = new AQuery(this);
        review_info = (TextView)findViewById(R.id.review_info);
        gotoReview = (Button)findViewById(R.id.gotoReview);

        Thread reviewThr = new JsonThread("testResultJson.php");
        reviewThr.start();

        Intent r_intent = getIntent();
        int index = r_intent.getIntExtra("position", -1);
        TagData tagData = Singleton.getInstance().getSingleton_List().get(index);
        //aq.id(R.id.review_info).text(tagData.getTestTime() + "\n"+ tagData.getTestResult() +"\n"+ tagData.getWrongAnswer()  ).textColor(Color.BLACK).textSize(30);

        String a = tagData.getWrongAnswer();
        review = a.replace("[","");
        review = review.replace("]","");
        review_info.setText(tagData.getTestTime()+"\n\n"+review);
        review = review.replace(", ","");
        reviewLetter = review.toCharArray();


        gotoReview.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (reviewLetter!=null) {
                    Intent goR = new Intent(AlphabetReviewInfoActivity.this, AlphabetReview1Activity.class);
                    goR.putExtra("review",review);
                    startActivity(goR);
                    finish();
                }else{
                    Toast show=Toast.makeText(getApplicationContext(), "복습 할 문자열이 없습니다.", Toast.LENGTH_SHORT);
                    show.show();
                }
            }

        });




        //Intent로 다음 액티비티 (객관식/주관식 복습창으로 넘긴다. 그러려면 복습 체계가 액티비티 하나 내에서 끝나야함)
    }
}
