package com.biotech.drawlessons.photoedit.draws;

import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.Iterator;

/**
 * Created by xintu on 2018/2/16.
 */

public class BaseBrush extends BaseDraw {

    public Path mPath;
    private boolean mTouchUpped;
    protected float mStroke;
    protected float mScreenToBitmapScale = 1f;
    protected BaseDrawData mDrawData;

    BaseBrush(int type, Matrix matrix, DrawOnWhere drawOnWhere) {
        super(type, matrix, drawOnWhere);
        mDrawData = new BaseDrawData(type);
    }

    /**
     * 初始化 draw 的路径
     * */
    @Override
    public void onTouchDown(float curXOnScreen, float curYOnScreen, float curXOnBitmap, float curYOnBitmap) {
        mPath = new Path();
        if (mDrawOnWhere == DrawOnWhere.DRAW_ON_SCREEN) {
            mPath.moveTo(curXOnScreen, curYOnScreen);
        } else {
            mPath.moveTo(curXOnBitmap, curYOnBitmap);
        }
        mDrawData.path = mPath;
    }

    /**
     * path 移动的方法，用二阶贝塞尔曲线让 path 更平滑
     * */
    @Override
    public void onTouchMove(float curXOnScreen, float curYOnScreen, float lastXOnScreen, float lastYOnScreen,
                            float curXOnBitmap, float curYOnBitmap, float lastXOnBitmap, float lastYOnBitmap) {
        if (mDrawOnWhere == DrawOnWhere.DRAW_ON_SCREEN) {
            float dx = Math.abs(curXOnScreen - lastXOnScreen);
            float dy = Math.abs(curYOnScreen - lastYOnScreen);

            if (dx >= 2 || dy >= 2) {
                mPath.quadTo(lastXOnScreen, lastYOnScreen,
                        (curXOnScreen + lastXOnScreen) / 2,
                        (curYOnScreen + lastYOnScreen) / 2);
            }
        } else if (mDrawOnWhere == DrawOnWhere.DRAW_ON_INTERNAL_BITMAP){
            float dx = Math.abs(curXOnBitmap - lastXOnBitmap);
            float dy = Math.abs(curYOnBitmap - lastYOnBitmap);

            if (dx >= 2 || dy >= 2) {
                mPath.quadTo(lastXOnBitmap, lastYOnBitmap,
                        (curXOnBitmap + lastXOnBitmap) / 2,
                        (curYOnBitmap + lastYOnBitmap) / 2);
            }
        }
    }

    @Override
    public void onTouchUp(float curXOnScreen, float curYOnScreen, float curXOnBitmap, float curYOnBitmap) {
    }

    /**
     * 把 path 从 screen 转换到 bitmap。
     * 同时里面的 stroke 等参数也需要做相应的缩放
     * */
    public void mapPathFromScreenToBitmap() {
        // 如果已经是绘制在图片上了，就不能再转一次了
        if (mDrawOnWhere == DrawOnWhere.DRAW_ON_INTERNAL_BITMAP) {
            return;
        }
        Matrix matrix = new Matrix(mMatrix);
        matrix.invert(matrix);
        mPath.transform(matrix);
        matrix.getValues(mv);
        mScreenToBitmapScale = mv[Matrix.MSCALE_X];
//        mStroke *= mScreenToBitmapScale;
        setDrawOnWhere(DrawOnWhere.DRAW_ON_INTERNAL_BITMAP);
    }

    public void setTouchUp(){
        mTouchUpped = true;
    }

    public boolean isTouchUpped() {
        return mTouchUpped;
    }

    public BaseDrawData getDrawData() {
        if (mDrawData == null) {
            return null;
        }
        return mDrawData;
    }


    //TODO:感觉没必要这么写，每次draw都重置这么多参数是没有必要的。
    protected void resetBasePaint(Paint paint) {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(null);
        paint.setColorFilter(null);
        paint.setAlpha(255);
    }
}
