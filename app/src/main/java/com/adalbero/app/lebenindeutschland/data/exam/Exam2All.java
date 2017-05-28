package com.adalbero.app.lebenindeutschland.data.exam;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.data.Question;

/**
 * Created by Adalbero on 27/05/2017.
 */

public class Exam2All extends Exam2 {
    public Exam2All(String name) {
        super(name);
    }

    @Override
    protected boolean onFilter(Question q) {
        return !q.getAreaCode().equals("land");
    }

    @Override
    public int onGetIconResource() {
        return R.drawable.wappen_de;
    }

    public boolean isIconColor() {
        return true;
    }



}
