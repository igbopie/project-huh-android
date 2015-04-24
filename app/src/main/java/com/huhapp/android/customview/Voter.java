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
public abstract class Voter extends View {
    Paint paint;
    float x;
    float y;
    float containerWidth;
    float containerHeight;
    float radius;
    float stroke = 10;
    float alphaActive = 1f;
    float alphaNotActive = .1f;
    boolean active = false;

    //need values!
    float rectLeft;
    float rectTop;
    float rectRight;
    float rectBottom;
    int color;
    int arcSign = -1;

    public Voter(Context context) {
        super(context);
        init();
    }

    public Voter(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
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


        rectLeft = (containerWidth/2) - radius;
        rectRight = (containerWidth/2) + radius;
        onMeasured();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paintUp = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintUp.setStyle(Paint.Style.FILL);
        //paintUp.setStrokeWidth(stroke);
        paintUp.setColor(adjustAlpha(this.color, active?alphaActive:alphaNotActive));
        RectF rect = new RectF(rectLeft, rectTop, rectRight, rectBottom);
        canvas.drawArc(rect, 0, arcSign *180, false, paintUp);

    }

    public int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        invalidate();
    }


    abstract void init();
    abstract void onMeasured();
}
