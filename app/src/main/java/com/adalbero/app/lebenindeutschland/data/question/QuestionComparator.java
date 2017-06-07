package com.adalbero.app.lebenindeutschland.data.question;

import com.adalbero.app.lebenindeutschland.controller.Statistics;

import java.util.Comparator;

/**
 * Created by Adalbero on 07/06/2017.
 */

public class QuestionComparator implements Comparator<String> {
    public static final int METHOD_SHUFFLE = 0;
    public static final int METHOD_NUM = 1;
    public static final int METHOD_RATING = 2;

    private int mMethod;
    private int mReverse;
    private Statistics mStat;

    public QuestionComparator(int method, int reverse) {
        mMethod = method;
        mReverse = reverse;
        mStat = Statistics.getInstance();
    }


    @Override
    public int compare(String num1, String num2) {
        switch (mMethod) {
            case METHOD_RATING:
                return mReverse * compareRating(num1, num2);
            case METHOD_NUM:
            default:
                return mReverse * compareNum(num1, num2);
        }
    }

    private Integer getNum(String num) {
        try {
            return Integer.valueOf(num);
        } catch (Exception ex) {
            try {
                return 300 + Integer.valueOf(num.substring(3));
            } catch (Exception ex2) {
                return 0;
            }
        }
    }

    private int compareNum(String num1, String num2) {
        return getNum(num1).compareTo(getNum(num2));
    }

    private int compareRating(String num1, String num2) {
        Statistics.Info stat1 = mStat.getQuestionStat(num1);
        Statistics.Info stat2 = mStat.getQuestionStat(num2);
        int inv = 1;

        // greater rating...
        int r1 = (int) (100 * stat1.getRating());
        int r2 = (int) (100 * stat2.getRating());

        if (r1 != r2)
            return r1 - r2;

        // if 0 less answerd is better, if > 0 more answerd is better
        if (r1 == 0)
            inv = -1;

        r1 = stat1.getNumAnswered();
        r2 = stat2.getNumAnswered();

        if (r1 != r2)
            return inv * (r1 - r2);

        // lesser right...
        inv = -1;
        r1 = stat1.getNumRight();
        r2 = stat2.getNumRight();

        if (r1 != r2)
            return inv * (r1 - r2);

        // greater num
        return compareNum(num1, num2);
    }
}
