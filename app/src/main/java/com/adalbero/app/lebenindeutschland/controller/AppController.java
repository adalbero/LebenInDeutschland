package com.adalbero.app.lebenindeutschland.controller;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.multidex.MultiDexApplication;

import android.util.DisplayMetrics;
import android.util.Log;

import com.adalbero.app.lebenindeutschland.BuildConfig;
import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.data.exam.Exam;
import com.adalbero.app.lebenindeutschland.data.exam.ExamAll;
import com.adalbero.app.lebenindeutschland.data.exam.ExamArea;
import com.adalbero.app.lebenindeutschland.data.exam.ExamExercise;
import com.adalbero.app.lebenindeutschland.data.exam.ExamHeader;
import com.adalbero.app.lebenindeutschland.data.exam.ExamLand;
import com.adalbero.app.lebenindeutschland.data.exam.ExamRandom;
import com.adalbero.app.lebenindeutschland.data.exam.ExamSearch;
import com.adalbero.app.lebenindeutschland.data.exam.ExamStat;
import com.adalbero.app.lebenindeutschland.data.exam.ExamTag;
import com.adalbero.app.lebenindeutschland.data.exam.ExamTheme;
import com.adalbero.app.lebenindeutschland.data.question.QuestionDB;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.UserMessagingPlatform;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adalbero on 16/05/2017.
 */

public class AppController extends MultiDexApplication {

    private static final String TAG = "lid:AppController";
    private static AppController mInstance;
    private FirebaseAnalytics mFirebaseAnalytics;
    private ConsentInformation consentInformation;
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
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getInstance());
        consentInformation = UserMessagingPlatform.getConsentInformation(getInstance());

        Analytics.logAppCreate(mFirebaseAnalytics);

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
        app.mQuestionDB.load();
    }

    public static List<Exam> getExamList() {
        AppController app = getInstance();

        if (app.mExamList == null) {
            loadExamList();
        }

        return app.mExamList;
    }

    public static void loadExamList() {

        List<Exam> examList = new ArrayList<>();

        examList.add(new ExamHeader("Questions"));
        examList.add(new ExamAll("Alle", "All general questions"));
        examList.add(new ExamLand("Land", "Questions specific to your Bundesland"));
        examList.add(new ExamExercise("Exercise", "Random questions not answered yet"));
        examList.add(new ExamRandom("Test", "Exam simulator. 33 Random questions"));

        examList.add(new ExamHeader("By Theme"));
        examList.add(new ExamArea("Politik in der Demokratie"));
        examList.add(new ExamArea("Geschichte und Verantwortung"));
        examList.add(new ExamArea("Mensch und Gesellschaft"));

        examList.add(new ExamHeader("By Topic"));
        List<String> themes = getQuestionDB().listAllTheme();
        for (String theme : themes) {
            examList.add(new ExamTheme(theme));
        }

        examList.add(new ExamHeader("Filter"));
        examList.add(new ExamSearch("Search", "Search questions by keyword"));
        examList.add(new ExamTag("Tags", "Search questions by tag"));

        examList.add(new ExamHeader("Statistics"));
        examList.add(new ExamStat("At least once wrong", "Questions answered at least one time wrong", ExamStat.FILTER_ONCE_WRONG));
        examList.add(new ExamStat("Mostly wrong", "Questions answered more wrong than right", ExamStat.FILTER_MOSTLY_WRONG));
        examList.add(new ExamStat("Last answer wrong", "Questions answered wrong the last time", ExamStat.FILTER_LAST_WRONG));
        examList.add(new ExamStat("Not answered yet", "Questions never answered", ExamStat.FILTER_NOT_ANSWERED));
        examList.add(new ExamStat("Last answer right", "Questions answered right the last time", ExamStat.FILTER_LAST_RIGHT));
        examList.add(new ExamStat("Mostly right", "Questions answered more right than wrong", ExamStat.FILTER_MOSTLY_RIGHT));

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

    public static int getImageResourceByName(String name) {
        return getInstance().getResources().getIdentifier(name, "drawable", getInstance().getPackageName());
    }

    public static Drawable getCompatDrawable(int resId) {
        return ContextCompat.getDrawable(getInstance(), resId);
    }

    public static int getCompatColor(int resId) {
        return ContextCompat.getColor(getInstance(), resId);
    }

    public static DisplayMetrics getDisplayMetrics() {
        return getInstance().getResources().getDisplayMetrics();
    }

    public static InputStream openRawResource(int resId) {
        return getInstance().getResources().openRawResource(resId);
    }

    public static void initAdView(Activity activity) {

        String DEVICE_PIXEL3 = "ED3FDF134EDDED51DCD128B2D18F9FE2";

        try {
            // Load AdView
            AdView adView = activity.findViewById(R.id.adView);
            if (adView == null) return;

            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    log("Ad loaded");
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    log("Ad failed: " + loadAdError.getMessage());
                }

                @Override
                public void onAdClicked() {
                    log("Ad clicked");
                }

                @Override
                public void onAdImpression() {
                    log("Ad impression");
                }

                private void log(String msg) {
                    String title = activity.getClass().getSimpleName();
                    Log.d(TAG, String.format("[%s] (Consent: %s) %s", title, Analytics.getConsentStatus(getInstance().consentInformation), msg));
                    Analytics.logAdsStatus(getInstance().mFirebaseAnalytics, getInstance().consentInformation, title, msg);
                }
            });

            if (BuildConfig.DEBUG) {
                List<String> testDevices = new ArrayList<>();
                testDevices.add(AdRequest.DEVICE_ID_EMULATOR);
                testDevices.add(DEVICE_PIXEL3);

                RequestConfiguration requestConfiguration
                        = new RequestConfiguration.Builder()
                        .setTestDeviceIds(testDevices)
                        .build();
                MobileAds.setRequestConfiguration(requestConfiguration);
            }

            Log.d(TAG, "adView.loadAd");
            adView.loadAd(new AdRequest.Builder().build());

        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
            FirebaseCrashlytics.getInstance().recordException(ex);
        }
    }

    public static void setExamIdx(int idx) {
        getInstance().mExamIdx = idx;
    }

    public static int getExamIdx() {
        return getInstance().mExamIdx;
    }
}
