package com.huhapp.android.customview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

/**
 * Created by igbopie on 4/12/15.
 */
public class VoteUpView extends Voter {

    public VoteUpView(Context context) {
        super(context);
    }

    public VoteUpView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init() {
        arcSign = -1;
        color = Color.parseColor("#2ecc71");
    }

    @Override
    void onMeasured() {
        rectTop = containerHeight - radius;
        rectBottom = containerHeight + radius;
    }
}
