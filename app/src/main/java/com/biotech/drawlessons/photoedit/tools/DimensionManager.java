package com.biotech.drawlessons.photoedit.tools;

import android.graphics.Matrix;
import android.graphics.RectF;

import com.biotech.drawlessons.UtilsKt;


/**
 * Created by xintu on 2018/2/14.
 */

public class DimensionManager implements IStrokeWidthChange {

    private static final int MAX_CROPPER_MARGIN = (int) UtilsKt.dp2px(20);
    // 显示图片matrix
    // 对应原图大小的matrix
    private Matrix mMatrix, mInvertMatrix;
    // 原始图片
    // 当前经过变换的
    // 初始drawingboardview
    // 最大裁剪
    // 实际要裁剪的
    private RectF mInitBitmapRect, mCurDrawableRect, mInitViewRect, mMaxCropRect, mClipRect;
    private boolean mMatrixChanged;
    private float[] mv = new float[9];
    private int mLlUtilRotateHeight;
    private float mInitScale;
    private int mStrokeLevel;

    /**
     * @param bitmapW            bitmap的宽
     * @param bitmapH            bitmap的高
     * @param llRotateUtilHeight 裁剪工具栏的高度，注意，因为初始高度取值是已经算上底部工具栏的高度，
     *                           所以传过来的高度应该是 （裁剪工具总体的高度 - 底部工具栏的高度）
     */
    public DimensionManager(int viewW, int viewH, int bitmapW, int bitmapH, int llRotateUtilHeight,
                            int color, int strokeLevel) {
        mLlUtilRotateHeight = llRotateUtilHeight;
        mStrokeLevel = strokeLevel;
        // 初始化Rects
        mInitBitmapRect = new RectF(0, 0, bitmapW, bitmapH);
        mCurDrawableRect = new RectF();
        mInitViewRect = new RectF(0, 0, viewW, viewH);
        mClipRect = new RectF();
        mMaxCropRect = new RectF(MAX_CROPPER_MARGIN,
                MAX_CROPPER_MARGIN,
                viewW - MAX_CROPPER_MARGIN,
                viewH - mLlUtilRotateHeight - MAX_CROPPER_MARGIN);

        // 初始化矩阵
        initMatrix();
        mMatrix.mapRect(mCurDrawableRect, mInitBitmapRect);
    }

    public static float getScaleFromMatrix(Matrix matrix) {
        float[] mv = new float[9];
        matrix.getValues(mv);
        float scaleX = mv[Matrix.MSCALE_X];
        float skewY = mv[Matrix.MSKEW_Y];
        return (float) Math.sqrt(scaleX * scaleX + skewY * skewY);
    }

    private void initMatrix() {
        mMatrix = new Matrix();
        float ratioX = mInitViewRect.width() / mInitBitmapRect.width();
        float ratioY = mInitViewRect.height() / mInitBitmapRect.height();
        float ratio = ratioX < ratioY ? ratioX : ratioY;
        float offsetX = mInitViewRect.centerX() - mInitBitmapRect.centerX();
        float offsetY = mInitViewRect.centerY() - mInitBitmapRect.centerY();
        mMatrix.setTranslate(offsetX, offsetY);
        mMatrix.postScale(ratio, ratio, mInitViewRect.centerX(), mInitViewRect.centerY());
        mInitScale = ratio;
        mMatrixChanged = true;

        mInvertMatrix = new Matrix();
        mInvertMatrix.set(mMatrix);
        mInvertMatrix.invert(mInvertMatrix);
    }

    public RectF getMaxCropRect() {
        return mMaxCropRect;
    }

    public RectF getInitViewRect() {
        return mInitViewRect;
    }

    public RectF getCurDrawableRect() {
        ensureMatrixValue();
        return mCurDrawableRect;
    }

    public void setCurDrawableRect(RectF rect) {
        mCurDrawableRect.set(rect);
    }

    public RectF getClipRect() {
        return mClipRect;
    }

    public void setClipRect(RectF clipRect) {
        mClipRect.set(clipRect);
    }

