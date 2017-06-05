package com.adalbero.app.lebenindeutschland.data.exam;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adalbero on 27/05/2017.
 */

public class Exam2Header extends Exam2 {
    private static List<String> EMPTY_LIST = new ArrayList<>();

    public Exam2Header(String name) {
        super(name);
    }

    @Override
    public String getTitle(boolean showSize) {
        return getName();
    }

    @Override
    protected List<String> onGetQuestions() {
        return EMPTY_LIST;
    }
}