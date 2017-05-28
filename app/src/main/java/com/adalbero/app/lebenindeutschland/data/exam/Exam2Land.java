package com.adalbero.app.lebenindeutschland.data.exam;

import android.app.Activity;
import android.content.Intent;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.ResultCallback;
import com.adalbero.app.lebenindeutschland.SettingsActivity;
import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.adalbero.app.lebenindeutschland.controller.Store;
import com.adalbero.app.lebenindeutschland.data.Question;

/**
 * Created by Adalbero on 27/05/2017.
 */

public class Exam2Land extends Exam2 {
    private String mLand;

    public Exam2Land(String name) {
        super(name);
    }

    public void setLand(String name) {
        mLand = name;
        update();
    }

    @Override
    protected boolean onFilter(Question q) {
        return q.getTheme().equals(mLand);
    }

    @Override
    public void onUpdate() {
        String name = Store.getSelectedLandName();
        setLand(name);
    }

    @Override
    public String getTitle() {
        if (mLand == null) {
            return "Select Bundesland...";
        }

        int n = getSize();
        return mLand + " (" + n + ")";
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

    @Override
    public boolean onPrompt(Activity activity, ResultCallback callback) {
        String code = Store.getSelectedLandCode();
        if (code == null) {
            activity.startActivity(new Intent(activity, SettingsActivity.class));

            return true;
        }

        return false;
    }


}
