package com.adalbero.app.lebenindeutschland.data.exam;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.controller.Statistics;
import com.adalbero.app.lebenindeutschland.data.question.Question;

/**
 * Created by Adalbero on 03/06/2017.
 */

public class ExamStat extends Exam {
    public static final int FILTER_ONCE_WRONG = 0;
    public static final int FILTER_MOSTLY_WRONG = 1;
    public static final int FILTER_LAST_WRONG = 2;
    public static final int FILTER_NOT_ANSWERED = 3;
    public static final int FILTER_LAST_RIGHT = 4;
    public static final int FILTER_MOSTLY_RIGHT = 5;

    private int colors[] = {
            R.color.Red_400,
            R.color.Red_200,
            R.color.Red_100,
            R.color.BlueGray_100,
            R.color.Green_100,
            R.color.Green_200
    };

    private Statistics mStat;
    private int mFilterMode;

    public ExamStat(String name, String subtitle, int filterMode) {
        super(name, subtitle);

        mFilterMode = filterMode;
    }

    @Override
    protected void createQuestionList() {
        mStat = Statistics.getInstance();
        super.createQuestionList();
    }

    @Override
    public int getIconResource() {
        return R.drawable.ic_stat;
    }

    @Override
    protected boolean onFilterQuestion(Question q) {
        String num = q.getNum();
        Statistics.Info info = mStat.getQuestionStat(num);

        switch (mFilterMode) {
            case FILTER_ONCE_WRONG:
                return info.isAnswered() && info.getNumWrong() > 0;
            case FILTER_MOSTLY_WRONG:
                return info.isAnswered() && info.getRightProgress() <= 0.5f;
            case FILTER_LAST_WRONG:
                return info.isAnswerWrong(0);
            case FILTER_NOT_ANSWERED:
                return !info.isAnswered();
            case FILTER_LAST_RIGHT:
                return info.isAnswerRight(0);
            case FILTER_MOSTLY_RIGHT:
                return info.isAnswered() && info.getRightProgress() > 0.5f;
            default:
                return false;
        }
    }

    @Override
    protected int onGetColorResource() {
        return colors[mFilterMode];
    }


}
