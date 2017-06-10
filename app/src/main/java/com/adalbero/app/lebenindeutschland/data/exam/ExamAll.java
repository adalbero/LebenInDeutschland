package com.adalbero.app.lebenindeutschland.data.exam;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.data.question.Question;

/**
 * Created by Adalbero on 27/05/2017.
 */

public class ExamAll extends Exam {
    public ExamAll(String name) {
        super(name);
    }

    @Override
    protected boolean onFilterQuestion(Question q) {
        return !q.getAreaCode().equals("land");
    }

    @Override
    protected int onGetColorResource() {
        return R.color.colorAll;
    }

    @Override
    public int onGetIconResource() {
        return R.drawable.wappen_de;
    }

    public boolean isIconColor() {
        return true;
    }



}
