package com.adalbero.app.lebenindeutschland.data.result;

import android.support.v4.content.ContextCompat;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.adalbero.app.lebenindeutschland.controller.Store;
import com.adalbero.app.lebenindeutschland.data.exam.Exam;
import com.adalbero.app.lebenindeutschland.data.question.Question;

import java.util.Arrays;
import java.util.List;

import static com.adalbero.app.lebenindeutschland.controller.AppController.getQuestionDB;

/**
 * Created by Adalbero on 28/05/2017.
 */

public class Exam2Result {
    private static final String KEY_ANSWER_LIST = "exam.result.answers";

    private List<String> mAnswerList;
    private Exam mExam;

    public Exam2Result() {
//        getAnswerList();
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

    public void reset() {
        mAnswerList = null;
        getAnswerList();
    }

    public void saveAnserList() {
        Store.setList(KEY_ANSWER_LIST, getAnswerList());
    }

    public void setAnswer(String num, String answer) {
        int idx = getExam().getQuestionIdx(num);
        getAnswerList().set(idx, answer);
        saveAnserList();
    }

    public String getAnswer(String num) {
        int idx = getExam().getQuestionIdx(num);
        if (idx < 0) return null;
        return getAnswerList().get(idx);
    }

    public int getCount() {
        return getAnswerList().size();
    }

    public ResultInfo getResult() {
        Exam exam = getExam();
        List<String> answerList = getAnswerList();

        ResultInfo result = new ResultInfo();
        result.total = getCount();

        for (int i = 0; i < answerList.size(); i++) {
            if (answerList.get(i) != null) {
                result.answered++;

                String answer = answerList.get(i);
                if (answer != null) {
                    String num = exam.getQuestionList().get(i);
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