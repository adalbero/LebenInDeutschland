package com.adalbero.app.lebenindeutschland.data;

/**
 * Created by Adalbero on 17/05/2017.
 */

public class ExamHeader extends Exam {
    public ExamHeader(String name) {
        super(name);
    }

    @Override
    public String getTitle() {
        return getName();
    }

}
