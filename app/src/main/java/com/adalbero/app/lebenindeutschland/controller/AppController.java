package com.adalbero.app.lebenindeutschland.controller;

import android.app.Activity;
import android.app.Application;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.data.exam.Exam;
import com.adalbero.app.lebenindeutschland.data.exam.ExamAll;
import com.adalbero.app.lebenindeutschland.data.exam.ExamArea;
import com.adalbero.app.lebenindeutschland.data.exam.ExamHeader;
import com.adalbero.app.lebenindeutschland.data.exam.ExamLand;
import com.adalbero.app.lebenindeutschland.data.exam.ExamRandom;
import com.adalbero.app.lebenindeutschland.data.exam.ExamSearch;
import com.adalbero.app.lebenindeutschland.data.exam.ExamStat;
import com.adalbero.app.lebenindeutschland.data.exam.ExamTag;
import com.adalbero.app.lebenindeutschland.data.exam.ExamThema;
import com.adalbero.app.lebenindeutschland.data.question.QuestionDB;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adalbero on 16/05/2017.
 */

public class AppController extends Application {

    private static AppController mInstance;

    private QuestionDB mQuestionDB;
    private List<Exam> mExamList;
    private int mExamIdx;

    public static AppController getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        loadQuestionDB();
        loadExamList();
    }

    public static QuestionDB getQuestionDB() {
        AppController app = getInstance();

        if (app.mQuestionDB == null) {
            loadQuestionDB();
        }

        return app.mQuestionDB;
    }

    private static void loadQuestionDB() {
        AppController app = getInstance();

        app.mQuestionDB = new QuestionDB();
        app.mQuestionDB.load(app);
    }

    public static List<Exam> getExamList() {
        AppController app = getInstance();

        if (app.mExamList == null) {
            loadExamList();
        }

        return app.mExamList;
    }

    // TODO: cache examList
    public static void loadExamList() {

        List<Exam> examList = new ArrayList<>();

        examList.add(new ExamHeader("Questions"));
        examList.add(new ExamAll("Alle"));
        examList.add(new ExamLand("Land"));
        examList.add(new ExamRandom("Test"));

        examList.add(new ExamHeader("By Thema"));
        examList.add(new ExamArea("Politik in der Demokratie"));
        examList.add(new ExamArea("Geschichte und Verantwortung"));
        examList.add(new ExamArea("Mensch und Gesellschaft"));

        examList.add(new ExamHeader("By Topic"));
        List<String> themas = getQuestionDB().listAllTheme();
        for (String thema : themas) {
            examList.add(new ExamThema(thema));
        }

        examList.add(new ExamHeader("Filter"));
        examList.add(new ExamSearch("Search"));
        examList.add(new ExamTag("Tags"));

        examList.add(new ExamHeader("Statistics"));
        examList.add(new ExamStat("At least once wrong", ExamStat.FILTER_ONCE_WRONG));
        examList.add(new ExamStat("Mostly wrong", ExamStat.FILTER_MOSTLY_WRONG));
        examList.add(new ExamStat("Last answer wrong", ExamStat.FILTER_LAST_WRONG));
        examList.add(new ExamStat("Not answered yet", ExamStat.FILTER_NOT_ANSWERED));
        examList.add(new ExamStat("Last answer right", ExamStat.FILTER_LAST_RIGHT));
        examList.add(new ExamStat("Mostly right", ExamStat.FILTER_MOSTLY_RIGHT));

        examList.add(new ExamHeader(""));

        getInstance().mExamList = examList;
    }

    public static Exam getCurrentExam() {
        String name = Store.getExamName();
        return getExam(name);
    }

    public static Exam getExam(String name) {
        if (name != null) {
            for (Exam exam : getExamList()) {
                if (exam.getName().equals(name)) {
                    return exam;
                }
            }
        }

        return null;
    }

    public static int getResource(String type, String name) {
        int resourceId = getInstance().getResources().getIdentifier(name, type, getInstance().getPackageName());
        return resourceId;
    }

    public static Drawable getDrawable(String name) {
        int resourceId = getResource("drawable", name);
        return getInstance().getResources().getDrawable(resourceId);
    }

    public static int getBackgroundColor(int resource) {
        return ContextCompat.getColor(getInstance(), resource);
    }

    public static void initAdView(Activity activity) {
        String ADS_APP_ID = "ca-app-pub-5723913637413365~4650789131";
        String DEVICE_NEXUS_5X = "4218740A6FE03A56FFF5F7EA8E178378";

        AdView adView = activity.findViewById(R.id.adView);

        if (adView == null) return;

        MobileAds.initialize(getInstance(), ADS_APP_ID);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(DEVICE_NEXUS_5X)
                .build();


        adView.loadAd(adRequest);
    }

    public static void setExamIdx(int idx) {
        getInstance().mExamIdx = idx;
    }

    public static int getExamIdx() {
        return getInstance().mExamIdx;
    }
}
