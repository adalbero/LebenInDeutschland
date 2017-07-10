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

    public static final String LID_FEATURE = "lid_feature";
    public static final String LID_EXAM_FINISH = "lid_exam_finish";
    public static final String LID_QUESTION_ANSWERED = "lid_question_answered";

    public static final String LID_PARAM_FEATURE_NAME = "lid_feature_name";
    public static final String LID_PARAM_FEATURE_VALUE = "lid_feature_value";
    public static final String LID_PARAM_QUESTION_NUM = "lid_question_num";
    public static final String LID_PARAM_EXAM_NAME = "lid_exam_name";
    public static final String LID_PARAM_EXAM_SCORE = "lid_exam_score";
    public static final String LID_PARAM_EXAM_DURATION = "lid_exam_duration_sec";
    public static final String LID_PARAM_QUESTION_RIGHT = "lid_question_right";

    private static void logFeature(FirebaseAnalytics firebaseAnalytics, String name, String value, Exam exam, Question question) {

        Bundle bundle = new Bundle();
        bundle.putString(LID_PARAM_FEATURE_NAME, name);
        if (value != null)
            bundle.putString(LID_PARAM_FEATURE_VALUE, value);
        if (exam != null)
            bundle.putString(LID_PARAM_EXAM_NAME, exam.getName());
        if (question != null)
            bundle.putString(LID_PARAM_QUESTION_NUM, question.getNum());

        firebaseAnalytics.logEvent(LID_FEATURE, bundle);
    }

    public static void logFeatureLand(FirebaseAnalytics firebaseAnalytics, String land, boolean isNew) {

        if (land.length() > 3 && land.charAt(2) == ';') {
            land = land.substring(3);
        }

        String featureName = isNew ? "Select Land" : "Change Land";

        logFeature(firebaseAnalytics, featureName, land, null, null);
    }

    public static void logFeatureStatHistory(FirebaseAnalytics firebaseAnalytics, String value) {
        logFeature(firebaseAnalytics, "Stat History", value, null, null);
    }

    public static void logFeatureDebug(FirebaseAnalytics firebaseAnalytics, String value) {
        logFeature(firebaseAnalytics, "Debug", value, null, null);
    }

    public static void logFeatureClearStat(FirebaseAnalytics firebaseAnalytics) {
        logFeature(firebaseAnalytics, "Clear Stat", null, null, null);
    }

    public static void logFeatureSearch(FirebaseAnalytics firebaseAnalytics, Exam exam) {
        String name = exam.getName() + " List";
        logFeature(firebaseAnalytics, name, exam.getQualification(), exam, null);
    }

    public static void logFeatureExamStat(FirebaseAnalytics firebaseAnalytics, Exam exam) {
        int rating = Statistics.getInstance().getRatingInt(exam.getQuestionList());

        logFeature(firebaseAnalytics, "Exam Stat", "" + rating, exam, null);
    }

    public static void logFeatureSort(FirebaseAnalytics firebaseAnalytics, String value, Exam exam) {
        logFeature(firebaseAnalytics, "Sort", value, exam, null);
    }

    public static void logFeatureInlineMode(FirebaseAnalytics firebaseAnalytics, String value, Exam exam) {
        logFeature(firebaseAnalytics, "Inline Mode", value, exam, null);
    }

    public static void logFeatureTagged(FirebaseAnalytics firebaseAnalytics, Question question) {
        logFeature(firebaseAnalytics, "Question Tagged", question.getTags().toString(), null, question);
    }

    public static void logFeatureQuestionStat(FirebaseAnalytics firebaseAnalytics, Question question) {
        int rating = Statistics.getInstance().getQuestionStat(question.getNum()).getRatingInt();

        logFeature(firebaseAnalytics, "Question Stat", "" + rating, null, question);
    }

    public static void logFeatureSpeak(FirebaseAnalytics firebaseAnalytics, Question question) {
        logFeature(firebaseAnalytics, "Speak", null, null, question);
    }

    public static void logFeatureVoice(FirebaseAnalytics firebaseAnalytics, String value, Question question) {
        logFeature(firebaseAnalytics, "Voice", value, null, question);
    }

    public static void logFeatureTranslate(FirebaseAnalytics firebaseAnalytics, String value, Question question) {
        logFeature(firebaseAnalytics, "Translate", value, null, question);
    }

    public static void logExamFinish(FirebaseAnalytics firebaseAnalytics, Clock clock, ExamResult result) {

        if (result.isResultLooged()) return;
        result.setResultLogged();

        Bundle bundle = new Bundle();
        bundle.putString(LID_PARAM_EXAM_NAME, result.getExam().getName());
        bundle.putInt(LID_PARAM_EXAM_DURATION, clock.getExamDuration());
        bundle.putInt(LID_PARAM_EXAM_SCORE, (int) result.getResult().getRightPerc());

        firebaseAnalytics.logEvent(LID_EXAM_FINISH, bundle);
    }

    public static void logQuestionAnswer(FirebaseAnalytics firebaseAnalytics, ExamResult result, Question question) {

        Bundle bundle = new Bundle();
        bundle.putString(LID_PARAM_EXAM_NAME, result.getExam().getName());
        bundle.putString(LID_PARAM_QUESTION_NUM, question.getNum());
        bundle.putInt(LID_PARAM_QUESTION_RIGHT, result.getAnswerStatus(question.getNum()));

        firebaseAnalytics.logEvent(LID_QUESTION_ANSWERED, bundle);
    }

}
