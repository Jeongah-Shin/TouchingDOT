package com.dotincorp.touchingdot.Alphabet;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.dotincorp.touchingdot.R;
import com.dotincorp.watchservice.BluetoothLeService;

import java.util.ArrayList;
import java.util.Random;

import static android.content.ContentValues.TAG;
import static com.dotincorp.touchingdot.BluetoothScanning.MainActivity.bluetoothService;

/**
 * Created by wjddk on 2017-02-09.
 */

public class AlphabetTestMultipleActivity extends Activity {
    public int connectionStatus = BluetoothLeService.STATE_DISCONNECTED;

    int correct,incorrect;
    static char[] alphabet = {
            'A', 'B', 'C', 'D',
            'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X',
            'Y', 'Z'
    };
    public static ArrayList<Character> wrongAnswer = new ArrayList<>();
    public static int wrongAnswer_length =0;
    int[] generated = new int[4];
    String selected;
    int answer_number;
    int set=1;
    Random example_generator = new Random();
    char[] choices = new char[4];
    TextView example_set;
    TextView problem_number;
    ListView choice;
    ArrayAdapter choice_adapter;
    Button first;
    Button second;
    Button third;
    Button fourth;
    Vibrator m_vibrator;
    SoundPool soundPool;

    /**
     * 서비스 연결
     */
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bluetoothService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!bluetoothService.initialize()) {
                finish();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bluetoothService = null;

        }

    };

    /**
     * 연결 상태 업데이트 리시버
     */
    private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) { // 연결되었을 때
                Log.i(TAG, "Received ACTION_GATT_CONNECTED");
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.i(TAG, "Received ACTION_GATT_SERVICES_DISCOVERED");
            } else if (BluetoothLeService.ACTION_GATT_OBSERVER_SETTED.equals(action)) {
                Log.i(TAG, "Received ACTION_GATT_OBSERVER_SETTED");
                connectionStatus = BluetoothLeService.STATE_CONNECTED;

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) { // 연결 해제 되었을 때
                Log.i(TAG, "Received ACTION_GATT_DISCONNECTED");
                connectionStatus = BluetoothLeService.STATE_DISCONNECTED;

            }
        }
    };

    private IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_OBSERVER_SETTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        return intentFilter;
    }


    @Override
    protected void onResume() {
        super.onResume();

        // 리시버 등록
        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());

        // 서비스에 연결
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, serviceConnection, BIND_AUTO_CREATE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiple_test);

        problem_number = (TextView)findViewById(R.id.problem_number);
        first= (Button)findViewById(R.id.first);
        second= (Button)findViewById(R.id.second);
        third= (Button)findViewById(R.id.third);
        fourth= (Button)findViewById(R.id.fourth);

        example_set = (TextView)findViewById(R.id.example);
        // Android에서 제공하는 string 문자열 하나를 출력 가능한 layout으로 어댑터 생성
        choice_adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.test_list_item);

        // Xml에서 추가한 ListView 연결
        choice = (ListView)findViewById(R.id.choice);

        // ListView에 어댑터 연결
        choice.setAdapter(choice_adapter);

        m_vibrator=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

        problem_number.setText("1번");
        randWordPlace();
        example_set.setText(Character.toString(alphabet[answer_number]));

        first.setOnClickListener(Clickname);
        second.setOnClickListener(Clickname);
        third.setOnClickListener(Clickname);
        fourth.setOnClickListener(Clickname);


        // 리스트를 터치 했을 때, 닷 워치 점자가 올라옴
        choice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position,
                                    long arg3) {
                sendMessage(choice_adapter.getItem(position).toString());

            }
        });
    }

    // 제출 하였을 때, 선택된 답안이 정답이면 정답 알림 소리를 발생시키고,  정답이 아니라면 오답 알림 소리 호출 후, 다음 문제로 넘어간다.
    View.OnClickListener Clickname = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.first:
                    selected = choice_adapter.getItem(0).toString();
                    break;
                case R.id.second:
                    selected = choice_adapter.getItem(1).toString();
                    break;
                case R.id.third:
                    selected = choice_adapter.getItem(2).toString();
                    break;
                case R.id.fourth:
                    selected = choice_adapter.getItem(3).toString();
                    break;
                default:
                    break;
            }
            checkAnswer();
            nextProblemSetting();
            example_set.setText(Character.toString(alphabet[answer_number]));
        }
    };



    public void randWordPlace(){
        //문제의 답안이 될 알파벳을 랜덤 추출한다.
        answer_number = example_generator.nextInt(26);
        Random orderShuffle = new Random();
        int order = orderShuffle.nextInt(3);
        //선지에 답안이 될 알파벳을 삽입한다.
        choices[3]=alphabet[answer_number];
        //알파벳을 제외한 다른 3개의 선지를 넣는다. 단, 그 전의 선정된 선지와 비교하여 겹치지 않도록 한다.
        for (int index=0; index<3; index++ ){
            generated[index] = example_generator.nextInt(25);
            switch(index){
                case 0:
                    if (generated[index]==answer_number)
                        generated[index]= generated[index]+1;
                        break;
                case 1:
                    if (generated[index]==answer_number|(generated[index]==generated[index-1])|(generated[index]==generated[index+1]))
                        generated[index]= generated[index]+1;
                        break;
                case 2:
                    if (generated[index]==answer_number|(generated[index]==generated[index-1])|(generated[index]==generated[index-2]))
                        generated[index]= generated[index]+1;
                        break;
                default:
                        break;

            }
            choices[index]=alphabet[generated[index]];
        }

        switch (order){
            case 0:
                choice_adapter.add(choices[0]);
                choice_adapter.add(choices[1]);
                choice_adapter.add(choices[2]);
                choice_adapter.add(choices[3]);
                break;
            case 1:
                choice_adapter.add(choices[1]);
                choice_adapter.add(choices[2]);
                choice_adapter.add(choices[3]);
                choice_adapter.add(choices[0]);
                break;
            case 2:
                choice_adapter.add(choices[2]);
                choice_adapter.add(choices[3]);
                choice_adapter.add(choices[0]);
                choice_adapter.add(choices[1]);
                break;
            case 3:
                choice_adapter.add(choices[3]);
                choice_adapter.add(choices[0]);
                choice_adapter.add(choices[1]);
                choice_adapter.add(choices[2]);
                break;
            default:
                break;
        }
    }

    public void nextProblemSetting(){
        set ++;
        if(set >10){
            Intent intent = new Intent(getApplicationContext(), MultipleTestResultActivity.class);
            startActivity(intent);
            finish();
        }
        problem_number.setText(set + "번");
        choice_adapter.clear();
        // ListView에 아이템 추가. 하나는 example_text 문자열, 나머지 셋은 example 글자를 제외한 랜덤의 세 가지 글자
        randWordPlace();

    }

    public void checkAnswer(){
        //정답일 경우
        if (Character.toString(alphabet[answer_number]).matches(selected)){
           correctSound();

        }
        //정답이 아닐 경우
        else{
            incorrectSound();
            wrongAnswer.add(alphabet[answer_number]);
            wrongAnswer_length++;
        }
    }
    public void correctSound(){
        correct = soundPool.load(this, correct, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(correct, 50, 50, 0, 0, 1);
            }
        });
    }
    public void incorrectSound(){
        m_vibrator.vibrate(1000);
        incorrect = soundPool.load(this, incorrect, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(incorrect, 50, 50, 0, 0, 1);
            }
        });
    }

    /**
     * 메시지 보내기
     * @param message 보낼 메시지
     */
    private void sendMessage(String message) {
        if (bluetoothService != null) {
            bluetoothService.sendMessage(message);
        }
    }


}
