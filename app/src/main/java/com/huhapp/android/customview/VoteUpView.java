package com.huhapp.android.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by igbopie on 4/12/15.
 */
public class VoteUpView extends View {
    Paint paint;
    float x;
    float y;
    float containerWidth;
    float containerHeight;
    float radius;
    float stroke = 10;

    public VoteUpView(Context context) {
        super(context);
        init();
    }

    public VoteUpView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Try for a width based on our minimum
        containerWidth = MeasureSpec.getSize(widthMeasureSpec);
        containerHeight = MeasureSpec.getSize(heightMeasureSpec);
        radius =  Math.min(containerWidth / 2, containerHeight);
        y = containerHeight / 2;
        x = containerWidth / 2;

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paintUp = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintUp.setStyle(Paint.Style.FILL);
        //paintUp.setStrokeWidth(stroke);
        paintUp.setColor(adjustAlpha(Color.parseColor("#2ecc71"), .1f)); //#2ecc71

        //canvas.drawCircle(x, y, radius, paint);

        RectF rect = new RectF((containerWidth/2) - radius, containerHeight - radius, (containerWidth/2) + radius, containerHeight + radius);
        canvas.drawArc(rect, 0, -180, false, paintUp);

    }

    public int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }
}