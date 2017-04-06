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

// ---------------------------------------------------------------------------------------
public class AlphabetSongActivity extends Activity implements Runnable, SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    //imgBtn - play/pause/stop, previous, next
    //SeekBar - seekBar

    SeekBar seekBar;
    TextView current_section;
    Button play;
    Button pause;
    Button stop;
    Button next;
    Button previous;

    MediaPlayer mPlayer;
    int[] area = {8290, 11250, 14200, 17500, 19000, 20120, 23120, 26090, 49100, 52000, 55050, 58550, 60000, 61120, 64050, 68000, 90000, 92550, 96020, 99520, 100800, 101200, 104400, 107200};
    String[] br = {"01030919", "110b1b00", "130a1a05", "070d1d15", "0f000000", "1f170e00", "le252700", "3a2d3d35"};
    int a = 0; //current area position
    int max = area.length - 1; //whole parts number
    BleApplication bleApplication;

    @Override
    protected void onResume() {
        super.onResume();
        bleApplication = (BleApplication) getApplication();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alphabet_song);

        current_section = (TextView)findViewById(R.id.current_section);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        play = (Button)findViewById(R.id.play);
        play.setOnClickListener(this);
        pause = (Button)findViewById(R.id.pause);
        pause.setOnClickListener(this);
        stop = (Button)findViewById(R.id.stop);
        stop.setOnClickListener(this);
        next = (Button)findViewById(R.id.next);
        next.setOnClickListener(this);
        previous = (Button)findViewById(R.id.previous);
        previous.setOnClickListener(this);


    }

    public void onClick(View v) {
        if (v.getId() == R.id.play) {
            runOnUiThread(new Thread(new Runnable() {
                public void run() {
                    if ((area[a - 1] + 600 < mPlayer.getCurrentPosition()) && (mPlayer.getCurrentPosition() < area[a] - 600)) {
                        current_section.setText(area[a]);
                    }
                }
            }));
            if (mPlayer != null && mPlayer.isPlaying()) return;
            if (seekBar.getProgress() > 0) {
                mPlayer.start();
                new Thread(this).start();
                return;
            }
            mPlayer = MediaPlayer.create(AlphabetSongActivity.this, R.raw.abcsong);
            mPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mPlayer.getDuration());
            new Thread(this).start();
        }

        if ((v.getId() == R.id.pause) && (mPlayer != null)) {
            mPlayer.pause();
        }
        if ((v.getId() == R.id.stop) && (mPlayer != null)) {
            mPlayer.stop();
            try {
                mPlayer.prepare();
            } catch (IOException ie) {
                ie.printStackTrace();
            }
            mPlayer.seekTo(0);
        }
        if (v.getId() == R.id.previous && (mPlayer != null)) {
            a--;
            if (a < 0) {
                a = max;
            }
            mPlayer.seekTo(area[a] - 500);
            runOnUiThread(new Thread(new Runnable() {
                public void run() {
                    current_section.setText(area[a]);
                }
            }));
            brailleSync();
            new Thread(this).start();
        }
        if (v.getId() == R.id.next && (mPlayer != null)) {
            a++;
            if (a > max) {
                a = 0;
            }
            mPlayer.seekTo(area[a] - 500);
            runOnUiThread(new Thread(new Runnable() {
                public void run() {
                    current_section.setText(area[a]);
                }
            }));
            brailleSync();
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

    public void brailleSync() {
        int cp = mPlayer.getCurrentPosition();
        if ((area[a] - 500 <= cp) && (cp <= area[a + 1] - 500)) {
            //sendBraille(br[a]);
        }
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) mPlayer.release();
    }

}