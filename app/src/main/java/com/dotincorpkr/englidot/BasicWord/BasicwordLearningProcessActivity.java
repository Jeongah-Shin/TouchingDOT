package com.dotincorpkr.englidot.BasicWord;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dotincorpkr.englidot.BaseActivity;
import com.dotincorpkr.englidot.JsonThread;
import com.dotincorpkr.englidot.R;

/**
 * Created by wjddk on 2017-02-22.
 */

public class BasicwordLearningProcessActivity extends BaseActivity {
    TextView nickname;
    TextView level;
    Button basic_voca;
    Button test;
    Button myWord_note;
    ImageView logo;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.basicword_learning_process);

        nickname = (TextView)findViewById(R.id.nickname);
        level = (TextView)findViewById(R.id.level);
        basic_voca = (Button)findViewById(R.id.basic_voca);
        test = (Button)findViewById(R.id.test);
        myWord_note = (Button)findViewById(R.id.myWord_note);
        logo = (ImageView)findViewById(R.id.logo);

        Thread basicWord = new JsonThread("basicWordList.php");
        basicWord.start();

        Thread basicW = new JsonThread("basicWordList.php");
        basicW.start();


        basic_voca.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BasicwordLearningListActivity.class);
                startActivity(intent);

            }
        });

        test.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BasicwordTestActivity.class);
                startActivity(intent);
            }
        });

        myWord_note.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BasicwordBookmarkListActivity.class);
                startActivity(intent);
            }
        });



    }

}
