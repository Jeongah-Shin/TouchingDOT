package com.dotincorp.touchingdot;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by wjddk on 2017-04-04.
 */

public class SpecialFragment extends Fragment {

    Button sc_number;
    Button sc_punc;
    Button sc_sign;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sc_fragment, container, false);
        sc_number = (Button)view.findViewById(R.id.sc_number);
        sc_punc = (Button)view.findViewById(R.id.sc_punc);
        sc_sign = (Button)view.findViewById(R.id.sc_sign);
        return view;
    }
}