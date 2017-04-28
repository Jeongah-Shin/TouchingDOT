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

public class BrailleFragment extends Fragment {

    Button tutorial;
    Button br_learning;
    Button br_selected;
    Button br_search;

    /**
     * Braille 탭의 View를 띄우는 Fragment
     */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.br_fragment, container, false);
        tutorial = (Button)view.findViewById(R.id.tutorial);
        br_learning = (Button) view.findViewById(R.id.br_learning);
        br_selected = (Button)view.findViewById(R.id.br_selected);
        br_search = (Button)view.findViewById(R.id.br_search);

        return view;
    }

}