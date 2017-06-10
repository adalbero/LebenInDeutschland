package com.adalbero.app.lebenindeutschland.data.exam;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.ui.common.ResultCallback;
import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.adalbero.app.lebenindeutschland.controller.Store;
import com.adalbero.app.lebenindeutschland.data.question.Question;
import com.adalbero.app.lebenindeutschland.data.question.QuestionComparator;
import com.adalbero.app.lebenindeutschland.data.question.QuestionDB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Adalbero on 27/05/2017.
 */

public class Exam {
    public static final String QUESTIONS = "questions";
    private String mName;
    private List<String> mQuestionList;

    public Exam(String name) {
        mName = name;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public String getTitle(boolean showSize) {
        return getName() + (showSize ? " (" + getSize() + ")" : "");
    }

    public int getSize() {
        return getQuestionList().size();
    }

    public List<String> getQuestionList() {
        if (mQuestionList == null) {
            loadQuestionList();

            if (mQuestionList == null) {
                createQuestionList();
            }
        }

        return mQuestionList;
    }

    protected void createQuestionList() {
        List<String> list = new ArrayList<>();

        String land = Store.getSelectedLandName();
        QuestionDB questionDB = AppController.getInstance().getQuestionDB();
        for (Question q : questionDB.listAll()) {
            if (q.getAreaCode().equals("land") & !q.getTheme().equals(land))
                continue;

            if (onFilterQuestion(q))
                list.add(q.getNum());
        }

        setQuestionList(list);
    }

    protected boolean onFilterQuestion(Question q) {
        return false;
    }

    public void resetQuestionList() {
        createQuestionList();
    }

    public void sortQuestionList(int method) {
        int reverse = (method >= 0 ? 1 : -1);
        method = Math.abs(method);

        List<String> list = getQuestionList();

        if (method == QuestionComparator.METHOD_SHUFFLE) {
            Collections.shuffle(list);
        } else {
            Comparator<String> comparator = new QuestionComparator(method, reverse);
            Collections.sort(list, comparator);
        }

        setQuestionList(list);
    }

    protected void setQuestionList(List<String> list) {
        mQuestionList = list;
        saveQuestionList();
    }

    protected void invalidateQuestionList() {
        String key = getParamKey(QUESTIONS);
        Store.remove(key);
        mQuestionList = null;
    }

    protected void loadQuestionList() {
        String key = getParamKey(QUESTIONS);
        mQuestionList = Store.getList(key);
    }

    protected void saveQuestionList() {
        String key = getParamKey(QUESTIONS);
        Store.setList(key, mQuestionList);
    }

    protected String getParamKey(String key) {
        return "exam." + getName() + "." + key;
    }

    public int getQuestionIdx(String num) {
        List<String> list = getQuestionList();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(num)) {
                return i;
            }
        }

        return -1;
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
