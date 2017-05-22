package com.adalbero.app.lebenindeutschland.data;

import com.adalbero.app.lebenindeutschland.R;

/**
 * Created by Adalbero on 21/05/2017.
 */

public class ExamTag extends Exam {
    private String tag;

    public ExamTag(String name) {
        super("With tag: " + name);
        this.tag = name;
    }

    @Override
    protected boolean isFilter(Question q) {
        return q.hasTag(tag);
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_tag;
    }
}
