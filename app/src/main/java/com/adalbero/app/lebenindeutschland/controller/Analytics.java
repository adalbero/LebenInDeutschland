package com.adalbero.app.lebenindeutschland.controller;

import android.os.Bundle;

import com.adalbero.app.lebenindeutschland.data.exam.Exam;
import com.adalbero.app.lebenindeutschland.data.question.Question;
import com.adalbero.app.lebenindeutschland.data.result.ExamResult;
import com.adalbero.app.lebenindeutschland.data.result.ResultInfo;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by Adalbero on 10/06/2017.
 */

public class Analytics {

    public static String getKey(String key) {
        return key.replaceAll("[. ]", "_");
    }

    public static void init(FirebaseAnalytics firebaseAnalytics) {
        int debugAnalytics = Store.getInt(Store.DEBUG_ANALYTICS_VER, 0);
        int currentAnalytics = 1;

        if (debugAnalytics < currentAnalytics) {
            String land = Store.getString(Store.PREF_LAND, "NO_LAND");
            Analytics.logBundesland(firebaseAnalytics, land);

            Store.setInt(Store.DEBUG_ANALYTICS_VER, currentAnalytics);
        }

    }

    public static void setUserProperty(FirebaseAnalytics firebaseAnalytics, String key, String value) {
        key = getKey(key);
        firebaseAnalytics.setUserProperty(key, value);
    }

    public static void logUserId(FirebaseAnalytics firebaseAnalytics, String value) {
        firebaseAnalytics.setUserId(value);
    }

    public static void logBundesland(FirebaseAnalytics firebaseAnalytics, String value) {
        if (value.length() > 3 && value.charAt(2) == ';') {
            value = value.substring(3);
        }

        setUserProperty(firebaseAnalytics, "lid_land", value);

        logFeature(firebaseAnalytics, "Land", value);
    }

    public static void logFeature(FirebaseAnalytics firebaseAnalytics, String name, String value) {
        //value = name + ":" + value;

        Bundle bundle = new Bundle();
        bundle.putString("lid_feature_name", name);
        bundle.putString("lid_feature_value", value);
        firebaseAnalytics.logEvent("lid_feature", bundle);
    }

    public static void logExamView(FirebaseAnalytics firebaseAnalytics, Exam exam) {
        float rating = 100* Statistics.getInstance().getRating(exam.getQuestionList());

        Bundle bundle = new Bundle();
        bundle.putString("lid_exam_name", exam.getName());
        bundle.putInt("lid_exam_size", exam.getSize());
        bundle.putInt("lid_exam_rating", (int)rating);

        firebaseAnalytics.logEvent("lid_exam_view", bundle);
    }

    public static void logExamFinish(FirebaseAnalytics firebaseAnalytics, Clock mClock, ExamResult result) {
        if (result.isResultLooged()) return;

        ResultInfo resultInfo = result.getResult();
        Bundle bundle = new Bundle();

        bundle.putString("lid_exam_name", result.getExam().getName());
        bundle.putInt("lid_exam_duration_sec", mClock.getExamDuration());
        bundle.putInt("lid_exam_total", resultInfo.getTotal());
        bundle.putInt("lid_exam_right_perc", (int) resultInfo.getRightPerc());

        firebaseAnalytics.logEvent("lid_exam_finish", bundle);

        result.setResultLogged();
    }

    public static void logSearch(FirebaseAnalytics firebaseAnalytics, Exam exam) {
        String examName = exam.getName();
        String terms = exam.getQualification();
        int count = exam.getSize();

        Bundle bundle = new Bundle();
        bundle.putString("lid_exam_name", examName);
        bundle.putString("lid_search_terms", terms);
        bundle.putInt("lid_search_count", count);

        firebaseAnalytics.logEvent("lid_search", bundle);

        logFeature(firebaseAnalytics, examName, terms);
    }

    public static void logQuestionAnswer(FirebaseAnalytics firebaseAnalytics, ExamResult result, String num) {
        String examName = result.getExam().getName();
        String answer = result.getAnswer(num);
        int isRight = result.getAnswerStatus(num);
        float rating = 100 * Statistics.getInstance().getQuestionStat(num).getRating();

        Bundle bundle = new Bundle();
        bundle.putString("lid_exam_name", examName);
        bundle.putString("lid_question_num", num);
        bundle.putString("lid_question_answere", answer);
        bundle.putInt("lid_question_right", isRight);
        bundle.putInt("lid_question_rating", (int)rating);

        firebaseAnalytics.logEvent("lid_question_answered", bundle);
    }

    public static void logQuestionTagged(FirebaseAnalytics firebaseAnalytics, Question question) {
        String num = question.getNum();
        String tags = question.getTags().toString();
        Bundle bundle = new Bundle();
        bundle.putString("lid_question_num", num);
        bundle.putString("lid_question_tags", tags);

        firebaseAnalytics.logEvent("lid_question_tagged", bundle);

        logFeature(firebaseAnalytics, "Question Tagged", tags);

    }

}
