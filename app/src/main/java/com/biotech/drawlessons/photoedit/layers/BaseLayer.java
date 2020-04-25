package com.biotech.drawlessons.photoedit.layers;

import android.graphics.Matrix;
import android.graphics.RectF;

/**
 * Created by xintu on 2018/2/7.
 */

public class BaseLayer extends AbsLayer {
    protected Matrix mMatrix;
    protected boolean mNeedClip;
    protected RectF mClipRect;
    private boolean mLayerAct;
    private boolean mIsNeedSecondTempBitmap;


    public BaseLayer(ILayerParent parent, int layerType, Matrix matrix) {
        super(parent, layerType);
        mMatrix = matrix;
        setIsDealDrawEvent(true);
        setIsDealTouchEvent(false);
    }

    public void setLayerAct(boolean act) {
        mLayerAct = act;
    }

    public boolean getLayerAct() {
        return mLayerAct;
    }

    public boolean isNeedSecondTempBitmap(){
        return mIsNeedSecondTempBitmap;
    }

    public void setIsNeedSecondTempBitmap(boolean isNeed) {
        mIsNeedSecondTempBitmap = isNeed;
    }

    @Override
    public void switchToThisLayer() {
        setIsDealDrawEvent(true);
        setIsDealTouchEvent(true);
    }

    @Override
    public void switchToOtherLayer(BaseLayer otherLayerType) {
        setIsDealDrawEvent(false);
        setIsDealTouchEvent(false);
    }

    @Override
    public void updateMatrix(Matrix matrix) {
        mMatrix = matrix;
    }

    @Override
    public int getLayerType() {
        return super.getLayerType();
    }

    @Override
    public void updateClipRectStatus(boolean isClip, RectF clipRect) {
        mNeedClip = isClip;
        mClipRect = clipRect;
    }

    public void requestInvalidate() {
        mParent.requestInvalidate();
    }
}
