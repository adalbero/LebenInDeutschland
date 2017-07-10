package com.adalbero.app.lebenindeutschland.controller;

import android.os.Bundle;

import com.adalbero.app.lebenindeutschland.data.exam.Exam;
import com.adalbero.app.lebenindeutschland.data.question.Question;
import com.adalbero.app.lebenindeutschland.data.result.ExamResult;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by Adalbero on 10/06/2017.
 */

public class Analytics {

    public static void logBundesland(FirebaseAnalytics firebaseAnalytics, String value) {

        if (value.length() > 3 && value.charAt(2) == ';') {
            value = value.substring(3);
        }

        logFeature(firebaseAnalytics, "Select Land", value);
    }

    public static void logFeature(FirebaseAnalytics firebaseAnalytics, String name, String value) {

        Bundle bundle = new Bundle();
        bundle.putString("lid_feature_name", name);
        bundle.putString("lid_feature_value", value);

        firebaseAnalytics.logEvent("lid_feature", bundle);
    }

    public static void logExamView(FirebaseAnalytics firebaseAnalytics, Exam exam) {

        Bundle bundle = new Bundle();
        bundle.putString("lid_exam_name", exam.getName());

        firebaseAnalytics.logEvent("lid_exam_view", bundle);
    }

    public static void logExamFinish(FirebaseAnalytics firebaseAnalytics, Clock mClock, ExamResult result) {

        if (result.isResultLooged()) return;
        result.setResultLogged();

        Bundle bundle = new Bundle();
        bundle.putString("lid_exam_name", result.getExam().getName());
        bundle.putInt("lid_exam_score", (int) result.getResult().getRightPerc());

        firebaseAnalytics.logEvent("lid_exam_finish", bundle);
    }

    public static void logSearch(FirebaseAnalytics firebaseAnalytics, Exam exam) {

        logFeature(firebaseAnalytics, exam.getName(), exam.getQualification());

        Bundle bundle = new Bundle();
        bundle.putString("lid_exam_name", exam.getName());
        bundle.putString("lid_search_terms", exam.getQualification());

        firebaseAnalytics.logEvent("lid_search", bundle);
    }

    public static void logQuestionAnswer(FirebaseAnalytics firebaseAnalytics, ExamResult result, String num) {

        Bundle bundle = new Bundle();
        bundle.putString("lid_question_num", num);
        bundle.putInt("lid_question_right", result.getAnswerStatus(num));

        firebaseAnalytics.logEvent("lid_question_answered", bundle);
    }

    public static void logQuestionTagged(FirebaseAnalytics firebaseAnalytics, Question question) {

        logFeature(firebaseAnalytics, "Question Tagged", question.getTags().toString());

        Bundle bundle = new Bundle();
        bundle.putString("lid_question_num", question.getNum());
        bundle.putString("lid_question_tags", question.getTags().toString());

        firebaseAnalytics.logEvent("lid_question_tagged", bundle);
    }

}
