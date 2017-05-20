package com.adalbero.app.lebenindeutschland.data;

import com.adalbero.app.lebenindeutschland.R;

/**
 * Created by Adalbero on 16/05/2017.
 */

public class Exam300 extends Exam {
    public Exam300(String name) {
        super(name);
    }

    @Override
    protected boolean isFilter(Question q) {
        return !q.getAreaCode().equals("land");
    }

    @Override
    public int getIcon() {
        return R.drawable.wappen_de;
    }
}
