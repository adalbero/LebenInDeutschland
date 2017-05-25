package com.adalbero.app.lebenindeutschland.controller;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.adalbero.app.lebenindeutschland.SettingsActivity;

/**
 * Created by Adalbero on 24/05/2017.
 */

public class Store {
    public static final String KEY_EXAM_NAME = "exam_name";
    public static final String KEY_QUESTION_IDX = "question_idx";
    public static final String KEY_EXAM_INLINE = "exam.inline";


    public static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(AppController.getInstance());
    }

    public static void setString(String key, String value) {
        SharedPreferences.Editor edit = getPreferences().edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static String getString(String key, String def) {
        return getPreferences().getString(key, def);
    }

    public static void setInt(String key, int value) {
        SharedPreferences.Editor edit = getPreferences().edit();
        edit.putInt(key, value);
        edit.commit();
    }

    public static int getInt(String key, int def) {
        return getPreferences().getInt(key, def);
    }

    public static String getExamName() {
        String name = Store.getString(KEY_EXAM_NAME, null);
        return name;
    }

    public static void setExamName(String name) {
        Store.setString(KEY_EXAM_NAME, name);
    }

    public static int getQuestionIdx() {
        int idx = Store.getInt(KEY_QUESTION_IDX, 0);
        return idx;
    }

    public static void setQuestionIdx(int idx) {
        Store.setInt(KEY_QUESTION_IDX, idx);
    }

    public static String getSelectedLandCode() {
        String value = Store.getString(SettingsActivity.PREF_KEY_LAND, null);
        if (value == null) return null;
        return value.substring(0, 2);
    }

    public static String getSelectedLandName() {
        String value = Store.getString(SettingsActivity.PREF_KEY_LAND, null);
        if (value == null) return null;
        return value.substring(3);
    }

    public static void setExamInline(boolean inline) {
        Store.setInt(KEY_EXAM_INLINE, inline ? 1 : 0);
    }

    public static boolean getExamInline() {
        int inline = Store.getInt(KEY_EXAM_INLINE, 0);
        return (inline == 1);
    }

}
