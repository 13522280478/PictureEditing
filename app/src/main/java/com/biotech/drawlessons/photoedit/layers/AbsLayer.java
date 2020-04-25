package com.biotech.drawlessons.photoedit.layers;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;

/**
 * Created by xintu on 2018/1/30.
 */

public abstract class AbsLayer implements ILayer {
    private int mType;
    private boolean mDealTouchEvent;
    private boolean mDealDrawEvent;
    protected ILayerParent mParent;
    public AbsLayer(ILayerParent parent, int layerType) {
        mType = layerType;
        mParent = parent;
    }

    public int getLayerType() {
        return mType;
    }

    public boolean isDealTouchEvent(){
        return mDealTouchEvent;
    }

    public void setIsDealTouchEvent(boolean dealTouchEvent) {
        mDealTouchEvent = dealTouchEvent;
    }

    public boolean isDealDrawEvent() {
        return mDealDrawEvent;
    }

    public void setIsDealDrawEvent(boolean dealDrawEvent) {
        mDealDrawEvent = dealDrawEvent;
    }


    public void invalidate() {
        mParent.requestInvalidate();
    }

    public ILayerParent getParent() {
        return mParent;
    }

    public abstract void switchToThisLayer();

    public abstract void switchToOtherLayer(BaseLayer otherLayerType);

    @Override
    public void onSizeChange(int w, int h, int oldw, int oldh) {

    }

    @Override
    public void onDraw(Canvas canvas) {

    }


    @Override
    public void updateMatrix(Matrix matrix) {

    }

    public boolean onRotate(float angle) {
        return false;
    }

    public boolean onTouchDown(float curXOnScreen, float curYOnScreen, float curXOnBitmap, float curYOnBitmap) {
        return false;
    }

    public boolean onTouchMove(float curXOnScreen, float curYOnScreen, float lastXOnScreen, float lastYOnScreen,
                               float curXOnBitmap, float curYOnBitmap, float lastXOnBitmap, float lastYOnBitmap) {
        return false;
    }

    public boolean onScale(float scaleFactor, float focusX, float focusY) {
        return false;
    }

    public void onFling(float startX, float startY, float velocityX, float velocityY) {

    }

    public boolean onTouchUp(float curXOnScreen, float curYOnScreen, float curXOnBitmap, float curYOnBitmap) {
        return false;
    }

    @Override
    public void updateClipRectStatus(boolean isClip, RectF clipRect) {

    }
}
