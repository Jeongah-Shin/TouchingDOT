package com.dotincorpkr.englidot.BasicWord;

import android.view.View;
import android.widget.TextView;

import com.dotincorpkr.englidot.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by wjddk on 2017-02-22.
 */

public class BasicwordLearningVIewHolder {

    @Bind(R.id.word)
    public TextView word;

    public BasicwordLearningVIewHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
