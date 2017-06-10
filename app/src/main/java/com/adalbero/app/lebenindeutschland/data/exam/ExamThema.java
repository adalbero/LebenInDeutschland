package com.adalbero.app.lebenindeutschland.data.exam;

import com.adalbero.app.lebenindeutschland.data.question.Question;

/**
 * Created by Adalbero on 27/05/2017.
 */

public class ExamThema extends ExamArea {
    public ExamThema(String name) {
        super(name);
    }

    @Override
    protected boolean onFilterQuestion(Question q) {
        return q.getTheme().equals(getName());
    }

}
