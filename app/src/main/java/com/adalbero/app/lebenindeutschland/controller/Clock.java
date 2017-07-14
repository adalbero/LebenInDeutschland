package com.adalbero.app.lebenindeutschland.controller;

import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.adalbero.app.lebenindeutschland.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Adalbero on 28/05/2017.
 */

public class Clock {
    private static final String KEY_CLOCK_TIME = "exam.result.mCountDouwn.time";
    private static final String KEY_CLOCK_STOP = "exam.result.mCountDouwn.stop";
    private static final String KEY_CLOCK_DEFAULT = "param.timer";

    private TextView mClockView;
    private int mTime;
    private CountDownTimer mCountDouwn;
    private int mDefaultTime;

    public Clock(TextView clockView) {
        mClockView = clockView;
        mDefaultTime = Store.getInt(KEY_CLOCK_DEFAULT, 1 * 60 * 60);
    }

    public boolean start() {
        boolean stop = Store.getInt(KEY_CLOCK_STOP, 0) == 1;

        mTime = Store.getInt(KEY_CLOCK_TIME, mDefaultTime);
        boolean isNew = mTime == mDefaultTime;
        if (isNew) {
            Store.setInt(KEY_CLOCK_TIME, mTime);
        }

        if (mCountDouwn != null) {
            mCountDouwn.cancel();
        }

        if (stop) {
            updateView();
        } else {
            Store.setInt(KEY_CLOCK_STOP, 0);
            mCountDouwn = new CountDownTimer(mTime * 1000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    mTime = (int) (millisUntilFinished / 1000);
                    if (Store.getInt(KEY_CLOCK_TIME, -1) != -1) {
                        Store.setInt(KEY_CLOCK_TIME, mTime);
                        updateView();
                    }
                }

                @Override
                public void onFinish() {
                    mTime = 0;
                    Store.setInt(KEY_CLOCK_TIME, mTime);
                    updateView();
                }
            };

            mCountDouwn.start();
        }

        return isNew;
    }

    public int getExamDuration() {
        return 60 * 60 - mTime;
    }

    private String format(int timeInSec) {
        Date date = new Date(timeInSec * 1000);
        DateFormat formatter = new SimpleDateFormat(timeInSec > 60 * 60 ? "H:mm:ss" : "mm:ss");
        String text = formatter.format(date);

        return text;
    }

    private void updateView() {
        boolean stop = Store.getInt(KEY_CLOCK_STOP, 0) == 1;

        mClockView.setText(format(mTime));
        if (stop || isTimout()) {
            mClockView.setTextColor(ContextCompat.getColor(mClockView.getContext(), R.color.colorNotAnswerd));
        }

        if (isTimout()) {
            TextView mResultView = (TextView) mClockView.getRootView().findViewById(R.id.view_result);
            if (mResultView != null) {
                mResultView.setText("Time Out");
                mResultView.setVisibility(View.VISIBLE);
                mResultView.setTextColor(ContextCompat.getColor(mClockView.getContext(), R.color.colorWrong));
            }
        }
    }

    public void pause() {
        if (mCountDouwn != null)
            mCountDouwn.cancel();
    }

    public void stop() {
        Store.setInt(KEY_CLOCK_STOP, 1);
        if (mCountDouwn != null)
            mCountDouwn.cancel();
        updateView();
    }

    public boolean isTimout() {
        return mTime == 0;
    }
}
