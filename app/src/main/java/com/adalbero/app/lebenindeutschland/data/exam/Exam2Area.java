package com.adalbero.app.lebenindeutschland.data.exam;

import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.adalbero.app.lebenindeutschland.data.Question;

/**
 * Created by Adalbero on 27/05/2017.
 */

public class Exam2Area extends Exam2 {
    private int mColor;

    public Exam2Area(String name) {
        super(name);
    }

    @Override
    protected boolean onFilter(Question q) {
        return q.getArea().equals(mName);
    }

    @Override
    public int getColor() {
        if (mColor == 0) {
            if (getSize() == 0) {
                mColor = super.getColor();
            } else {
                String num = mQuestionNumList.get(0);
                mColor = AppController.getInstance().getQuestionDB().findByNum(num).getAreaColor();
            }
        }

        return mColor;
    }

}
