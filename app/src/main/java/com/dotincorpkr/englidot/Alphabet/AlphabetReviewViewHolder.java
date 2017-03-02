package com.dotincorpkr.englidot.Alphabet;

import android.view.View;
import android.widget.TextView;

import com.dotincorpkr.englidot.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by wjddk on 2017-02-06.
 */

public class AlphabetReviewViewHolder {

    @Bind(R.id.order)
    public TextView order;
    @Bind(R.id.wrongAnswer)
    public TextView wrongAnswer;

    public AlphabetReviewViewHolder(View view) {
        ButterKnife.bind(this, view);
    }
}


