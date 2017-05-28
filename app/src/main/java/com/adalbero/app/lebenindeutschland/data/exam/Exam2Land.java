package com.adalbero.app.lebenindeutschland.data.exam;

import android.app.Activity;
import android.content.Intent;

import com.adalbero.app.lebenindeutschland.SettingsActivity;
import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.adalbero.app.lebenindeutschland.controller.Store;
import com.adalbero.app.lebenindeutschland.data.Question;

/**
 * Created by Adalbero on 27/05/2017.
 */

public class Exam2Land extends Exam2 {
    public Exam2Land(String name) {
        super(name);
    }

    public void setName(String name) {
        mName = name;
        update();
    }

    @Override
    protected boolean onFilter(Question q) {
        return q.getTheme().equals(mName);
    }

    @Override
    public void onUpdate() {
        String name = Store.getSelectedLandName();
        setName(name);
    }

    public String getTitle() {
        int n = getSize();
        return getName() + " (" + n + ")";
    }

    @Override
    public int onGetIconResource() {
        String code = Store.getSelectedLandCode();
        if (code == null) return super.onGetIconResource();

        String name = "wappen_" + code.toLowerCase();
        int resId = AppController.getResource("drawable", name);
        return resId;
    }

    public boolean isIconColor() {
        return true;
    }



    public boolean onPrompt(Activity activity) {
        String code = Store.getSelectedLandCode();
        if (code == null) {
            activity.startActivity(new Intent(activity, SettingsActivity.class));

            return true;
        }

        return false;
    }


}
