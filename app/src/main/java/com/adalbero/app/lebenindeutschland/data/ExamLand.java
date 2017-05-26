package com.adalbero.app.lebenindeutschland.data;

import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.adalbero.app.lebenindeutschland.controller.Store;

/**
 * Created by Adalbero on 17/05/2017.
 */

public class ExamLand extends ExamByArea {
    public ExamLand(String name) {
        super(name);
    }

    public void setLand(String name) {
        name = (name == null ? "* Select Bundesland" : name);

        mValue = name;
        this.mName = name;
        init();
    }

    @Override
    protected boolean isFilter(Question q) {
        return q.getTheme().equals(mValue);
    }

    @Override
    public int getColor() {
        return super.getColor();
    }

    @Override
    public int getIcon() {
        String code = Store.getSelectedLandCode();
        if (code == null) return super.getIcon();

        String name = "wappen_" + code.toLowerCase();
        int resId = AppController.getResource("drawable", name);
        return resId;
    }

}
