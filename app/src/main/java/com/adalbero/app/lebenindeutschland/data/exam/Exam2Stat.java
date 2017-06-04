package com.adalbero.app.lebenindeutschland.data.exam;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.controller.Statistics;
import com.adalbero.app.lebenindeutschland.data.question.Question;

/**
 * Created by Adalbero on 03/06/2017.
 */

public class Exam2Stat extends Exam2 {
    public static final int FILTER_MOSTLY_WRONG = 0;
    public static final int FILTER_LAST_WRONG = 1;
    public static final int FILTER_NOT_RESPONDED = 2;
    public static final int FILTER_LAST_RIGHT = 3;
    public static final int FILTER_MOSTLY_RIGHT = 4;

    private int colors[] = {
            R.color.Red_200,
            R.color.Red_100,
            R.color.BlueGray_100,
            R.color.Green_100,
            R.color.Green_200
    };

    private Statistics mStat;
    private int mFilterMode;

    public Exam2Stat(String name, int filterMode) {
        super(name);

        mFilterMode = filterMode;
    }

    @Override
    public void onCreate() {
        mStat = Statistics.getInstance();
        super.onCreate();
    }

    @Override
    public void onUpdate() {
        update();
    }

    @Override
    protected int onGetIconResource() {
        return R.drawable.ic_stat;
    }

    @Override
    protected boolean onFilter(Question q) {
        String num = q.getNum();
        Statistics.Info info = mStat.getQuestionStat(num);

        switch (mFilterMode) {
            case FILTER_NOT_RESPONDED:
                return !info.isResponded();
            case FILTER_LAST_RIGHT:
                return info.isAnswerRight(0);
            case FILTER_LAST_WRONG:
                return info.isAnswerWrong(0);
            case FILTER_MOSTLY_RIGHT:
                return info.isResponded() && info.getRating() > 0.5f;
            case FILTER_MOSTLY_WRONG:
                return info.isResponded() && info.getRating() <= 0.5f;
            default:
                return false;
        }
    }

    @Override
    protected int onGetColorResource() {
//        return R.color.colorStat;
        return colors[mFilterMode];
    }


}
