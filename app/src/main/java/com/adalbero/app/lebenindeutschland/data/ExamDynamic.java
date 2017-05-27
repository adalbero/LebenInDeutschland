package com.adalbero.app.lebenindeutschland.data;

import android.app.Activity;

import com.adalbero.app.lebenindeutschland.ResultCallback;

/**
 * Created by Adalbero on 19/05/2017.
 */

public class ExamDynamic extends Exam {
    public ExamDynamic(String name) {
        super(name);
    }

    public void build(Activity activity, ResultCallback callback) {

    }

    @Override
    protected boolean isFilter(Question q) {
        return false;
    }

}
