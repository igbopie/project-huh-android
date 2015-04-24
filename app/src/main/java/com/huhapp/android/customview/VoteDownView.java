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
public class VoteDownView extends Voter {


    public VoteDownView(Context context) {
        super(context);
    }

    public VoteDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init() {
        arcSign = 1;
        color = Color.parseColor("#e74c3c");
    }
    @Override
    void onMeasured() {
        rectTop =  - radius;
        rectBottom = radius;
    }
}
