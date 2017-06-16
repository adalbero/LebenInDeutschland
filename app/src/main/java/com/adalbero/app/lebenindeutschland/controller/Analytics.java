package com.adalbero.app.lebenindeutschland.controller;

import android.os.Bundle;

import com.adalbero.app.lebenindeutschland.data.result.ExamResult;
import com.adalbero.app.lebenindeutschland.data.result.ResultInfo;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by Adalbero on 10/06/2017.
 */

public class Analytics {
    private static boolean mLogResult;

    public static String getKey(String key) {
        return key.replaceAll("[. ]", "_");
    }

    public static void setUserProperty(FirebaseAnalytics firebase, String key, String value) {
        key = getKey(key);
        firebase.setUserProperty(key, value);
    }

    public static void setLogResult(boolean logResult) {
        mLogResult = logResult;
    }

    public static void logTestResult(FirebaseAnalytics firebaseAnalytics, Clock mClock, ExamResult mResult) {
        if (mLogResult) {
            ResultInfo resultInfo = mResult.getResult();
            Bundle bundle = new Bundle();

            bundle.putString("exam_name", mResult.getExam().getName());
            bundle.putLong("exam_timestamp", System.currentTimeMillis());
            bundle.putInt("exam_duration_sec", mClock.getExamDuration());
            bundle.putInt("exam_total", resultInfo.getTotal());
            bundle.putFloat("exam_right", resultInfo.getRightPerc());

            firebaseAnalytics.logEvent("lid_exam_finish", bundle);
        }

        setLogResult(false);
    }

    public static void logBundesland(FirebaseAnalytics firebaseAnalytics, String value) {
        String key = Store.PREF_LAND;

        setUserProperty(firebaseAnalytics, "lid_land", value);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.GROUP_ID, value);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.JOIN_GROUP, bundle);
    }

    public static void logSearch(FirebaseAnalytics firebaseAnalytics, String value) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, value);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle);
    }

    public static void logViewExam(FirebaseAnalytics firebaseAnalytics, String value) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, value);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, bundle);
    }

    public static void logShare(FirebaseAnalytics firebaseAnalytics, String value) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "text/plain");
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, value);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle);
    }

    public static void logFeature(FirebaseAnalytics firebaseAnalytics, String name, String value) {
        Bundle bundle = new Bundle();
        bundle.putString("lid_feature_name", name);
        bundle.putString("lid_feature_value", value);
        firebaseAnalytics.logEvent("lid_feature", bundle);
    }

}
