package com.biotech.drawlessons.photoedit.draws;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;


import com.biotech.drawlessons.photoedit.utils.IPhotoEditType;

import java.util.LinkedList;

/**
 * Created by xintu on 2017/12/4.
 * 画路径的方法，
 */

public class LightLineBrush extends BaseBrush {
    private Paint mInnerPaint, mOuterPaint;
    private int mInnerColor, mOuterColor;
    private BlurMaskFilter filter;

    public LightLineBrush(Matrix matrix, Paint innerPaint,
                          Paint outerPaint, float strokeWidth, BlurMaskFilter filter) {
        // 创建的时候，是绘制在屏幕上的，在onTouchUp的时候，会把绘制在屏幕上的path，转换到 bitmap 上
        super(IPhotoEditType.BRUSH_LIGHT_COLOR, matrix, DrawOnWhere.DRAW_ON_INTERNAL_BITMAP);
        mStroke = strokeWidth;
        // 这里把颜色写死了，无法配置，如果要更换颜色，直接在这里更改
        mOuterColor = 0xFFFF00FF;
        mInnerColor = 0xFFFFFFFF;
        this.filter = filter;

        mInnerPaint = innerPaint;
        mOuterPaint = outerPaint;
        mDrawData.strokeWidth = mStroke;
    }

    public static LightLineBrush createBrush(BaseDrawData drawData, Matrix matrix,
                                             Paint innerPaint, Paint outerPaint,
                                             BlurMaskFilter filter) {

        LightLineBrush brush = new LightLineBrush(matrix, innerPaint, outerPaint, drawData.strokeWidth, filter);
        // 新建path
        brush.mPath = new Path();
        brush.mDrawData = new BaseDrawData(drawData.type);
        brush.mDrawData.copy(drawData);
        brush.mPath = brush.mDrawData.path;

        return brush;
    }

    private void resetInnerPaint() {
        resetBasePaint(mInnerPaint);
        mInnerPaint.setColor(mInnerColor);
        if (mDrawOnWhere == DrawOnWhere.DRAW_ON_SCREEN) {
            mInnerPaint.setStrokeWidth(mStroke * mRealScaleValue);
        } else {
            mInnerPaint.setStrokeWidth(mStroke);
        }
    }

    private void resetOuterPaint() {
        resetBasePaint(mOuterPaint);
        mOuterPaint.setColor(mOuterColor);
        if (mDrawOnWhere == DrawOnWhere.DRAW_ON_SCREEN) {
            mOuterPaint.setStrokeWidth(mStroke/2 * mRealScaleValue);
        } else {
            mOuterPaint.setStrokeWidth(mStroke);
        }
        mOuterPaint.setMaskFilter(filter);
    }

    @Override
    public void draw(Canvas canvas) {
        resetInnerPaint();
        resetOuterPaint();

        canvas.drawPath(mPath, mInnerPaint);
        canvas.drawPath(mPath, mOuterPaint);

    }
}