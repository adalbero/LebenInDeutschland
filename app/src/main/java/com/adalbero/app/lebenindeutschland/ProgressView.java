package com.adalbero.app.lebenindeutschland;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.adalbero.app.lebenindeutschland.data.result.Exam2Result;

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

    public void setProgress(Exam2Result result) {
        int total = result.getCount();
        int answerd = result.getAnswerdCount();
        int right = result.countRightAnswers();

        this.total = total;
        this.right = right;
        this.wrong = answerd - right;

        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        int h = canvas.getHeight();
        int w = canvas.getWidth();
        RectF rectF = new RectF();

        int colorRight = ContextCompat.getColor(getContext(), R.color.colorRightDark);
        int colorWrong = ContextCompat.getColor(getContext(), R.color.colorWrongDark);
        int colorNotAnswerd = ContextCompat.getColor(getContext(), R.color.colorNotAnswerd);

        paint.setStyle(Paint.Style.FILL);

        int dx = (total == 0 ? w: w / total);
        int corner = h / 2;

        for (int i=0; i<total; i++) {
            paint.setColor( i < right ? colorRight : i < right + wrong ? colorWrong : colorNotAnswerd);
            rectF.set(i*dx, 2, (i+1)*dx, h-2);
            canvas.drawRoundRect(rectF, corner, corner, paint);

        }

        paint.setColor(Color.RED);
        rectF.set(w/2-2, 0, w/2+2, h);
        canvas.drawRect(rectF, paint);
    }

}
