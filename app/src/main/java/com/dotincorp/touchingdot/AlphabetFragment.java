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

public class AlphabetFragment extends Fragment {
    Button ap_learning;
    Button ap_song;
    Button ap_test;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ap_fragment, container, false);

        ap_learning = (Button)view.findViewById(R.id.ap_learning);
        ap_song = (Button)view.findViewById(R.id.ap_song);
        ap_test = (Button)view.findViewById(R.id.ap_test);
        return view;
    }

}