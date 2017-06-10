package com.adalbero.app.lebenindeutschland.controller;

import android.os.Bundle;
import android.util.Log;

import com.adalbero.app.lebenindeutschland.data.result.ExamResult;
import com.adalbero.app.lebenindeutschland.data.result.ResultInfo;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by Adalbero on 10/06/2017.
 */

public class Analytics {
    private static boolean mLogResult;

    public static void logUserProperty(FirebaseAnalytics firebase, String key, String value) {
        key = key.replaceAll("[. ]", "_");
        firebase.setUserProperty(key, value);
    }

    public static void setLogResult(boolean logResult) {
        mLogResult = logResult;
    }

    public static void logTestResult(FirebaseAnalytics firebase, Clock mClock, ExamResult mResult) {
        if (mLogResult) {
            ResultInfo resultInfo = mResult.getResult();
            Bundle bundle = new Bundle();

            bundle.putString("exam_name", mResult.getExam().getTitle(false));
            bundle.putLong("exam_timestamp", System.currentTimeMillis());
            bundle.putString("exam_time", mClock.getTimeString());
            bundle.putInt("exam_duration", mClock.getTime());
            bundle.putInt("exam_total", resultInfo.getTotal());
            bundle.putFloat("exam_right", resultInfo.getRightPerc());

            firebase.logEvent("exam_finish", bundle);

            Log.d("MyApp", "Analytics.logTestResult: ");
        }

        setLogResult(false);
    }
}
