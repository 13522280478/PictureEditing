package com.biotech.drawlessons.photoedit.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.biotech.drawlessons.BaseApplication;


@SuppressWarnings("WeakerAccess")
abstract class ShaderImageView extends androidx.appcompat.widget.AppCompatImageView {

    private final static boolean DEBUG = false;
    private ShaderHelper pathHelper;

    public ShaderImageView(Context context) {
        super(context);
        setup(context, null, 0);
    }

    public ShaderImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs, 0);
    }

    public ShaderImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup(context, attrs, defStyle);
    }

    private void setup(Context context, AttributeSet attrs, int defStyle) {
        getPathHelper().init(context, attrs, defStyle);
    }

    protected ShaderHelper getPathHelper() {
        if(pathHelper == null) {
            pathHelper = createImageViewHelper();
        }
        return pathHelper;
    }

    protected abstract ShaderHelper createImageViewHelper();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(getPathHelper().isSquare()) {
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();
            int dimen = Math.min(width, height);
            setMeasuredDimension(dimen, dimen);
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        getPathHelper().onImageDrawableReset(getDrawable());
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        getPathHelper().onImageDrawableReset(getDrawable());
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        getPathHelper().onImageDrawableReset(getDrawable());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        getPathHelper().onSizeChanged(w, h);
    }

    @SuppressLint("WrongCall")
	@Override
    public void onDraw(Canvas canvas) {
        if(DEBUG) {
            canvas.drawRGB(10, 200, 200);
        }

        if(!getPathHelper().onDraw(canvas)) {
            super.onDraw(canvas);
        }

    }

    /**
     * 白天夜间样式切换
     * @param lightColor
     */
    public void setBorderColor(int lightColor){
        pathHelper.borderColor = lightColor;
        pathHelper.borderPaint.setColor(pathHelper.borderColor);
        invalidate();
    }

    public void setRadius(){
    }




}