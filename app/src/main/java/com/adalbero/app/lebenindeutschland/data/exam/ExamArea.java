package com.adalbero.app.lebenindeutschland.data.exam;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.adalbero.app.lebenindeutschland.data.question.Question;

/**
 * Created by Adalbero on 27/05/2017.
 */

public class ExamArea extends Exam {
    private int mColor;

    public ExamArea(String name) {
        super(name, null);
    }

    @Override
    public String getSubtitle() {
        return "by Theme";
    }

    @Override
    protected boolean onFilterQuestion(Question q) {
        return q.getArea().equals(getName());
    }

    @Override
    public int getColor() {
        if (mColor == 0) {
            if (getSize() == 0) {
                mColor = super.getColor();
            } else {
                String num = getQuestionList().get(0);
                mColor = AppController.getQuestionDB().getQuestion(num).getAreaColor();
            }
        }

        return mColor;
    }

    @Override
    public int getIconResource() {
        String area = getArea();

        if (area.startsWith("Politik"))
            return R.drawable.ic_theme_politic;
        else if (area.startsWith("Geschichte"))
            return R.drawable.ic_theme_history;
        else if (area.startsWith("Mensch"))
            return R.drawable.ic_theme_mensh;
        else
            return super.getIconResource();
    }

    protected String getArea() {
        return getTitle(false);
    }

}
