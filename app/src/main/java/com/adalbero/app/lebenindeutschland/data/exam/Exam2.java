package com.adalbero.app.lebenindeutschland.data.exam;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.ResultCallback;
import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.adalbero.app.lebenindeutschland.controller.Store;
import com.adalbero.app.lebenindeutschland.data.question.Question;
import com.adalbero.app.lebenindeutschland.data.question.QuestionDB;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adalbero on 27/05/2017.
 */

public class Exam2 {
    protected String mName;
    protected List<String> mQuestionNumList;

    public Exam2(String name) {
        mName = name;

        onCreate();
    }

    public void setName(String name) {
        mName = name;
    }

    public List<String> getQuestions() {
        if (mQuestionNumList != null) {
            return mQuestionNumList;
        }

        mQuestionNumList = onGetQuestions();

        return mQuestionNumList;
    }

    public int getQuestionIdx(String num) {
        List<String> list = getQuestions();

        for (int i=0; i<list.size(); i++) {
            if (list.get(i).equals(num)) {
                return i;
            }
        }

        return -1;
    }

    protected List<String> onGetQuestions() {
        List<String> list = new ArrayList<>();

        String land = Store.getSelectedLandName();
        QuestionDB questionDB = AppController.getInstance().getQuestionDB();
        for (Question q : questionDB.listAll()) {
            if (q.getAreaCode().equals("land") & !q.getTheme().equals(land))
                continue;

            if (onFilter(q))
                list.add(q.getNum());
        }

        return list;
    }

    protected boolean onFilter(Question q) {
        return false;
    }

    public String getName() {
        return mName;
    }

    public String getTitle(boolean showSize) {
        return getName() + (showSize ? " (" + getSize() + ")" : "");
    }

    public int getSize() {
        int n = getQuestions().size();
        return n;
    }

    public void onCreate() {
        getQuestions();
    }

    public void onUpdate() {
    }

    protected void update() {
        mQuestionNumList = null;
        getQuestions();
    }

    protected String getParamKey(String key) {
        return "exam." + getName() + "." + key;
    }

    public boolean onPrompt(Activity activity, ResultCallback callback) {
        return false;
    }

    public int getColor() {
        int resId = onGetColorResource();
        return AppController.getInstance().getBackgroundColor(resId);
    }

    protected int onGetColorResource() {
        return R.color.colorExam;
    }

    public Drawable getIcon() {
        int resId = onGetIconResource();
        return AppController.getInstance().getResources().getDrawable(resId);
    }

    protected int onGetIconResource() {
        return R.drawable.ic_exam;
    }

    public boolean isIconColor() {
        return false;
    }

}
