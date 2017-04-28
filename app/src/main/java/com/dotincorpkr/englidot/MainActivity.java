package com.dotincorpkr.englidot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.dotincorpkr.englidot.Alphabet.AlphabetProcessActivity;
import com.dotincorpkr.englidot.BasicWord.BasicwordLearningProcessActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @Bind(R.id.englidot_logo)
    ImageView englidot_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }
    @OnClick({R.id.Alphabet, R.id.Basic_voca, R.id.Abbrev1, R.id.Abbrev1_utilization, R.id.Abbrev2, R.id.Abbrev2_utilization})
    public void categorizedMenu(View v){
        switch (v.getId()){
            case R.id.Alphabet:
                Intent AIntent = new Intent(getApplicationContext(), AlphabetProcessActivity.class);
                startActivity(AIntent);
                break;
            case R.id.Basic_voca:
                Intent BIntent = new Intent(getApplicationContext(), BasicwordLearningProcessActivity.class);
                startActivity(BIntent);
                break;
            case R.id.Abbrev1:
                break;
            case R.id.Abbrev1_utilization:
                break;
            case R.id.Abbrev2:
                break;
            case R.id.Abbrev2_utilization:
                break;
        }
    }
}
