package com.adalbero.app.lebenindeutschland.data.result;

/**
 * Created by Adalbero on 28/05/2017.
 */

public class ResultInfo {
    public int total;
    public int answered;
    public int right;
    public int wrong;

    public boolean isFinished() {
        return total > 0 && total == answered;
    }

    public float getAnsweredPerc() {
        float perc = total == 0 ? 0 : (float) (100 * answered / total);
        return perc;
    }

    public float getRightPerc() {
        float perc = answered == 0 ? 0 : (float) (100 * right / answered);
        return perc;
    }

    public boolean isPass() {
        return right >= (float)total/2;
    }
}
