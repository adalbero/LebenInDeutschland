package com.adalbero.app.lebenindeutschland.data;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Adalbero on 17/05/2017.
 */

public class ExamBySelect extends Exam {
    private List<String> select;

    public ExamBySelect(String name, String select) {
        super(name);
        this.select = Arrays.asList(select.split(","));
    }

    @Override
    protected boolean isFilter(Question q) {
        String num = q.getNum();
        boolean result = select.contains(num);
        return result;
    }

}
