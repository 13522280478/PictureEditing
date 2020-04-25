package com.biotech.drawlessons.photoedit.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;

import com.biotech.drawlessons.R;

class RoundedShader extends ShaderHelper {

    private final RectF borderRect = new RectF();
    private final RectF imageRect = new RectF();
    private int radius = 90;
    private int bitmapRadius;
    private int borderAlpha = 255;

    //
    Paint whitePaint = new Paint();

    public RoundedShader() {
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyle) {
        super.init(context, attrs, defStyle);
        borderPaint.setStrokeWidth(borderWidth * 2);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShaderImageView, defStyle, 0);
            radius = typedArray.getDimensionPixelSize(R.styleable.ShaderImageView_radius, radius);
            borderAlpha = typedArray.getInteger(R.styleable.ShaderImageView_borderAlpha, borderAlpha);
            typedArray.recycle();
        }
        // 白色
        whitePaint.setColor(Color.WHITE);
        whitePaint.setAntiAlias(true);
    }


    private Xfermode paintFlagsDrawFilter = new Xfermode();

    @Override
    public void draw(Canvas canvas, Paint imagePaint, Paint borderPaint) {
        if (borderWidth == 0) {
            borderPaint.setAlpha(0);
        } else {
            borderPaint.setAlpha(borderAlpha);
        }


        canvas.drawRoundRect(borderRect, radius, radius, borderPaint);
        canvas.save();
        canvas.concat(matrix);

        imagePaint.setXfermode(paintFlagsDrawFilter);

        //----------------加一个白色背景------------------ 2016.11.21发现这个白色背景会导致夜间模式显示有问题，所以先去掉了。
//        canvas.drawRoundRect(imageRect, bitmapRadius, bitmapRadius, whitePaint);

        canvas.drawRoundRect(imageRect, bitmapRadius, bitmapRadius, imagePaint);
        imagePaint.setXfermode(null);

//        if (!SnsUtil.isThemeDefaultSimple()) {
//            nightPaint.setColor(Color.BLACK);
//            nightPaint.setAlpha((int) (255 * 0.5));
//            nightPaint.setXfermode(paintFlagsDrawFilter);
//            canvas.drawRoundRect(imageRect, bitmapRadius, bitmapRadius, nightPaint);
//            nightPaint.setXfermode(null);
//        }
        borderPaint.setColor(borderColor);
        canvas.restore();
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    public void onSizeChanged(int width, int height) {
        super.onSizeChanged(width, height);
        borderRect.set(borderWidth, borderWidth, viewWidth - borderWidth, viewHeight - borderWidth);
    }

    @Override
    public void calculate(int bitmapWidth, int bitmapHeight,
                          float width, float height,
                          float scale,
                          float translateX, float translateY) {
        imageRect.set(-translateX, -translateY, bitmapWidth + translateX, bitmapHeight + translateY);
        bitmapRadius = Math.round(radius / scale);
    }

    @Override
    public void reset() {
        imageRect.set(0, 0, 0, 0);
        bitmapRadius = 0;
    }


}