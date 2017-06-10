package com.adalbero.app.lebenindeutschland.data.exam;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adalbero on 27/05/2017.
 */

public class ExamHeader extends Exam {
    private static List<String> EMPTY_LIST = new ArrayList<>();

    public ExamHeader(String name) {
        super(name);
    }

    @Override
    public String getTitle(boolean showSize) {
        return getName();
    }

    @Override
    protected void createQuestionList() {
        setQuestionList(EMPTY_LIST);
    }
}