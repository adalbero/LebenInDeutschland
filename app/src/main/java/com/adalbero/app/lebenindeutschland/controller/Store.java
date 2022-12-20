package com.adalbero.app.lebenindeutschland.controller;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
    public static final String KEY_RESULT_LOGGED = "exam.result.logged";

    public static final String PREF_INLINE_MODE = "pref.inline.mode";
    public static final String PREF_LAND = "pref.land";
    public static final String PREF_VERSION = "pref.version";
    public static final String PREF_STAT_MAX = "pref.stat.max";
    public static final String PREF_REMOVE_STAT = "pref.remove.stat";
    public static final String PREF_EXERCISE_SIZE = "pref.exercise.size";
    public static final String PREF_POLICY = "pref.policy";

    public static void remove(String key) {
        getPreferences().edit().remove(key).commit();
    }

    public static void removeGroup(String path) {
        SharedPreferences.Editor edit = getPreferences().edit();

        if (!path.endsWith("."))
            path = path + ".";

        for (String key : getAllKeys()) {
            if (key.startsWith(path)) {
                edit.remove(key);
            }
        }

        edit.commit();
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

    public static void setBoolean(String key, boolean value) {
        SharedPreferences.Editor edit = getPreferences().edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

    public static boolean getBoolean(String key, boolean def) {
        return getPreferences().getBoolean(key, def);
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
        String value = Store.getString(PREF_LAND, null);
        if (value == null) return null;
        return value.substring(0, 2);
    }

    public static String getSelectedLandName() {
        String value = Store.getString(PREF_LAND, null);
        if (value == null) return null;
        return value.substring(3);
    }

    public static void setExamInline(boolean inline) {
        Store.setBoolean(PREF_INLINE_MODE, inline);
    }

    public static boolean getExamInline() {
        boolean inline = Store.getBoolean(PREF_INLINE_MODE, false);
        return inline;
    }

    public static void resetExam() {
        removeGroup("exam");
    }

    public static void resetExamResult() {
        removeGroup("exam.result");
    }

    public static void setList(String key, List<String> list) {
        String str = list.toString();
        setString(key, str);
    }

    public static List<String> getList(String key) {
        return getList(key, false);
    }

    public static List<String> getListWithNull(String key) {
        return getList(key, true);
    }

    private static List<String> getList(String key, boolean canNull) {
        String str = getString(key, null);

        if (str == null) return null;

        str = str.substring(1, str.length() - 1);

        String[] vet = str.split(",");
        List<String> list = Arrays.asList(vet);

        for (int i=0; i<list.size(); i++) {
            String value = list.get(i);
            value = value.trim();

            if (canNull && value.equals("null")) {
                value = null;
            }

            list.set(i, value);
        }

        return list;
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

    public static void setLand(String land) {
        setString(Store.PREF_LAND, land);
    }

    public static String getLand() {
        return getString(Store.PREF_LAND, null);
    }


    public static int getExcerciseSize() {
        return Integer.parseInt(getString(PREF_EXERCISE_SIZE, "20"));
    }
}
