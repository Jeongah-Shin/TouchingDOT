package com.dotincorpkr.englidot.BasicWord;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.dotincorpkr.englidot.BaseActivity;
import com.dotincorpkr.englidot.R;

import butterknife.ButterKnife;
import butterknife.OnItemClick;


/**
 * Created by wjddk on 2017-02-22.
 */

public class BasicwordLearningListActivity extends BaseActivity {

    ListView basicword_list;
    Button bwSwitching;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basicword_learning_list);

        ButterKnife.bind(this);

        basicword_list = (ListView)findViewById(R.id.basicword_list);
        basicword_list.setAdapter(new BasicwordLearningAdapter());
        bwSwitching = (Button)findViewById(R.id.bwSwitching);

        bwSwitching.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent Sintent = new Intent(getApplicationContext(), BasicwordLearningSwitching.class);
                startActivity(Sintent);
            }
        });

    }

    @OnItemClick(R.id.basicword_list)
    public void listViewItemClicked ( int position){
        Log.i("onitemclicked", "click");
        Intent Lintent = new Intent(this, BasicwordLearningDetailActivity.class);
        Lintent.putExtra("position", position);
        startActivity(Lintent);
    }


}
