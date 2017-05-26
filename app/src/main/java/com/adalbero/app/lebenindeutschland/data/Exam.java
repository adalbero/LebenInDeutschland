package com.adalbero.app.lebenindeutschland.data;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.adalbero.app.lebenindeutschland.JSONUtil;
import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.adalbero.app.lebenindeutschland.controller.Store;

import org.json.JSONArray;
import org.json.JSONException;

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
        String land = Store.getSelectedLandName();
        QuestionDB questionDB = AppController.getInstance().getQuestionDB();
        for (Question q : questionDB.listAll()) {
            if (q.getAreaCode().equals("land") & !q.getTheme().equals(land))
                continue;

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
    }

    private String getKey(String key) {
        return "pref." + getName() + "." + key;
    }

    public void putString(String key, String value) {
        key = getKey(key);
        Store.setString(key, value);
    }

    public String getString(String key, String def) {
        key = getKey(key);
        return Store.getString(key, def);
    }

    public int getIcon() {
        return R.drawable.ic_exam;
    }

    public int getStatusColor(String num) {
        int status = getResult().getAnswerStatus(num);
        int colorStatus = ContextCompat.getColor(AppController.getInstance(),
                status == 1 ? R.color.colorRightDark
                        : status == 0 ? R.color.colorWrongDark
                        : R.color.colorNotAnswerd);
        return colorStatus;
    }

    public void saveState(Bundle bundle) {
        try {
            String questions = JSONUtil.toJSONString(mQuestionNumList);
            String result = getResult().toJSONString();

            bundle.putString("exam.questions", questions);
            bundle.putString("exam.result", result);

        } catch (JSONException e) {
            Log.e("MyApp", "Exam.saveSate: " + e.getMessage(), e);
        }
    }

    public void restoreState(Bundle bundle) {
        try {
            String questions = bundle.getString("exam.questions");
            String result = bundle.getString("exam.result");

            JSONArray jsonArray = JSONUtil.toJSONArray(questions);
            mQuestionNumList = JSONUtil.toStringList(jsonArray);
            mResult = ResultExam.fromJSON(result);

        } catch (JSONException e) {
            Log.e("MyApp", "Exam.restoreState: " + e.getMessage(), e);
        }
    }
}

