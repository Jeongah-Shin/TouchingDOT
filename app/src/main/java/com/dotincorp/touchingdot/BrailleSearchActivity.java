package com.dotincorp.touchingdot;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by wjddk on 2017-04-04.
 */

public class BrailleSearchActivity extends Activity implements View.OnClickListener {

    EditText edit_text;
    Button send;

    BleApplication bleApplication;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.braille_search);
        edit_text = (EditText)findViewById(R.id.edit_text);
        send = (Button)findViewById(R.id.send);
        send.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bleApplication = (BleApplication) getApplication();
    }

    public void onClick(View v){
        if(v.getId()==R.id.send){
            String text = edit_text.getText().toString();
            bleApplication.sendMessage(text);

        }
    }
}
