package com.adalbero.app.lebenindeutschland.data;

import com.adalbero.app.lebenindeutschland.R;

/**
 * Created by Adalbero on 17/05/2017.
 */

public class ExamWithPicture extends Exam {
    public ExamWithPicture(String name) {
        super(name);
    }

    @Override
    protected boolean isFilter(Question q) {
        return q.getImage() != null;
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_tag;
    }

}
