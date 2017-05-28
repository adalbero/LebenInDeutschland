package com.adalbero.app.lebenindeutschland.controller;

import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Adalbero on 28/05/2017.
 */

public class Debug {

    public static void dumpSharedPreferences() {
        Map<String, ?> prefs = PreferenceManager.getDefaultSharedPreferences(
                AppController.getInstance()).getAll();
        Set<String> keys = new TreeSet<>(prefs.keySet());
        for (String key : keys) {
            if (key.startsWith("question.")) continue;

            Object value = prefs.get(key);

            Log.d("MyApp", key + " : " + value);
        }
    }

    public static void removeAll() {
        Map<String, ?> prefs = PreferenceManager.getDefaultSharedPreferences(
                AppController.getInstance()).getAll();
        Set<String> keys = new TreeSet<>(prefs.keySet());

        for (String key : keys) {
            if (key.startsWith("App Restrictions")) continue;
            if (key.startsWith("question.")) continue;

            Store.remove(key);
        }
    }


}
