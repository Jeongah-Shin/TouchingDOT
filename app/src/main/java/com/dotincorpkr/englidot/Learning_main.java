package com.dotincorpkr.englidot;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by wjddk on 2017-01-23.
 */

public class Learning_main extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1_learning_main);

        ImageView profile_image = (ImageView)findViewById(R.id.profile_image);
        TextView nickname = (TextView)findViewById(R.id.nickname);
        TextView level = (TextView)findViewById(R.id.level);
        Button learning = (Button)findViewById(R.id.learning);
        Button review = (Button)findViewById(R.id.review);
        Button test = (Button)findViewById(R.id.test);
        Button mySpace = (Button)findViewById(R.id.mySpace);
        LinearLayout englidot_more = (LinearLayout)findViewById(R.id.englidot_more);

        //learning_catergoryning_catergory.xml 레이아웃 구성요소 선언부
        LinearLayout englidot_logo = (LinearLayout)findViewById(R.id.englidot_logo);


        // '나의 학습방'의 메인 Intent로 이동합니다.
        /*
        MyPage.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v){
                Intent intent = new Intent(mainActivity.this,@@@@.class );
                startActivity(intent);
            }
        });
        */



    }


}