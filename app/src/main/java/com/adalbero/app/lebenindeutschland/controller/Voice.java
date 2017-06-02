package com.adalbero.app.lebenindeutschland.controller;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Created by Adalbero on 01/06/2017.
 */

public class Voice {
    public static final int SPEECH_REQUEST_CODE = 0;

    private TextToSpeech mTTS;

    public Voice(Context context) {
        mTTS = new TextToSpeech(context, ttsListener);
    }

    public void shutdown() {
        if (mTTS != null) {
            mTTS.shutdown();
        }
    }

    public void speak(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTTS.speak(text, TextToSpeech.QUEUE_ADD, null, "DEFAULT");
        } else {
            mTTS.speak(text, TextToSpeech.QUEUE_ADD, null);
        }
    }

    public boolean speechRecognizer(Activity activity, String language, String prompt) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        if (language != null) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "de-DE");
        }

        if (prompt != null) {
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, prompt);
        }

        try {
            activity.startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (ActivityNotFoundException ex) {
            return false;
        }

        return true;
    }


    private TextToSpeech.OnInitListener ttsListener =
            new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(final int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        mTTS.setLanguage(Locale.GERMAN);
                    } else {
                        Log.d("MyApp", "Error starting the text to speech engine.");
                    }
                }
            };

}
