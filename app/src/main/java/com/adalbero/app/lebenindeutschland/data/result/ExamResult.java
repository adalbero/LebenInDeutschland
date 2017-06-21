package com.adalbero.app.lebenindeutschland.data.result;

import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.adalbero.app.lebenindeutschland.controller.Store;
import com.adalbero.app.lebenindeutschland.data.exam.Exam;
import com.adalbero.app.lebenindeutschland.data.question.Question;
import com.google.firebase.crash.FirebaseCrash;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.adalbero.app.lebenindeutschland.controller.AppController.getQuestionDB;

/**
 * Created by Adalbero on 28/05/2017.
 */

public class ExamResult {
    private static final String KEY_ANSWER_LIST = "exam.result.answers";

    private List<String> mAnswerList;
    private Exam mExam;

    public ExamResult() {
        Store.setInt(Store.KEY_RESULT_LOGGED, getResult().isFinished() ? 1 : 0);
    }

    public boolean isResultLooged() {
        return Store.getInt(Store.KEY_RESULT_LOGGED, 0) == 1;
    }

    public void setResultLogged() {
        Store.setInt(Store.KEY_RESULT_LOGGED, 1);
    }

    public Exam getExam() {
        if (mExam == null) {
            mExam = AppController.getCurrentExam();
        }

        return mExam;
    }

    public List<String> getAnswerList() {
        Exam exam = getExam();
        if (exam == null) {
            mAnswerList = null;
        } else if (mAnswerList == null) {
            mAnswerList = Store.getListWithNull(KEY_ANSWER_LIST);
            if (mAnswerList == null) {
                String answers[] = new String[exam.getSize()];
                mAnswerList = Arrays.asList(answers);
            }
        }

        return mAnswerList;
    }

    public Map<String, String> getAnswerMap() {
        Map<String, String> answerMap = new HashMap<>();
        List<String> answerList = getAnswerList();
        List<String> questionList = getExam().getQuestionList();

        int n = Math.min(questionList.size(), answerList.size());

        for (int i = 0; i < n; i++) {
            String num = questionList.get(i);
            answerMap.put(num, answerList.get(i));
        }

        return answerMap;
    }

    public void setAnswerMap(Map<String, String> answerMap) {
        List<String> questionList = getExam().getQuestionList();
        String answers[] = new String[questionList.size()];
        mAnswerList = Arrays.asList(answers);

        for (int i = 0; i < questionList.size(); i++) {
            String num = questionList.get(i);
            String answer = answerMap.get(num);
            mAnswerList.set(i, answer);
        }

        saveAnserList();
    }

    public void reset() {
        mAnswerList = null;
        getAnswerList();
    }

    public void saveAnserList() {
        Store.setList(KEY_ANSWER_LIST, getAnswerList());
    }

    public void setAnswer(String num, String answer) {
        int idx = getExam().getQuestionIdx(num);
        if (idx >= 0) {
            getAnswerList().set(idx, answer);
            saveAnserList();
        }
    }

    public String getAnswer(String num) {
        int idx = getExam().getQuestionIdx(num);
        List<String> answerList = getAnswerList();

        if (idx >= 0 && idx < answerList.size()) {
            return answerList.get(idx);
        } else {
            return null;
        }
    }

    public int getCount() {
        return getAnswerList().size();
    }

    public ResultInfo getResult() {
        Exam exam = getExam();
        List<String> answerList = getAnswerList();
        List<String> questionList = exam.getQuestionList();

        ResultInfo result = new ResultInfo();
        result.total = getCount();

        if (answerList.size() != questionList.size()) {
            FirebaseCrash.logcat(Log.DEBUG, "MyApp", "Exam: " + exam.getTitle(true));
            FirebaseCrash.logcat(Log.DEBUG, "MyApp", "questionList=" + questionList.toString());
            FirebaseCrash.logcat(Log.DEBUG, "MyApp", "answerList=" + answerList.toString());

            FirebaseCrash.report(new IndexOutOfBoundsException("questionList != answerList"));
        }

        int n = Math.min(questionList.size(), answerList.size());

        for (int i = 0; i < n; i++) {
            if (answerList.get(i) != null) {
                result.answered++;

                String answer = answerList.get(i);
                if (answer != null) {
                    String num = questionList.get(i);
                    Question q = getQuestionDB().getQuestion(num);
                    if (q != null) {
                        if (answer.charAt(0) - 'a' == q.getAnswer())
                            result.right++;
                    }
                }
            }

        }

        result.wrong = result.answered - result.right;

        return result;
    }

    public int getAnswerStatus(String num) {
        String answer = getAnswer(num);
        if (answer == null) return -1;

        Question q = AppController.getQuestionDB().getQuestion(num);

        if (answer.charAt(0) - 'a' == q.getAnswer())
            return 1;
        else
            return 0;
    }

    public int getStatusColor(String num) {
        int status = getAnswerStatus(num);
        int colorStatus = ContextCompat.getColor(AppController.getInstance(),
                status == 1 ? R.color.colorRight
                        : status == 0 ? R.color.colorWrong
                        : R.color.colorNotAnswerd);
        return colorStatus;
    }

}