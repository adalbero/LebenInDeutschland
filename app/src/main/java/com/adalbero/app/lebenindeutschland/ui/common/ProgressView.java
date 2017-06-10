package com.adalbero.app.lebenindeutschland.ui.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.data.result.ResultInfo;

/**
 * Created by Adalbero on 19/05/2017.
 */

public class ProgressView extends View {
    Paint paint = new Paint();

    private ResultInfo mResultInfo;

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setProgress(ResultInfo resultInfo) {
        mResultInfo = resultInfo;

        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        int h = canvas.getHeight();
        int w = canvas.getWidth();
        RectF rectF = new RectF();

        int colorRight = ContextCompat.getColor(getContext(), R.color.colorRight);
        int colorWrong = ContextCompat.getColor(getContext(), R.color.colorWrong);
        int colorNotAnswerd = ContextCompat.getColor(getContext(), R.color.colorNotAnswerd);

        paint.setStyle(Paint.Style.FILL);

        float dx = (mResultInfo.total == 0 ? w : (float) w / mResultInfo.total);
        float corner = h / 2;

        if (dx < corner)
            corner = 0;

        for (int i = 0; i < mResultInfo.total; i++) {
            paint.setColor(i < mResultInfo.right ? colorRight : i < mResultInfo.answered ? colorWrong : colorNotAnswerd);
            rectF.set(i * dx, 2, (i + 1) * dx, h - 2);
            canvas.drawRoundRect(rectF, corner, corner, paint);
        }

        paint.setColor(Color.RED);
        rectF.set(w / 2 - 2, 0, w / 2 + 2, h);
        canvas.drawRect(rectF, paint);
    }

}
