package com.dotincorp.touchingdot.X_DoNotUse;

import android.app.Activity;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import java.util.Locale;

/*
 * Example:
 * TTS dex = new TTS(this);
 * TTS.Speech = "Changing Sentence!";
 * thing.setOnClickListener(talky);
 * TTS.shutdownTTS();
 */

public class TTS implements OnClickListener, OnInitListener {

    private final int MY_DATA_CHECK_CODE = 1234;
    private Activity activity;
    private TextToSpeech myTTS;
    private Intent checkTTSIntent;
    public String speech;

    public TTS(Activity activity) {
        this.activity = activity;
        checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        activity.startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
    }

    /*
     * Callback to determine if a valid TTS engine is installed on the device
     * if there is initialize myTTS else start the intent to download one
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                myTTS = new TextToSpeech(activity, this);
            }
            else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                activity.startActivity(installTTSIntent);
            }
        }
    }

    /* Called when a TTS object is created
     * you can change the language here
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            if(myTTS.isLanguageAvailable(Locale.US)==TextToSpeech.LANG_AVAILABLE)
                myTTS.setLanguage(Locale.US);
        }
        else if (status == TextToSpeech.ERROR) {
            Toast.makeText(activity, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View arg0) {
        speakWords();
    }

    public void speakWords() {
        myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
    }

    /*Used to shutdown TTS which is important for service content leaks*/
    public void shutdownTTS(){
        if (myTTS != null)
        {
            myTTS.stop();
            myTTS.shutdown();
            Log.d("TTS", "TTS Destroyed");
        }
    }
}