package com.adalbero.app.lebenindeutschland.data.exam;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.adalbero.app.lebenindeutschland.controller.Store;
import com.adalbero.app.lebenindeutschland.data.question.Question;

/**
 * Created by Adalbero on 27/05/2017.
 */

public class ExamLand extends Exam {
    private String mLand;

    public ExamLand(String name, String subtitle) {
        super(name, subtitle);
    }

    @Override
    protected boolean onFilterQuestion(Question q) {
        return q.getTheme().equals(mLand);
    }

    @Override
    protected void createQuestionList() {
        String name = Store.getSelectedLandName();
        mLand = name;
        super.createQuestionList();
    }

    @Override
    public void resetQuestionList() {
        createQuestionList();
    }

    @Override
    public String getTitle(boolean showSize) {
        if (mLand == null) {
            return "Select Bundesland...";
        }

        int n = getSize();
        return mLand + (showSize ? " (" + n + ")" : "");
    }

    @Override
    public int onGetIconResource() {
        String code = Store.getSelectedLandCode();
        if (code == null) return super.onGetIconResource();

        String name = "wappen_" + code.toLowerCase();
        int resId = AppController.getResource("drawable", name);
        return resId;
    }

    @Override
    protected int onGetColorResource() {
        return R.color.colorLand;
    }

    public boolean isIconColor() {
        return true;
    }

//    @Override
//    public boolean onPrompt(Activity activity, ResultCallback callback) {
//        String code = Store.getSelectedLandCode();
//        if (code == null) {
//            activity.startActivity(new Intent(activity, SettingsActivity.class));
//
//            return true;
//        }
//
//        return false;
//    }


}
