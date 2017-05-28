package com.adalbero.app.lebenindeutschland.data.exam;

import com.adalbero.app.lebenindeutschland.data.question.Question;

/**
 * Created by Adalbero on 27/05/2017.
 */

public class Exam2Thema extends Exam2Area {
    public Exam2Thema(String name) {
        super(name);
    }

    @Override
    protected boolean onFilter(Question q) {
        return q.getTheme().equals(mName);
    }

}
