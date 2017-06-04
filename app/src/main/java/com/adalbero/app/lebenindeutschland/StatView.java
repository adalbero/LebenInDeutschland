package com.adalbero.app.lebenindeutschland;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.adalbero.app.lebenindeutschland.controller.Statistics;
import com.adalbero.app.lebenindeutschland.data.exam.Exam2;
import com.adalbero.app.lebenindeutschland.data.question.Question;

/**
 * Created by Adalbero on 02/06/2017.
 */

public class StatView extends View {
    Paint paint = new Paint();

    private String mQuestionNum;

    private float mProgress;
    private float mRating;

    Statistics mStat;

    public StatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mStat = Statistics.getInstance();
    }

    public void setQuestion(Question q) {
        mQuestionNum = q.getNum();

        invalidate();
    }


    public void setExam(Exam2 exam) {
        mProgress = (exam.getSize() == 0 ? 0f : mStat.getProgress(exam.getQuestions()));
        mRating = mStat.getRating(exam.getQuestions());

        mQuestionNum = null;

        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
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
        int size = mStat.getMax();

        int colorRight = ContextCompat.getColor(getContext(), R.color.colorRight);
        int colorWrong = ContextCompat.getColor(getContext(), R.color.colorWrong);
        int colorNotAnswerd = ContextCompat.getColor(getContext(), R.color.colorNotAnswerd);

        paint.setStyle(Paint.Style.FILL);

        float dx = (size == 0 ? w : (float) w / size);
        float corner = h / 2;

        if (dx < corner)
            corner = 0;

        for (int i = 0; i < size; i++) {
            paint.setColor(info.isAnswerRight(i) ? colorRight : info.isAnswerWrong(i) ? colorWrong : colorNotAnswerd);
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
        int colorNotAnswerd = ContextCompat.getColor(getContext(), R.color.colorNotAnswerd);

        paint.setStyle(Paint.Style.FILL);

        rect.set(0, 0, w, h);
        paint.setColor(colorNotAnswerd);
        canvas.drawOval(rect, paint);

        int startAngle = -90;
        int sweepAngle = (int) (360 * mProgress);
        paint.setColor(colorWrong);
        canvas.drawArc(rect, startAngle, sweepAngle, true, paint);

        sweepAngle = (int) (360 * mRating * mProgress);
        paint.setColor(colorRight);
        canvas.drawArc(rect, startAngle, sweepAngle, true, paint);

        float d = .25f;
        rect.set(0 + w*d, 0 + h*d, w - w*d, h - h*d);
        paint.setColor(Color.WHITE);
        canvas.drawOval(rect, paint);

        if (false) {
            String text = String.format("%d", (int) (mRating * 100));
            float x = w / 2;
            float y = ((h / 2) - ((paint.ascent() - paint.descent()) / 2));

            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(40);
            paint.setColor(Color.BLACK);
            canvas.drawText(text, x, y, paint);
        }
    }

}

