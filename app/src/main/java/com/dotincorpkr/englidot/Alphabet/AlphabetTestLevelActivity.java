package com.dotincorpkr.englidot.Alphabet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dotincorpkr.englidot.BaseActivity;
import com.dotincorpkr.englidot.R;

/**
 * Created by wjddk on 2017-02-09.
 */

public class AlphabetTestLevelActivity extends BaseActivity {
    Button level1;
    Button level2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alphabet_test_level);

        level1 =(Button)findViewById(R.id.level1);
        level2 =(Button)findViewById(R.id.level2);

        level1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AlphabetTestMultipleActivity.class);
                startActivity(intent);
                finish();
            }
        });

        level2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AlphabetTestShortAnswerActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
