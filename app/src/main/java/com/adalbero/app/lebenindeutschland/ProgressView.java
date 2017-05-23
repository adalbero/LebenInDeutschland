package com.adalbero.app.lebenindeutschland;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.adalbero.app.lebenindeutschland.data.Exam;

/**
 * Created by Adalbero on 19/05/2017.
 */

public class ProgressView extends View {
    Paint paint = new Paint();

    private int total;
    private int right;
    private int wrong;

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setProgress(Exam exam) {
        int total = exam.getCount();
        int answerd = exam.getResult().getAnswerdCount();
        int right = exam.getResult().countRightAnswers();

        this.total = total;
        this.right = right;
        this.wrong = answerd - right;

        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        int h = canvas.getHeight();
        int w = canvas.getWidth();

        int x1 = total == 0 ? 0 : (w * right) / total;
        int x2 = total == 0 ? 0 : (w * wrong) / total;

        int colorRight = ContextCompat.getColor(getContext(), R.color.colorRight);
        int colorWrong = ContextCompat.getColor(getContext(), R.color.colorWrong);
        int colorNotAnswerd = ContextCompat.getColor(getContext(), R.color.colorNotAnswerd);
        int colorBackground = ContextCompat.getColor(getContext(), R.color.colorAccent);

        paint.setStyle(Paint.Style.FILL);

//        paint.setColor(colorBackground);
//        canvas.drawRect(new RectF(0, 0, w, h), paint);

        int dx = w / total;
        int corner = h / 2;

        for (int i=0; i<total; i++) {
            paint.setColor( i < right ? colorRight : i < right + wrong ? colorWrong : colorNotAnswerd);
            canvas.drawRoundRect(new RectF(i*dx, 2, (i+1)*dx, h-2), corner, corner, paint);

        }
//        paint.setColor(colorRight);
//        canvas.drawRect(new RectF(0, 0, x1, h), paint);
//
//        paint.setColor(colorWrong);
//        canvas.drawRect(new RectF(x1, 0, x1+x2, h), paint);

        paint.setColor(Color.RED);
        canvas.drawRect(new RectF(w/2-2, 0, w/2+2, h), paint);
    }

}
