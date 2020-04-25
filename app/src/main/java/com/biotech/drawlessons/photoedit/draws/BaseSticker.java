package com.biotech.drawlessons.photoedit.draws;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import com.biotech.drawlessons.photoedit.tools.DimensionManager;
import com.biotech.drawlessons.photoedit.utils.BitmapsManager;


/**
 * Created by xintu on 2017/12/13.
 */

public class BaseSticker extends BaseDraw implements ISticker {
    public String mStickerUri;
    protected Matrix mMatrix, mSelfMatrix;
    protected BitmapsManager mBitmapManager;
    protected Paint mPaint;
    protected float[][] mOriginalPoints = new float[4][2], mMapPoints = new float[4][2];
    private float[] mCenterOnScreen = new float[2];
    private boolean mTouched;
    private float[] mCenter = new float[2];
    private DimensionManager mDimensionManager;
    private float mCurScale;
    private final static float MAX_SCALE_VALUE = 8.0F;
    private final static float MIN_SCALE_VALUE = 0.4F;

    public BaseSticker(int type, BitmapsManager manager, Matrix matrix, Matrix stickerMatrix, String stickerUri,
                float centerXOnBitmap, float centerYOnBitmap, DimensionManager dimensionManager) {
        super(type, matrix, DrawOnWhere.DRAW_ON_SCREEN);
        mBitmapManager = manager;
        mMatrix = matrix;
        mDimensionManager = dimensionManager;

        Bitmap bitmap = mBitmapManager.getBitmap(stickerUri);
        int bitmapW = bitmap.getWidth();
        int bitmapH = bitmap.getHeight();

        setOriginalPoints(bitmapW, bitmapH);
        mSelfMatrix = stickerMatrix;
        if (mSelfMatrix == null) {
            mSelfMatrix = new Matrix();
            mSelfMatrix.postTranslate(centerXOnBitmap - bitmapW / 2, centerYOnBitmap - bitmapH / 2);
            mSelfMatrix.postRotate(mDimensionManager.getCurRotateAngle(), centerXOnBitmap, centerYOnBitmap);
            float scale = mDimensionManager.getInitScale() / mDimensionManager.getCurScale();
            if (scale < MIN_SCALE_VALUE) {
                scale = MIN_SCALE_VALUE;
            } else if (scale > MAX_SCALE_VALUE) {
                scale = MAX_SCALE_VALUE;
            }
            mCurScale = scale;
            mSelfMatrix.postScale(scale, scale, centerXOnBitmap, centerYOnBitmap);
        }
        mStickerUri = stickerUri;
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(10);
        mapPoints();
    }

    public boolean isTouched(float x, float y) {
        mapPoints();

        // 通过判断点是否在四条直线的左边来确定点是否在矩形内。
        // 对于一条直线：A * x + B * y + C = 0
        // 我们有： A = -(y2 - y1)，B = x2 - x1，C = -(A * x1 + B * y1)
        // 而判断某一点在直线的哪一边可以用：D = A * xp + B * yp + C，D的结果来判断，当D > 0 说明在左边，
        // D < 0 说明在右边，D = 0，说明点在直线上。
        // 这里需要注意的是，判断的直线是有方向的，当我们传入正确的方向是，只需要所有结果是否都大于零，如果是
        // 那么说明点在矩形内。
        float resP3ToP0 = calPtOnWhichSideOfLine(mMapPoints[3][0],
                mMapPoints[3][1], mMapPoints[0][0], mMapPoints[0][1], x, y);
        if (resP3ToP0 < 0) {
            return false;
        }
        float resP1ToP2 = calPtOnWhichSideOfLine(mMapPoints[1][0],
                mMapPoints[1][1], mMapPoints[2][0], mMapPoints[2][1], x, y);
        if (resP1ToP2 < 0) {
            return false;
        }
        float resP0ToP1 = calPtOnWhichSideOfLine(mMapPoints[0][0],
                mMapPoints[0][1], mMapPoints[1][0], mMapPoints[1][1], x, y);
        if (resP0ToP1 < 0) {
            return false;
        }
        float resP2ToP3 = calPtOnWhichSideOfLine(mMapPoints[2][0],
                mMapPoints[2][1], mMapPoints[3][0], mMapPoints[3][1], x, y);
        return resP2ToP3 >= 0;
    }

    private float calPtOnWhichSideOfLine(float x0, float y0, float x1, float y1, float xp, float yp) {
//        float a = y2 - y1;
//        float b = x1 - x2;
//        float c = x2 * y1 - x1 * y2;
//        return a * xp + b * yp + c;
        // 新找了一个方法，是一样的...但是计算的步骤少一些
        // https://stackoverflow.com/questions/22668659/calculate-on-which-side-of-a-line-a-point-is
        return (x1 - x0)*(yp - y0) - (xp - x0)*(y1 - y0);
    }

    public Matrix getMatrix() {
        return mMatrix;
    }

    public boolean getTouched() {
        return mTouched;
    }

    public void setTouched(boolean touched) {
        mTouched = touched;
    }

    public void replaceStickerUri(String stickerUri) {
        Bitmap bitmap = mBitmapManager.getBitmap(stickerUri);
        mStickerUri = stickerUri;
        int oldW = (int) mOriginalPoints[1][0];
        int oldH = (int) mOriginalPoints[2][1];
        int newW = bitmap.getWidth();
        int newH = bitmap.getHeight();
        mSelfMatrix.preTranslate((oldW - newW) / 2, (oldH - newH) / 2);
        setOriginalPoints(newW, newH);
        mapPoints();
    }

