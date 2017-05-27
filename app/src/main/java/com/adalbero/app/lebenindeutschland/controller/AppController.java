package com.adalbero.app.lebenindeutschland.controller;

import android.app.Activity;
import android.app.Application;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.data.Exam;
import com.adalbero.app.lebenindeutschland.data.Exam300;
import com.adalbero.app.lebenindeutschland.data.ExamByArea;
import com.adalbero.app.lebenindeutschland.data.ExamBySearch;
import com.adalbero.app.lebenindeutschland.data.ExamByTag;
import com.adalbero.app.lebenindeutschland.data.ExamByThema;
import com.adalbero.app.lebenindeutschland.data.ExamHeader;
import com.adalbero.app.lebenindeutschland.data.ExamLand;
import com.adalbero.app.lebenindeutschland.data.ExamSimulate;
import com.adalbero.app.lebenindeutschland.data.Question;
import com.adalbero.app.lebenindeutschland.data.QuestionDB;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Adalbero on 16/05/2017.
 */

public class AppController extends Application {

    private static AppController mInstance;

    private QuestionDB mQuestionDB;
    private List<Exam> mExamList;
    private ExamLand mExamLand;

    public static AppController getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("MyApp", "AppController.onCreate: ");

        mInstance = this;

//        getPreferences().edit().remove(SettingsActivity.PREF_LAND).commit();

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

        examList.add(new ExamHeader("Liste"));
        examList.add(new Exam300("Alle"));
        examList.add(getExamLand());

        examList.add(new ExamHeader("Themen"));
        examList.add(new ExamByArea("Politik in der Demokratie"));
        examList.add(new ExamByArea("Geschichte und Verantwortung"));
        examList.add(new ExamByArea("Mensch und Gesellschaft"));

        examList.add(new ExamHeader("Themengebiete"));
        List<String> themas = getQuestionDB().listAllTheme();
        for (String thema : themas) {
            examList.add(new ExamByThema(thema));
        }

        examList.add(new ExamHeader("Filter"));
        examList.add(new ExamBySearch("Search"));
        examList.add(new ExamByTag("Tags"));
        examList.add(new ExamSimulate("Probetest"));
//        Set<String> tags = getQuestionDB().getAllTags();
//        for (String tag : tags) {
//            ExamTag examTag = new ExamTag(tag);
//            examTag.init();
//            if (examTag.getCount() > 0)
//                examList.add(examTag);
//        }

        examList.add(new ExamHeader(""));

        getInstance().mExamList = examList;
    }

    // TODO: why persist?
    public static ExamLand getExamLand() {
        AppController app = getInstance();

        String landName = Store.getSelectedLandName();
        if (app.mExamLand == null) {
            app.mExamLand = new ExamLand(landName);
        }

        app.mExamLand.setLand(landName);

        return app.mExamLand;
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

    // TODO: why here?
    public static int countRightAnswers(Map<String, String> answers) {
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
