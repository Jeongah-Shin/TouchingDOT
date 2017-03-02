package com.dotincorpkr.englidot.BasicWord;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dotincorpkr.englidot.BaseActivity;
import com.dotincorpkr.englidot.R;
import com.dotincorpkr.englidot.Singleton;
import com.dotincorpkr.englidot.TagData;

import java.util.Random;

import static com.dotincorpkr.englidot.R.id.example;

/**
 * Created by wjddk on 2017-02-22.
 */

public class BasicwordTestActivity extends BaseActivity {


    Random random = new Random();
    String[] choices = new String[4];
    int answer;
    int correct, incorrect;
    int index;
    int selected;
    int pnum =0;
    String Aword;
    String Amean;

    TextView example_set;
    TextView problem_number;
    ListView choice;
    ArrayAdapter choice_adapter;
    Button first;
    Button second;
    Button third;
    Button fourth;

    TagData tagData;

    Vibrator m_vibrator;
    SoundPool soundPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiple_test);

        problem_number = (TextView)findViewById(R.id.problem_number);
        first= (Button)findViewById(R.id.first);
        second= (Button)findViewById(R.id.second);
        third= (Button)findViewById(R.id.third);
        fourth= (Button)findViewById(R.id.fourth);
        choice = (ListView)findViewById(R.id.choice);
        example_set = (TextView)findViewById(example);

        first.setOnClickListener(answerClick);
        second.setOnClickListener(answerClick);
        third.setOnClickListener(answerClick);
        fourth.setOnClickListener(answerClick);
        index = 0;

        // Android에서 제공하는 string 문자열 하나를 출력 가능한 layout으로 어댑터 생성
        choice_adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.test_list_item);
        choice.setAdapter(choice_adapter);

        m_vibrator=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

        problemSet();

        // 리스트를 터치 했을 때, 닷 워치 점자가 올라옴. (현재는 토스트 처리)
        choice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position,
                                    long arg3) {
                Toast.makeText(
                        getApplicationContext(),
                        choice_adapter.getItem(position).toString(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
    public void problemSet(){
        pnum++;
        for(int i=0;i<4;i++){
            tagData = Singleton.getInstance().getSingleton_List().get(index);
            choices[i] = tagData.getTword();
            if(i==0){
                Aword = tagData.getTword();
                Amean = tagData.getTmean1();
            }
            index++;
        }
        example_set.setText(Amean);
        problem_number.setText(Integer.toString(pnum));

        answer = random.nextInt(4);
        //선지에 답안이 될 알파벳을 삽입한다.

        switch (answer){
            case 0:
                choice_adapter.add(choices[0]);
                choice_adapter.add(choices[1]);
                choice_adapter.add(choices[2]);
                choice_adapter.add(choices[3]);
                break;
            case 1:
                choice_adapter.add(choices[2]);
                choice_adapter.add(choices[1]);
                choice_adapter.add(choices[3]);
                choice_adapter.add(choices[0]);
                break;
            case 2:
                choice_adapter.add(choices[3]);
                choice_adapter.add(choices[0]);
                choice_adapter.add(choices[2]);
                choice_adapter.add(choices[1]);
                break;
            case 3:
                choice_adapter.add(choices[2]);
                choice_adapter.add(choices[0]);
                choice_adapter.add(choices[1]);
                choice_adapter.add(choices[3]);
                break;
            default:
                break;
        }
        answerCheck();

    }

    // 제출 하였을 때, 선택된 답안이 정답이면 정답 알림 소리를 발생시키고,  정답이 아니라면 오답 알림 소리 호출 후, 다음 문제로 넘어간다.
    View.OnClickListener answerClick = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.first:
                    selected = 0;
                    break;
                case R.id.second:
                    selected = 1;
                    break;
                case R.id.third:
                    selected = 2;
                    break;
                case R.id.fourth:
                    selected = 3;
                    break;
                default:
                    break;
            }
        }
    };

    public void answerCheck(){
        if(Amean.matches(choice_adapter.getItem(selected).toString())){
            problemSet();

        }
        else{
            problemSet();

        }


    }





}
