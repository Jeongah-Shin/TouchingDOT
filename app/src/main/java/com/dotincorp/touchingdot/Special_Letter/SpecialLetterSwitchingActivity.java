package com.dotincorp.touchingdot.Special_Letter;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dotincorp.touchingdot.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by wjddk on 2017-03-21.
 */

public class SpecialLetterSwitchingActivity extends Activity {

    @Bind(R.id.show)
    TextView show;
    @Bind(R.id.previous)
    ImageButton previous;
    @Bind(R.id.next)
    ImageButton next;
    @Bind(R.id.write)
    Button write;

    int position = 0;

    char[] operator = {};
    char[] special_letter = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.letter_switching);
        ButterKnife.bind(this);


    }

}
