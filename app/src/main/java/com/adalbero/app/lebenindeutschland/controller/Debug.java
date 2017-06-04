package com.adalbero.app.lebenindeutschland.controller;

import android.preference.PreferenceManager;
import android.util.Log;

import com.adalbero.app.lebenindeutschland.data.exam.Exam2;
import com.adalbero.app.lebenindeutschland.data.exam.Exam2Random;
import com.adalbero.app.lebenindeutschland.data.question.Question;

import java.util.Map;
import java.util.Random;
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
            if (key.startsWith("stat.")) continue;

            Object value = prefs.get(key);

            Log.i("MyApp", key + " : " + value);
        }
    }

    public static void dumpStatistics() {
        Map<String, ?> prefs = PreferenceManager.getDefaultSharedPreferences(
                AppController.getInstance()).getAll();
        Set<String> keys = new TreeSet<>(prefs.keySet());

        Log.i("MyApp", "Dump Statistics");
        for (String key : keys) {
            if (key.startsWith("stat.")) {

                Object value = prefs.get(key);

                Log.i("MyApp", key + " : " + value);
            }
        }
    }

    public static void removeAll() {
        Map<String, ?> prefs = PreferenceManager.getDefaultSharedPreferences(
                AppController.getInstance()).getAll();
        Set<String> keys = new TreeSet<>(prefs.keySet());

        for (String key : keys) {
            if (key.startsWith("App Restrictions")) continue;
            if (key.startsWith("question.")) continue;
            if (key.startsWith("stat.")) continue;

            Store.remove(key);
        }
    }

    public static void removeExam() {
        Store.removeGroup("exam.");
    }

    public static void removePref() {
        Store.removeGroup("pref.");
    }

    public static void removeStat() {
        Store.removeGroup("stat.");
    }

    public static void doTest(int times) {
        float wrong = 0.2f;

        Statistics stat = Statistics.getInstance();
        Exam2 exam = new Exam2Random("Debug");
        Random random = new Random();

        for (int i=0; i<times; i++) {
            exam.onUpdate();
            for (String num : exam.getQuestions()) {
                Question q = AppController.getQuestionDB().findByNum(num);
                String answer = q.getAnswerLetter();
                if (random.nextFloat() <= wrong)
                    answer = "x";
                stat.addAnswer(q, answer);
            }
        }
    }
}