    public RectF getInitBitmapRect() {
        return mInitBitmapRect;
    }

    public Matrix getMatrix() {
        return mMatrix;
    }

    public void updateMatrix(Matrix matrix) {
        mMatrix.set(matrix);
        mMatrixChanged = true;
    }

    public Matrix setMatrix(Matrix matrix) {
        mMatrix.set(matrix);
        mMatrixChanged = true;
        return mMatrix;
    }

    public void postMatrixTranslate(float dx, float dy) {
        mMatrix.postTranslate(dx, dy);
        mMatrixChanged = true;
    }

    public void postMatrixScale(float sx, float sy, float px, float py) {
        mMatrix.postScale(sx, sy, px, py);
        mMatrixChanged = true;
    }

    public void postRotate(float angle, float px, float py) {
        mMatrix.postRotate(angle, px, py);
        mMatrixChanged = true;
    }

    public float getCurScale() {
        ensureMatrixValue();
        float scaleX = mv[Matrix.MSCALE_X];
        float skewY = mv[Matrix.MSKEW_Y];
        return (float) Math.sqrt(scaleX * scaleX + skewY * skewY);
    }

    public float getInitScale() {
        return mInitScale;
    }

    public float getCurRotateAngle() {
        ensureMatrixValue();
        float skewX = mv[Matrix.MSKEW_X];
        return Math.round(Math.atan2(skewX, mv[Matrix.MSCALE_X]) * (180 / Math.PI));
    }

    public void mapPointsFromInvertMatrix(float bitmapPoints[], float[] screenPoint) {
        ensureMatrixValue();
        mInvertMatrix.mapPoints(bitmapPoints, screenPoint);
    }

    public void mapRectFFromInvertMatrix(RectF dst, RectF src) {
        ensureMatrixValue();
        mInvertMatrix.mapRect(dst, src);
    }

    public void mapRectFromMatrix(RectF dst, RectF src) {
        ensureMatrixValue();
        mMatrix.mapRect(dst, src);
    }

    private void ensureMatrixValue() {
        if (mMatrixChanged) {
            mMatrixChanged = false;
            mMatrix.getValues(mv);
            mMatrix.mapRect(mCurDrawableRect, mInitBitmapRect);
            mInvertMatrix.set(mMatrix);
            mInvertMatrix.invert(mInvertMatrix);
        }
    }

    private Matrix getRectMoveToCenterMatrix(RectF outerRect, RectF innerRect) {
        Matrix matrix = new Matrix();
        float ratioX = outerRect.width() / innerRect.width();
        float ratioY = outerRect.height() / innerRect.height();
        float ratio = ratioX < ratioY ? ratioX : ratioY;

        float offsetX = outerRect.centerX() - innerRect.centerX();
        float offsetY = outerRect.centerY() - innerRect.centerY();

        matrix.setTranslate(offsetX, offsetY);
        matrix.postScale(ratio, ratio, outerRect.centerX(), outerRect.centerY());
        return matrix;
    }

    public float getStrokeWidth() {
        return getStrokeWidth(mStrokeLevel);
    }

    public float getStrokeWidth(int mStrokeLevel) {
        switch (mStrokeLevel) {
            case IStrokeWidthChange.STOKE_LEVEL_SMALL:
                return 8 / mInitScale;
            case IStrokeWidthChange.STOKE_LEVEL_NORMAL:
                return 28 / mInitScale;
            case IStrokeWidthChange.STOKE_LEVEL_LARGE:
                return 60 / mInitScale;
        }
        return 0;
    }

    public float getBrushScaleValue() {
        switch (mStrokeLevel) {
            case IStrokeWidthChange.STOKE_LEVEL_SMALL:
                return 1.4f / mInitScale;
            case IStrokeWidthChange.STOKE_LEVEL_NORMAL:
                return 2 / mInitScale;
            case IStrokeWidthChange.STOKE_LEVEL_LARGE:
                return 2.4f / mInitScale;
        }
        return 1f;
    }

    @Override
    public void onStrokeWidthChange(int strokeLevel) {
        mStrokeLevel = strokeLevel;
    }

}
