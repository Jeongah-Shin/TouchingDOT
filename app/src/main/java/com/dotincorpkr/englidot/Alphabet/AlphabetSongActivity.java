package com.dotincorpkr.englidot.Alphabet;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import com.dotincorpkr.englidot.BaseActivity;
import com.dotincorpkr.englidot.R;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

// ---------------------------------------------------------------------------------------
public class AlphabetSongActivity extends BaseActivity implements Runnable, SeekBar.OnSeekBarChangeListener {
    //imgBtn - btn_play/pause/btnstop, previous_area, next_area
    //SeekBar - seekBar

    @Bind(R.id.seekBar)
    SeekBar seekBar;

    MediaPlayer mPlayer;
    int[] area = {8290, 11250, 14200, 20120, 23120, 26090, 49100, 52000, 55050, 61120, 64050, 68000, 90000, 92550, 96020, 101200, 104400, 107200 };
    int a=0; //current area position
    int max=area.length-1; //whole parts number
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alphabet_song);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.btn_play,R.id.btn_stop, R.id.btn_pause, R.id.previous_area, R.id.next_area})
    public void musicController(View v){
        if (v.getId() == R.id.btn_play) {
            if (mPlayer != null && mPlayer.isPlaying()) return;
            if(seekBar.getProgress() > 0) {
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

        if ((v.getId() ==R.id.btn_pause) && (mPlayer!=null)) {
            mPlayer.pause();
        }
        if ((v.getId() ==R.id.btn_stop) && (mPlayer!=null)) {
            mPlayer.stop();
            try
            {
                mPlayer.prepare();
            }
            catch(IOException ie)
            {
                ie.printStackTrace();
            }
            mPlayer.seekTo(0);
        }
        if(v.getId() ==R.id.previous_area&& (mPlayer!=null)){
            a--;
            if (a<0){
                a = max;
            }
            mPlayer.seekTo(area[a]);
            new Thread(this).start();
        }
        if (v.getId() ==R.id.next_area&& (mPlayer!=null)){
            a++;
            if (a>max){
                a=0;
            }
            mPlayer.seekTo(area[a]);
            new Thread(this).start();
        }

    }

    public void run() {
        int currentPosition= 0;
        int total = mPlayer.getDuration();
        while (mPlayer!=null && currentPosition<total) {
            try {
                Thread.sleep(1000);
                currentPosition= mPlayer.getCurrentPosition();
            } catch (InterruptedException e) {
                return;
            } catch (Exception e) {
                return;
            }
            seekBar.setProgress(currentPosition);
        }
    }

    // arbitary moving of SeekBar by users
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        if(fromUser){
            seekBar.setProgress(progress);
            mPlayer.seekTo(progress/mPlayer.getDuration());
        }
    }


}