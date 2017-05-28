package com.adalbero.app.lebenindeutschland.controller;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.adalbero.app.lebenindeutschland.SettingsActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Adalbero on 24/05/2017.
 */

public class Store {
    public static final String KEY_EXAM_NAME = "exam.name";
    public static final String KEY_QUESTION_IDX = "exam.question.idx";
    public static final String KEY_EXAM_INLINE = "pref.inline";
    public static final String KEY_TAGS_TERMS = "pref.Tags.terms";

    public static void remove(String key) {
        getPreferences().edit().remove(key).commit();
    }

    public static void removeGroup(String path) {
        if (!path.endsWith("."))
            path = path + ".";

        for (String key : getAllKeys()) {
            if (key.startsWith(path)) {
                remove(key);
            }
        }
    }

    public static List<String> getAllKeys() {
        Map<String, ?> prefs = PreferenceManager.getDefaultSharedPreferences(
                AppController.getInstance()).getAll();
        Set<String> keys = new TreeSet<>(prefs.keySet());

        return new ArrayList<>(keys);
    }

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
        String value = Store.getString(SettingsActivity.PREF_LAND, null);
        if (value == null) return null;
        return value.substring(0, 2);
    }

    public static String getSelectedLandName() {
        String value = Store.getString(SettingsActivity.PREF_LAND, null);
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

    public static void resetPrefs() {
        removeGroup("pref");
    }

    public static void resetExam() {
        removeGroup("exam");
    }

    public static void setTagTerms(Set<String> tags) {
        getPreferences().edit().putStringSet(KEY_TAGS_TERMS, tags).commit();
    }

    public static Set<String> getTagTerms() {
        return getPreferences().getStringSet(KEY_TAGS_TERMS, null);
    }

    public static void setList(String key, List<String> list) {
        String str = list.toString();
        setString(key, str);
    }

    public static List<String> getList(String key) {
        String str = getString(key, null);

        if (str == null) return null;

        str = str.substring(1, str.length() - 1);

        String[] vet = str.split(",");
        return Arrays.asList(vet);
    }

    public static void setSet(String key, Set<String> set) {
        List<String> list = new ArrayList<>(set);
        setList(key, list);
    }

    public static Set<String> getSet(String key) {
        List<String> list = getList(key);

        if (list == null) return null;

        return new TreeSet<>(list);
    }
}