    public String getStickerUri() {
        return mStickerUri;
    }

    private void setOriginalPoints(int bitmapW, int bitmapH) {
        mOriginalPoints[0] = new float[]{0, 0};
        mOriginalPoints[1] = new float[]{bitmapW, 0};
        mOriginalPoints[2] = new float[]{bitmapW, bitmapH};
        mOriginalPoints[3] = new float[]{0, bitmapH};
    }

    public float[] getCenterOnScreen () {
        return mCenterOnScreen;
    }

    @Override
    public void onTouchDown(float curXOnScreen, float curYOnScreen, float curXOnBitmap, float curYOnBitmap) {

    }

    @Override
    public void onTouchUp(float curXOnScreen, float curYOnScreen, float curXOnBitmap, float curYOnBitmap) {

    }

    @Override
    public void onRotate(float angle) {
        mSelfMatrix.postRotate(angle, mCenter[0], mCenter[1]);
        mapPoints();
    }

    @Override
    public void draw(Canvas canvas) {
        Matrix tempMatrix = new Matrix(mSelfMatrix);
        Bitmap bitmap = mBitmapManager.getBitmap(mStickerUri);
        if (bitmap == null) {
            return;
        }
        if (mDrawOnWhere == DrawOnWhere.DRAW_ON_SCREEN) {
            tempMatrix.postConcat(mMatrix);
        }
        canvas.drawBitmap(bitmap, tempMatrix, null);
    }

    @Override
    public void onTouchMove(float curXOnScreen, float curYOnScreen, float lastXOnScreen, float lastYOnScreen, float curXOnBitmap, float curYOnBitmap, float lastXOnBitmap, float lastYOnBitmap) {
        float dx = curXOnBitmap - lastXOnBitmap;
        float dy = curYOnBitmap - lastYOnBitmap;
        mSelfMatrix.postTranslate(dx, dy);
        mapPoints();
    }

    @Override
    public void onScale(float scaleFactor, float centerX, float centerY) {
        // 对缩放大小做一个限制
        float dstScaleValue = mCurScale * scaleFactor;
        if (dstScaleValue < MIN_SCALE_VALUE) {
            mCurScale = MIN_SCALE_VALUE;
            dstScaleValue = mCurScale / MIN_SCALE_VALUE;
        } else if (dstScaleValue > MAX_SCALE_VALUE) {
            mCurScale = MAX_SCALE_VALUE;
            dstScaleValue = mCurScale / MAX_SCALE_VALUE;
        } else {
            mCurScale = dstScaleValue;
            dstScaleValue = scaleFactor;
        }

        mSelfMatrix.postScale(dstScaleValue, dstScaleValue, mCenter[0], mCenter[1]);
        mapPoints();
    }

    public StickerData getStickerData() {
        StickerData data = new StickerData(getType(), mMatrix);
        data.bitmapUrl = mStickerUri;
        data.stickerMatrix = new Matrix(mSelfMatrix);
        data.setMapPoints(mMapPoints);
        return data;
    }

    public void setMapPoints(float[][] mapPoints) {
        mMapPoints[0][0] = mapPoints[0][0];
        mMapPoints[0][1] = mapPoints[0][1];
        mMapPoints[1][0] = mapPoints[1][0];
        mMapPoints[1][1] = mapPoints[1][1];
        mMapPoints[2][0] = mapPoints[2][0];
        mMapPoints[2][1] = mapPoints[2][1];
        mMapPoints[3][0] = mapPoints[3][0];
        mMapPoints[3][1] = mapPoints[3][1];
    }

    private void mapPoints() {
        Matrix tempMatrix = new Matrix(mSelfMatrix);
        tempMatrix.postConcat(mMatrix);

        tempMatrix.mapPoints(mMapPoints[0], mOriginalPoints[0]);
        tempMatrix.mapPoints(mMapPoints[1], mOriginalPoints[1]);
        tempMatrix.mapPoints(mMapPoints[2], mOriginalPoints[2]);
        tempMatrix.mapPoints(mMapPoints[3], mOriginalPoints[3]);

        float maxX = mMapPoints[0][0], maxY = mMapPoints[0][1], minX = mMapPoints[0][0], minY = mMapPoints[0][1];
        for (int i = 1; i < 4; i++) {
            if (maxX < mMapPoints[i][0]) {
                maxX = mMapPoints[i][0];
            }

            if (maxY < mMapPoints[i][1]) {
                maxY = mMapPoints[i][1];
            }

            if (minX > mMapPoints[i][0]) {
                minX = mMapPoints[i][0];
            }

            if (minY > mMapPoints[i][1]) {
                minY = mMapPoints[i][1];
            }
        }
        mCenterOnScreen[0] = (minX + maxX) / 2;
        mCenterOnScreen[1] = (minY + maxY) / 2;
        mDimensionManager.mapPointsFromInvertMatrix(mCenter, mCenterOnScreen);
    }

    private void printMapPoints() {
        Log.v("2333333333", "mMapPoint = [" + mMapPoints[0][0] + ", " + mMapPoints[0][1] + "] "
                + "[" + mMapPoints[1][0] + ", " + mMapPoints[1][1] + "] "
                + "[" + mMapPoints[2][0] + ", " + mMapPoints[2][1] + "] "
                + "[" + mMapPoints[3][0] + ", " + mMapPoints[3][1] + "] ");
    }
}
