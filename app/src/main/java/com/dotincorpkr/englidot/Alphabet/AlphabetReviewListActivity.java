package com.dotincorpkr.englidot.Alphabet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.dotincorpkr.englidot.BaseActivity;
import com.dotincorpkr.englidot.R;

import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * Created by wjddk on 2017-02-01.
 */

public class AlphabetReviewListActivity extends BaseActivity {
    ListView testResults;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alphabet_review_list);

        ButterKnife.bind(this);

        testResults = (ListView)findViewById(R.id.testResults);
        testResults.setAdapter(new AlphabetReviewAdapter());
    }

    @OnItemClick(R.id.testResults)
    public void listViewItemClicked(int position) {
        Log.i("onitemclicked", "click");
        Intent r_intent = new Intent(this, AlphabetReviewInfoActivity.class);
        r_intent.putExtra("position", position);
        startActivity(r_intent);
    }

}

