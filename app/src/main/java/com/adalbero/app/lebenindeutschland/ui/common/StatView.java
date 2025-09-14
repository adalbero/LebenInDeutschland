package com.adalbero.app.lebenindeutschland.ui.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.controller.Statistics;
import com.adalbero.app.lebenindeutschland.data.exam.Exam;
import com.adalbero.app.lebenindeutschland.data.question.Question;

import java.util.List;

/**
 * Created by Adalbero on 02/06/2017.
 */

public class StatView extends View {
    Paint paint = new Paint();

    private String mQuestionNum;

    private float mProgress;
    private float mRating;
    private float mLastRating;

    Statistics mStat;

    public StatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mStat = Statistics.getInstance();
    }

    public void setQuestion(Question q) {
        mQuestionNum = q.getNum();

        invalidate();
    }

    public void setExam(Exam exam) {
        List<String> list = exam.getQuestionList();

        mProgress = (exam.getSize() == 0 ? 0f : mStat.getAnswerProgress(list));
        mRating = mStat.getRightProgress(list);
        mLastRating = mStat.getLastRightProgress(list);

        mQuestionNum = null;

        invalidate();
    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        if (mQuestionNum != null) {
            drawRight(canvas);
        } else {
            drawRating(canvas);
        }
    }

    private void drawRight(Canvas canvas) {
        int h = canvas.getHeight();
        int w = canvas.getWidth();
        RectF rectF = new RectF();

        Statistics.Info info = mStat.getQuestionStat(mQuestionNum);
        int size = Statistics.getHistorySize();

        int colorRight = ContextCompat.getColor(getContext(), R.color.colorRight);
        int colorWrong = ContextCompat.getColor(getContext(), R.color.colorWrong);
        int colorNotAnswered = ContextCompat.getColor(getContext(), R.color.colorNotAnswered);

        paint.setStyle(Paint.Style.FILL);

        float dx = (size == 0 ? w : (float) w / size);
        float corner = (float) h / 2;

        if (dx < corner)
            corner = 0;

        for (int i = 0; i < size; i++) {
            paint.setColor(info.isAnswerRight(i) ? colorRight : info.isAnswerWrong(i) ? colorWrong : colorNotAnswered);
            rectF.set(i * dx, 2, (i + 1) * dx, h - 2);
            canvas.drawRoundRect(rectF, corner, corner, paint);
        }
    }

    private void drawRating(Canvas canvas) {
        int h = canvas.getHeight();
        int w = canvas.getWidth();
        RectF rect = new RectF();

        int colorRight = ContextCompat.getColor(getContext(), R.color.colorRightDark);
        int colorWrong = ContextCompat.getColor(getContext(), R.color.colorWrongDark);
        int colorLastRight = ContextCompat.getColor(getContext(), R.color.colorRight);
        int colorLastWrong = ContextCompat.getColor(getContext(), R.color.colorWrong);
        int colorNotAnswered = ContextCompat.getColor(getContext(), R.color.colorNotAnswered);
        int colorNotAnsweredLight = ContextCompat.getColor(getContext(), R.color.colorNotAnsweredLight);

        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        float x = w / 2f;
        float y = h / 2f;

        float r1 = Math.min(w, h) / 2f;
        float r2 = r1 * .6f;
        float r3 = r1 * .2f;

        rect.set(x - r1, y - r1, x + r1, y + r1);

        // Not answered
        paint.setColor(colorNotAnswered);
        canvas.drawOval(rect, paint);

        // Right answered
        int startAngle = -90;
        int sweepAngle = (int) (360 * mProgress);
        paint.setColor(colorWrong);
        canvas.drawArc(rect, startAngle, sweepAngle, true, paint);

        sweepAngle = (int) (360 * mRating * mProgress);
        paint.setColor(colorRight);
        canvas.drawArc(rect, startAngle, sweepAngle, true, paint);

        // Last answer
        rect.set(x - r2, y - r2, x + r2, y + r2);

        paint.setColor(colorNotAnsweredLight);
        canvas.drawOval(rect, paint);

        startAngle = -90;
        sweepAngle = (int) (360 * mProgress);
        paint.setColor(colorLastWrong);
        canvas.drawArc(rect, startAngle, sweepAngle, true, paint);

        sweepAngle = (int) (360 * mLastRating * mProgress);
        paint.setColor(colorLastRight);
        canvas.drawArc(rect, startAngle, sweepAngle, true, paint);

        rect.set(x - r3, y - r3, x + r3, y + r3);
        paint.setColor(Color.WHITE);
        canvas.drawOval(rect, paint);

    }

}

