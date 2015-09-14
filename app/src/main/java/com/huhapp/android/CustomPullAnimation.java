package com.huhapp.android;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.AbsoluteLayout;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by igbopie on 9/13/15.
 */
public class CustomPullAnimation extends AbsoluteLayout implements PtrUIHandler {

    GifImageView mummyAnimation;
    private GifDrawable gifDrawable;

    public CustomPullAnimation(Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(inflater != null){
            inflater.inflate(R.layout.view_custom_pull_animation, this);
        }

        mummyAnimation = (GifImageView) findViewById(R.id.mummyAnimation);
        gifDrawable = (GifDrawable) mummyAnimation.getDrawable();

    }

    public CustomPullAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomPullAnimation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void reset(){
        gifDrawable.seekToFrameAndGet(5);
        gifDrawable.stop();
    }

    private void start(){
        gifDrawable.start();
    }

    @Override
    public void onUIReset(PtrFrameLayout ptrFrameLayout) {
        reset();
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout ptrFrameLayout) {
        reset();
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout ptrFrameLayout) {
        start();
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout ptrFrameLayout) {
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout ptrFrameLayout, boolean b, byte b1, PtrIndicator ptrIndicator) {
        float percent = (1 - Math.min(1f, ptrIndicator.getCurrentPercent())) * 2f;
        mummyAnimation.setTop((int) (150 * percent));
    }
}
