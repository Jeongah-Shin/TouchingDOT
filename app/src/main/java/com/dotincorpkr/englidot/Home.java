package com.dotincorpkr.englidot;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dotincorpkr.englidot.Alphabet.Alphabet_stage;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_0_home);
        Button Alphabet =(Button)findViewById(R.id.Alphabet);
        Button Basic_voca=(Button)findViewById(R.id.Basic_voca);
        Button Abbrev1=(Button)findViewById(R.id.Abbrev1);
        Button Abbrev1_utilization=(Button)findViewById(R.id.Abbrev1_utilization);
        Button Abbrev2=(Button)findViewById(R.id.Abbrev2);
        Button Abbrev2_utilization=(Button)findViewById(R.id.Abbrev2_utilization);

        Alphabet.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(),Alphabet_stage.class );
                startActivity(intent);
            }
        });
    }
}
