package com.adalbero.app.lebenindeutschland.data;

/**
 * Created by Adalbero on 17/05/2017.
 */

public class ExamByThema extends ExamByArea {
    public ExamByThema(String name) {
        super(name);
    }

    @Override
    protected boolean isFilter(Question q) {
        return q.getTheme().equals(mValue);
    }

}
