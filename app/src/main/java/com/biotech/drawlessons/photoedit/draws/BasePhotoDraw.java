package com.biotech.drawlessons.photoedit.draws;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.text.TextUtils;

import com.biotech.drawlessons.photoedit.utils.BitmapsManager;


/**
 * Created by xintu on 2018/3/30.
 */

public class BasePhotoDraw extends BaseBrush {
    protected BitmapsManager mBitmapManager;
    protected Matrix mSelfMatrix;
    protected String mPhotoUrl;

    BasePhotoDraw(int type, Matrix matrix, DrawOnWhere drawOnWhere, BitmapsManager manager) {
        super(type, matrix, drawOnWhere);
        mBitmapManager = manager;
    }

    @Override
    public void onTouchDown(float curXOnScreen, float curYOnScreen, float curXOnBitmap, float curYOnBitmap) {

    }

    @Override
    public void onTouchMove(float curXOnScreen, float curYOnScreen, float lastXOnScreen, float lastYOnScreen, float curXOnBitmap, float curYOnBitmap, float lastXOnBitmap, float lastYOnBitmap) {

    }

    @Override
    public void onTouchUp(float curXOnScreen, float curYOnScreen, float curXOnBitmap, float curYOnBitmap) {

    }

    public void setPhoto(Bitmap frame) {
        if (frame == null) {
            return;
        }

        if (mSelfMatrix == null) {
            mSelfMatrix = new Matrix();
        }

        int width = frame.getWidth();
        int height = frame.getHeight();
        int originalWidth = mBitmapManager.getOriginalBitmap().getWidth();
        int originalHeight = mBitmapManager.getOriginalBitmap().getHeight();

        float ratioX = originalWidth / (float) width;
        float ratioY = originalHeight / (float) height;
        float ratio = ratioX > ratioY ? ratioX : ratioY;
        float offsetX = (originalWidth - width) >> 1;
        float offsetY = (originalHeight - height) >> 1;
        mSelfMatrix.setTranslate(offsetX, offsetY);
        mSelfMatrix.postScale(ratio, ratio, originalWidth >> 1, originalHeight >> 1);
    }

    public void setPhoto(String url) {
        mPhotoUrl = url;
        Bitmap frame = mBitmapManager.getPhotoFrameBitmap(mPhotoUrl);
        setPhoto(frame);
        mDrawData.bitmapUrl = mPhotoUrl;
    }

    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    public void clearPhoto() {
        if (mPhotoUrl == null || TextUtils.isEmpty(mPhotoUrl)) {
            return;
        }
        mBitmapManager.removeBitmap(mPhotoUrl);
        mSelfMatrix = null;
        mPhotoUrl = null;
        mDrawData.bitmapUrl = null;
    }

    @Override
    public void draw(Canvas canvas) {
        Bitmap bitmap = mBitmapManager.getBitmap(mPhotoUrl);
        if (bitmap == null) {
            return;
        }
        canvas.drawBitmap(bitmap, mSelfMatrix, null);
    }
}
