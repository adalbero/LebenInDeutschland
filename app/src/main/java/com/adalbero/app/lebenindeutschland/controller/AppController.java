package com.adalbero.app.lebenindeutschland.controller;

import android.app.Activity;
import android.app.Application;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.data.exam.Exam2;
import com.adalbero.app.lebenindeutschland.data.exam.Exam2All;
import com.adalbero.app.lebenindeutschland.data.exam.Exam2Area;
import com.adalbero.app.lebenindeutschland.data.exam.Exam2Header;
import com.adalbero.app.lebenindeutschland.data.exam.Exam2Land;
import com.adalbero.app.lebenindeutschland.data.exam.Exam2Random;
import com.adalbero.app.lebenindeutschland.data.exam.Exam2Search;
import com.adalbero.app.lebenindeutschland.data.exam.Exam2Tag;
import com.adalbero.app.lebenindeutschland.data.exam.Exam2Thema;
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
    private List<Exam2> mExamList;

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

    public static List<Exam2> getExamList() {
        AppController app = getInstance();

        if (app.mExamList == null) {
            loadExamList();
        }

        return app.mExamList;
    }

    // TODO: cache examList
    public static void loadExamList() {

        List<Exam2> examList = new ArrayList<>();

        examList.add(new Exam2Header("Questions"));
        examList.add(new Exam2All("Alle"));
        examList.add(new Exam2Land("Land"));
        examList.add(new Exam2Random("Test"));

        examList.add(new Exam2Header("Filter"));
        examList.add(new Exam2Search("Search"));
        examList.add(new Exam2Tag("Tags"));

        examList.add(new Exam2Header("By Thema"));
        examList.add(new Exam2Area("Politik in der Demokratie"));
        examList.add(new Exam2Area("Geschichte und Verantwortung"));
        examList.add(new Exam2Area("Mensch und Gesellschaft"));

        examList.add(new Exam2Header("By Topic"));
        List<String> themas = getQuestionDB().listAllTheme();
        for (String thema : themas) {
            examList.add(new Exam2Thema(thema));
        }

        examList.add(new Exam2Header(""));

        getInstance().mExamList = examList;
    }

    public static Exam2 getCurrentExam() {
        String name = Store.getExamName();
        return getExam(name);
    }

    public static Exam2 getExam(String name) {
        if (name != null) {
            for (Exam2 exam : getExamList()) {
                if (exam.getName().equals(name)) {
                    exam.onUpdate();
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

        AdView adView = (AdView) activity.findViewById(R.id.adView);

        if (adView == null) return;

        MobileAds.initialize(getInstance(), ADS_APP_ID);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(DEVICE_NEXUS_5X)
                .build();


        adView.loadAd(adRequest);
    }

}
