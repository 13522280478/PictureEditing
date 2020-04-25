package com.biotech.drawlessons.photoedit.draws;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;

import androidx.annotation.ColorInt;

import com.biotech.drawlessons.photoedit.utils.IPhotoEditType;

import java.util.LinkedList;


/**
 * Created by xintu on 2017/12/4.
 * 画路径的方法，
 */

public class NormalLineBrush extends BaseBrush {
    private Paint mPaint;
    private int mColor;

    public NormalLineBrush(Matrix matrix, @ColorInt int color, float strokeWidth, Paint paint) {
        super(IPhotoEditType.BRUSH_NORMAL_COLOR, matrix, DrawOnWhere.DRAW_ON_SCREEN);
        mPaint = paint;
        mColor = color;

        mStroke = strokeWidth;
        mDrawData = new BaseDrawData(IPhotoEditType.BRUSH_NORMAL_COLOR);
        mDrawData.color = mColor;
        mDrawData.strokeWidth = mStroke;
    }

    public static NormalLineBrush createBrush(BaseDrawData drawData, Matrix matrix, Paint paint) {
        NormalLineBrush brush = new NormalLineBrush(matrix, drawData.color, drawData.strokeWidth, paint);
        brush.setDrawOnWhere(DrawOnWhere.DRAW_ON_INTERNAL_BITMAP);
        brush.mDrawData = new BaseDrawData(drawData.type);
        brush.mDrawData.copy(drawData);
        brush.mPath = brush.mDrawData.path;

        return brush;
    }

    @Override
    public void draw(Canvas canvas) {
        resetBasePaint(mPaint);
        mPaint.setColor(mColor);
        if (mDrawOnWhere == DrawOnWhere.DRAW_ON_SCREEN) {
            mPaint.setStrokeWidth(mStroke * mRealScaleValue);
        } else {
            mPaint.setStrokeWidth(mStroke);
        }
        canvas.drawPath(mPath, mPaint);
    }


}