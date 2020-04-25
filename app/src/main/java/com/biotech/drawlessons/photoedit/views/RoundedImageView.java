package com.biotech.drawlessons.photoedit.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.biotech.drawlessons.R;


public class RoundedImageView extends ShaderImageView {
    private static final String TAG = RoundedImageView.class.getSimpleName();

    //
    Paint pressPaint;

    public RoundedImageView(Context context) {
        super(context);
        //
        initPressedPaint(null);
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //
        initPressedPaint(attrs);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //
        initPressedPaint(attrs);
    }

    boolean isShowCeil = true;

    private void initPressedPaint(AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray temp = getContext().obtainStyledAttributes(attributeSet, R.styleable.ShaderImageView);
            isShowCeil = temp.getBoolean(R.styleable.ShaderImageView_is_show_ceil, true);
            temp.recycle();
        }


        //
        pressPaint = new Paint();
        pressPaint.setAntiAlias(true);
        pressPaint.setColor(Color.BLACK);
        pressPaint.setAlpha((int) (255 * 0.46));
    }


    @Override
    public ShaderHelper createImageViewHelper() {
        return new RoundedShader();
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //&& Math.abs(mRadius - 90) < 0.01
        if (isPressedFlag && isShowCeil) {
            float radius = Math.min(getMeasuredWidth(), getMeasuredHeight()) / 2;
            //
            canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, radius, pressPaint);
        }
    }


    //
    boolean isPressedFlag = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isShowCeil) {
            return super.onTouchEvent(event);
        }


        boolean flag = super.onTouchEvent(event);
        //
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // 这块应该是true，现在说要去掉点击效果，呵呵，以后说不定还要改回来
            isPressedFlag = false;
            this.invalidate();
            //
            flag = true;
            //
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            isPressedFlag = false;
            this.invalidate();
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            isPressedFlag = false;
            this.invalidate();
        }
        return flag;
    }

}
