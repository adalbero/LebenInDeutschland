package com.adalbero.app.lebenindeutschland.data;

import android.support.v4.content.ContextCompat;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.controller.AppController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adalbero on 16/05/2017.
 */

public class Exam {
    protected String mName;
    protected List<String> mQuestionNumList;
    protected ResultExam mResult;

    public Exam(String name) {
        this.mName = name;
    }

    protected void filterQuestions() {
        List<String> list = new ArrayList<>();
        QuestionDB questionDB = AppController.getInstance().getQuestionDB();
        for (Question q : questionDB.listAll()) {
            if (isFilter(q))
                list.add(q.getNum());
        }

        mQuestionNumList = list;
    }

    public String getName() {
        return mName;
    }

    public void init() {
        filterQuestions();
        mResult = new ResultExam();
    }

    public ResultExam getResult() {
        return mResult;
    }

    public String getTitle() {
        return getName() + " (" + getCount() + ")";
    }

    public List<String> getQuestionNumList() {
        return mQuestionNumList;
    }

    public int getCount() {
        if (mQuestionNumList == null) return 0;
        return getQuestionNumList().size();
    }

    protected boolean isFilter(Question q) {
        return false;
    }

    public int getColor() {
        return AppController.getInstance().getBackgroundColor(R.color.colorArea0);
//        return R.color.colorArea0;
    }

    private String getKey(String key) {
        return getName() + "." + key;
    }

    public void putString(String key, String value) {
        key = getKey(key);
        AppController.getInstance().putString(key, value);
    }

    public String getString(String key, String def) {
        key = getKey(key);
        return AppController.getInstance().getString(key, def);
    }

    public int getIcon() {
        return R.drawable.ic_exam;
    }

    public int getStatusColor(String num) {
        int status = getResult().getAnswerStatus(num);
        int colorStatus = ContextCompat.getColor(AppController.getInstance(),
                status == 1 ? R.color.colorRight
                        : status == 0 ? R.color.colorWrong
                        : R.color.colorNotAnswerd);
        return colorStatus;
    }
}

