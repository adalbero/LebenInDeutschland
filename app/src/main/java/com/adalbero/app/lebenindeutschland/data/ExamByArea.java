package com.adalbero.app.lebenindeutschland.data;

import com.adalbero.app.lebenindeutschland.controller.AppController;

/**
 * Created by Adalbero on 16/05/2017.
 */

public class ExamByArea extends Exam {
    protected String mValue;
    protected int color = 0;

    public ExamByArea(String name) {
        super(name);
        mValue = name;
    }

    @Override
    protected boolean isFilter(Question q) {
        return q.getArea().equals(mValue);
    }

    @Override
    public int getColor() {
        if (color == 0) {
            if (getCount() == 0) {
                color = super.getColor();
            } else {
                String num = mQuestionNumList.get(0);
                color = AppController.getInstance().getQuestionDB().findByNum(num).getAreaColor();
            }
        }

        return color;
    }
}

