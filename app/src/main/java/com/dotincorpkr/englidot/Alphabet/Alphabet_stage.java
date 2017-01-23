package com.dotincorpkr.englidot.Alphabet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dotincorpkr.englidot.R;

/**
 * Created by wjddk on 2017-01-23.
 */

public class Alphabet_stage extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alphabet_1_stage);

        Button Song = (Button) findViewById(R.id.Song);
        Button AtoJ = (Button) findViewById(R.id.AtoJ);
        Button KtoT = (Button) findViewById(R.id.KtoT);
        Button UtoZ = (Button) findViewById(R.id.UtoZ);
        TextView user_section = (TextView)findViewById(R.id.user_section);


        AtoJ.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Alphabet_switching.class);
                startActivity(intent);
                finish();
            }
        });

    }


}
