package com.dotincorpkr.englidot.Kakaotalk;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dotincorpkr.englidot.BaseActivity;
import com.dotincorpkr.englidot.MainActivity;
import com.dotincorpkr.englidot.R;

/**
 * Created by wjddk on 2017-01-23.
 */
public class Kakaotalk_login_success extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("로그인 성공. 어플리케이션을 시작해보세요");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kakaotalk_login_success);


        LinearLayout englidot_logo = (LinearLayout)findViewById(R.id.englidot_logo);
        TextView nickname_greeting = (TextView)findViewById(R.id.nickname_greeting);
        Button start = (Button)findViewById(R.id.start);

        SharedPreferences pref = getSharedPreferences("kakaoInfo", Activity.MODE_PRIVATE);
        String kakaoNick = pref.getString("nick", "[Default] 꾸우...이름이 안나와요오");
        String greeting_message = kakaoNick + "님, 안녕하세요!";

        nickname_greeting.setText(greeting_message);

        //String kakaoImage = pref.getString("imagePath", null);
        //Uri kakaoImageUri = Uri.parse(kakaoImage);
        //profile_image.setImageURI(kakaoImageUri);
        //프로필 이미지 처리 - 중요 하지 않으므로 나중에!

        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(),MainActivity.class );
                startActivity(intent);
                finish();
            }
        });

    }


}



