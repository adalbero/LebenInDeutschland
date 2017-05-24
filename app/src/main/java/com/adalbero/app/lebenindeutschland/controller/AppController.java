package com.adalbero.app.lebenindeutschland.controller;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.SettingsActivity;
import com.adalbero.app.lebenindeutschland.data.Exam;
import com.adalbero.app.lebenindeutschland.data.Exam300;
import com.adalbero.app.lebenindeutschland.data.ExamByArea;
import com.adalbero.app.lebenindeutschland.data.ExamBySearch;
import com.adalbero.app.lebenindeutschland.data.ExamByThema;
import com.adalbero.app.lebenindeutschland.data.ExamHeader;
import com.adalbero.app.lebenindeutschland.data.ExamLand;
import com.adalbero.app.lebenindeutschland.data.ExamSimulate;
import com.adalbero.app.lebenindeutschland.data.ExamTag;
import com.adalbero.app.lebenindeutschland.data.Question;
import com.adalbero.app.lebenindeutschland.data.QuestionDB;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Adalbero on 16/05/2017.
 */

public class AppController extends Application {

    private static AppController instance;

    private QuestionDB mQuestionDB;
    private List<Exam> mExamList;
    private ExamLand mExamLand;

    public static AppController getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

//        getPreferences().edit().remove(SettingsActivity.PREF_KEY_LAND).commit();

        loadQuestionDB();
        loadExamList();
    }

    private void loadQuestionDB() {
        mQuestionDB = new QuestionDB();

        mQuestionDB.load(this);
    }

    public void loadExamList() {
        mExamList = new ArrayList<>();

        mExamList.add(new ExamHeader("Liste"));
        mExamList.add(new ExamSimulate("Probetest"));
        mExamList.add(new Exam300("Alle"));

        updateLand();
        mExamList.add(mExamLand);

        mExamList.add(new ExamHeader("Themen"));
        mExamList.add(new ExamByArea("Politik in der Demokratie"));
        mExamList.add(new ExamByArea("Geschichte und Verantwortung"));
        mExamList.add(new ExamByArea("Mensch und Gesellschaft"));

        mExamList.add(new ExamHeader("Themengebiete"));
        List<String> themas = mQuestionDB.listAllTheme();
        for (String thema : themas) {
            mExamList.add(new ExamByThema(thema));
        }

        mExamList.add(new ExamHeader("Filter"));
        mExamList.add(new ExamBySearch("Search"));
        Set<String> tags = getQuestionDB().getAllTags();
        for (String tag : tags) {
            ExamTag examTag = new ExamTag(tag);
            examTag.init();
            if (examTag.getCount() > 0)
                mExamList.add(examTag);
        }

        mExamList.add(new ExamHeader(""));

    }

    public QuestionDB getQuestionDB() {
        return mQuestionDB;
    }

    public List<Exam> getExamList() {
        return mExamList;
    }

    public Exam getCurrentExam() {
        String name = getString("exam_name", "???");
        return getExam(name);
    }

    public Exam getExam(String name) {
        for (Exam exam : mExamList) {
            if (exam.getName().equals(name)) {
                return exam;
            }
        }

        return null;
    }

    public SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor edit = getPreferences().edit();
        edit.putString(key, value);
        edit.commit();
    }

    public String getString(String key, String def) {
        return getPreferences().getString(key, def);
    }

    public void putInt(String key, int value) {
        SharedPreferences.Editor edit = getPreferences().edit();
        edit.putInt(key, value);
        edit.commit();
    }

    public int getInt(String key, int def) {
        return getPreferences().getInt(key, def);
    }

    public int getResource(String type, String name) {
        int resourceId = this.getResources().getIdentifier(name, type, this.getPackageName());
        return resourceId;
    }

    public Drawable getDrawable(String name) {
        int resourceId = getResource("drawable", name);
        return this.getResources().getDrawable(resourceId);
    }

    public String getSelectedLandCode() {
        String value = getString(SettingsActivity.PREF_KEY_LAND, null);
        if (value == null) return null;
        return value.substring(0, 2);
    }

    public String getSelectedLandName() {
        String value = getString(SettingsActivity.PREF_KEY_LAND, null);
        if (value == null) return null;
        return value.substring(3);
    }

    public void updateLand() {
        String landName = getSelectedLandName();
        if (mExamLand == null) {
            mExamLand = new ExamLand(landName);
        }

        mExamLand.setLand(landName);
    }

    public int countRightAnswers(Map<String, String> answers) {
        int right = 0;
        for (String num : answers.keySet()) {
            String answer = answers.get(num);
            Question q = getQuestionDB().findByNum(num);
            if (q != null) {
                if (answer.charAt(0) - 'a' == q.getAnswer())
                    right++;
            }
        }

        return right;
    }


    public void initAdView(Activity activity) {
        String ADS_APP_ID = "ca-app-pub-5723913637413365~4650789131";
        String DEVICE_NEXUS_5X = "580cd7e67c712dc2";

        AdView adView = (AdView) activity.findViewById(R.id.adView);

        if (adView == null) return;

        MobileAds.initialize(getApplicationContext(), ADS_APP_ID);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(DEVICE_NEXUS_5X)
                .build();


        adView.loadAd(adRequest);
    }

    public int getBackgroundColor(int resource) {
        return ContextCompat.getColor(this, resource);
    }

}
