package com.adalbero.app.lebenindeutschland;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

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

    public void setProgress(int total, int right, int wrong) {
        this.total = total;
        this.right = right;
        this.wrong = wrong;
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        int h = canvas.getHeight();
        int w = canvas.getWidth();

        int x1 = (w * right) / total;
        int x2 = (w * wrong) / total;

        int colorRight = ContextCompat.getColor(getContext(), R.color.colorRight);
        int colorWrong = ContextCompat.getColor(getContext(), R.color.colorWrong);
        int colorNotAnswerd = ContextCompat.getColor(getContext(), R.color.colorNotAnswerd);

        paint.setStyle(Paint.Style.FILL);

        paint.setColor(colorNotAnswerd);
        canvas.drawRect(new RectF(0, 0, w, h), paint);

        paint.setColor(colorRight);
        canvas.drawRect(new RectF(0, 0, x1, h), paint);

        paint.setColor(colorWrong);
        canvas.drawRect(new RectF(x1, 0, x1+x2, h), paint);

        paint.setColor(Color.GRAY);
        canvas.drawRect(new RectF(w/2-1, 0, w/2+1, h), paint);
    }

}
