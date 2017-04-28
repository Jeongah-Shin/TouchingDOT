package com.dotincorp.touchingdot;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class AlphabetSongActivity extends Activity implements Runnable, SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    /**
     * 알파벳 송을 재생하는 Activity
     */
    TextView title;
    SeekBar seekBar;
    TextView current_section;
    Button play;
    Button pause;
    Button stop;
    Button next;
    Button previous;

    Timer mTimer;
    TimerTask mTask;
    MediaPlayer mPlayer;

    int[] area = {8290, 11250, 14200, 17500, 19000, 20120, 23120, 26090, 49100, 52000, 55050, 58550, 60000, 61120, 64050, 68000, 90000, 92550, 96020, 99520, 100800, 101200, 104400, 107200};
    String[] alphabet_set = {"ABCD", "EFG", "HIJK", "LMNO", "P", "QRS", "TUV", "WXYZ"};
    String[] alphabet_br_set = {"01030919", "110b1b00", "130a1a05", "070d1d15", "0f000000", "1f170e00", "le252700", "3a2d3d35"};
    int a = 1; //current area position
    int max = area.length - 1; //whole parts number

    BleApplication bleApplication;

    @Override
    protected void onResume() {
        super.onResume();
        bleApplication = (BleApplication) getApplication();
        mPlayer = MediaPlayer.create(AlphabetSongActivity.this, R.raw.abcsong);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alphabet_song);
        viewBinder();
    }

    public void onClick(View v) {
        //play 버튼을 눌렀을 때, 이미 재생이 진행된 상태이면 이어지는 시점 부터 시작하고, 재생이 안되어있는 상태이면 처음부터 시작한다.
        if (v.getId() == R.id.play) {
            if (mPlayer != null && mPlayer.isPlaying()) return;
            if (seekBar.getProgress() > 0) {
                mPlayer.start();
                new Thread(this).start();
                return;
            }
            mPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mPlayer.getDuration());
            new Thread(this).start();

            //TODO 원활하게 작동이 안되므로 수정 필요한 부분
           // TimerTask mTask를 통해 닷워치에 해당 알파벳을 보내고, runOnUiThread를 통해 해당 알파벳을 화면에 보여준다.
            mTask = new TimerTask() {
                @Override
                public void run() {
                    if ((area[a - 1] + 600 < mPlayer.getCurrentPosition()) && (mPlayer.getCurrentPosition() < area[a] - 600)) {
                        bleApplication.sendBraille(alphabet_br_set[a]);
                    }
                    runOnUiThread(new Thread(new Runnable() {
                        public void run() {
                            if ((area[a - 1] + 600 < mPlayer.getCurrentPosition()) && (mPlayer.getCurrentPosition() < area[a] - 600)) {
                                current_section.setText(alphabet_set[a]);
                            }
                        }
                    }));
                }
            };
            mTimer = new Timer();
            mTimer.schedule(mTask, 500, 500);
        }
        //일시정지 버튼
        if ((v.getId() == R.id.pause) && (mPlayer != null)) {
            mPlayer.pause();
        }
        //정지 버튼, mPlayer의 재생 위치를 0으로 초기화 한다.
        if ((v.getId() == R.id.stop) && (mPlayer != null)) {
            mPlayer.stop();
            mTask.cancel();
            try {
                mPlayer.prepare();
            } catch (IOException ie) {
                ie.printStackTrace();
            }
            mPlayer.seekTo(0);
        }
        //이전 구간, 이후 구간으로 점프할 수 있다.
        //TODO 닷워치 올라오는 부분이나 화면 위 UI 갱신 수정이 필요하다.
        if (v.getId() == R.id.previous && (mPlayer != null)) {
            a--;
            if (a < 0) {
                a = max;
            }
            mPlayer.seekTo(area[a] - 500);
//            runOnUiThread(new Thread(new Runnable() {
//                public void run() {
//                    current_section.setText(alphabet_set[a]);
//                    bleApplication.sendBraille(alphabet_br_set[a]);
//                }
//            }));
            new Thread(this).start();
        }
        if (v.getId() == R.id.next && (mPlayer != null)) {
            a++;
            if (a > max) {
                a = 0;
            }
            mPlayer.seekTo(area[a] - 500);
//            runOnUiThread(new Thread(new Runnable() {
//                public void run() {
//                    current_section.setText(alphabet_set[a]);
//                    bleApplication.sendBraille(alphabet_br_set[a]);
//                }
//            }));
            new Thread(this).start();
        }

    }

    public void run() {
        int currentPosition = 0;
        int total = mPlayer.getDuration();
        while (mPlayer != null && currentPosition < total) {
            try {
                Thread.sleep(1000);
                currentPosition = mPlayer.getCurrentPosition();
            } catch (InterruptedException e) {
                return;
            } catch (Exception e) {
                return;
            }
            seekBar.setProgress(currentPosition);
        }
    }

    //기기 정보 불러오기
    // 의도치 않은 연결 해제 시, 다시 연결하기 위해 DeviceScanActivity에서 기기의 정보들을 SharedPreference에 저장했다.
    private String loadDeviceInfo(String index) {
        SharedPreferences pref = getSharedPreferences("device", Activity.MODE_APPEND);
        if (index == "address") {
            String mac = pref.getString("mac", "0");
            return mac;
        } else if (index == "name") {
            String device = pref.getString("name", "device");
            return device;
        } else {
            return "null";
        }
    }

    // arbitary moving of SeekBar by users
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        if (fromUser) {
            seekBar.setProgress(progress);
            mPlayer.seekTo(progress / mPlayer.getDuration());
        }

    }

    public void viewBinder() {
        // 액티비티 내 타이틀 설정
        title = (TextView) findViewById(R.id.title);
        title.setText("Alphabet Song");

        // 현 재생되고 있는 구간의 알파벳 화면에 표시
        current_section = (TextView) findViewById(R.id.current_section);

        seekBar = (SeekBar) findViewById(R.id.seekBar);

        // 음악 재생, 일시정지, 정지 버튼과 구간 이동 버튼 (이전, 다음)
        play = (Button) findViewById(R.id.play);
        play.setOnClickListener(this);
        pause = (Button) findViewById(R.id.pause);
        pause.setOnClickListener(this);
        stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(this);
        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(this);
        previous = (Button) findViewById(R.id.previous);
        previous.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) mPlayer.release();
        mTimer.cancel();
        mTask.cancel();
    }


}