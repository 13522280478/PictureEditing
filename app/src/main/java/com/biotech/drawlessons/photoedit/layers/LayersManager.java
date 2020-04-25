package com.biotech.drawlessons.photoedit.layers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.view.MotionEvent;


import com.biotech.drawlessons.photoedit.gesture.ITouchGesture;
import com.biotech.drawlessons.photoedit.gesture.TouchGestureDetector;
import com.biotech.drawlessons.photoedit.tools.DimensionManager;
import com.biotech.drawlessons.photoedit.utilsmodel.IColorPicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by xintu on 2018/2/7.
 */

public class LayersManager implements ITouchGesture {
    private ArrayList<BaseLayer> mLayers;
    private TouchGestureDetector mTouchDetector;
    private ILayerParent mRequest;
    private DimensionManager mDimensionManger;
    private IColorPicker mIColorPicker;
    private BaseLayer mCurLayer;

    public LayersManager(ILayerParent request, Context context, DimensionManager dimensionManager) {
        mLayers = new ArrayList<>();
        mRequest = request;
        mDimensionManger = dimensionManager;
        mTouchDetector = new TouchGestureDetector(context, this);
    }

    public void insertLayer(BaseLayer layer) {
        if (layer == null || mLayers.contains(layer)) {
            return;
        }
        mLayers.add(layer);
        orderLayers();
    }

    public void orderLayers() {
        if (mLayers.size() >= 2) {
            Collections.sort(mLayers, new Comparator<BaseLayer>() {
                @Override
                public int compare(BaseLayer o1, BaseLayer o2) {
                    return o1.getLayerType() - o2.getLayerType();
                }
            });
        }
    }

    public void switchToLayer(BaseLayer layer) {
        if (layer == null) {
            return;
        }
        if (!mLayers.contains(layer)) {
            //这里应该是主动创建一个layer，但是目前先抛异常吧
            //TODO:这里应该通过class类型创建相应的layer
            throw new RuntimeException("Before switch to layer, should insert layer first");
        }
        for (BaseLayer tempLayer :mLayers) {
            if (tempLayer == layer) {
                // 切换到该layer上
                tempLayer.switchToThisLayer();
                tempLayer.setLayerAct(true);
                mCurLayer = layer;
            } else {
                // 其他的layer回调一个切换的接口
                tempLayer.switchToOtherLayer(layer);
                tempLayer.setLayerAct(false);
            }
        }
        mRequest.requestInvalidate();
    }

    public boolean onTouchEvent(MotionEvent event) {
        return mTouchDetector.onTouchEvent(event);
    }

    public void onSizeChange(int w, int h, int oldw, int oldh) {
        for (AbsLayer layer : mLayers) {
            layer.onSizeChange(w, h, oldw, oldh);
        }
    }

    public void setIColorPicker(IColorPicker iColorPicker) {
        mIColorPicker = iColorPicker;
    }


    public void onDraw(ILayerParent parent, Canvas canvas) {
        for (AbsLayer layer : mLayers) {
            if (parent == layer.getParent() && layer.isDealDrawEvent()) {
                layer.onDraw(canvas);
            }
        }
    }

    public void updateMatrix(Matrix matrix) {
        for (AbsLayer layer : mLayers) {
            layer.updateMatrix(matrix);
        }
    }

    public void updateClipRectStatus(boolean isNeedClip, RectF clipRect) {
        for (BaseLayer layer : mLayers) {
            layer.updateClipRectStatus(isNeedClip, clipRect);
        }
    }

    @Override
    public boolean onTouchMove(float curXOnScreen, float curYOnScreen, float lastXOnScreen, float lastYOnScreen) {
        boolean needInvalidate = false;
        float[] ptsOnBitmap = new float[4];
        float[] ptsOnScreen = new float[] {curXOnScreen, curYOnScreen, lastXOnScreen, lastYOnScreen};
        mDimensionManger.mapPointsFromInvertMatrix(ptsOnBitmap, ptsOnScreen);
        for (BaseLayer layer : mLayers) {
            if (layer.isDealTouchEvent()) {
                needInvalidate |= layer.onTouchMove(curXOnScreen, curYOnScreen,
                        lastXOnScreen, lastYOnScreen,
                        ptsOnBitmap[0], ptsOnBitmap[1],
                        ptsOnBitmap[2], ptsOnBitmap[3]);
            }
        }
        if (needInvalidate) {
            mRequest.requestInvalidate();
            if (mIColorPicker != null && mCurLayer != null && (mCurLayer instanceof BrushLayer
                    || mCurLayer instanceof LightLineBrushLayer
                    || mCurLayer instanceof MosaicsLayer
                    || mCurLayer instanceof BackgroundBrushLayer)) {
                mIColorPicker.dismissColorPicker(IColorPicker.DISMISS_DELAY_TIME);
            }
        }
        return true;
    }

    @Override
    public boolean onScale(float scaleFactor, float focusX, float focusY) {
        boolean needInvalidate = false;
        for (BaseLayer layer : mLayers) {
            if (layer.isDealTouchEvent()) {
                needInvalidate |= layer.onScale(scaleFactor, focusX, focusY);
            }
        }
        if (needInvalidate) {
            mRequest.requestInvalidate();
        }
        return true;
    }

    @Override
    public boolean onRotate(float angle) {
        boolean needInvalidate = false;
        for (BaseLayer layer : mLayers) {
            if (layer.isDealTouchEvent()) {
                needInvalidate |= layer.onRotate(angle);
            }
        }
        if (needInvalidate) {
            mRequest.requestInvalidate();
        }
        return true;
    }

    @Override
    public void onFling(float startX, float startY, float velocityX, float velocityY) {

    }

    @Override
    public boolean onTouchDown(float curXOnScreen, float curYOnScreen) {
        boolean needInvalidate = false;
        float[] ptsOnBitmap = new float[2];
        float[] ptsOnScreen = new float[] {curXOnScreen, curYOnScreen};
        mDimensionManger.mapPointsFromInvertMatrix(ptsOnBitmap, ptsOnScreen);

        for (BaseLayer layer : mLayers) {
            if (layer.isDealTouchEvent()) {
                needInvalidate |= layer.onTouchDown(curXOnScreen, curYOnScreen, ptsOnBitmap[0], ptsOnBitmap[1]);
            }
        }
        if (needInvalidate) {
            mRequest.requestInvalidate();
        }
        return true;
    }

    @Override
    public boolean onTouchUp(float curXOnScreen, float curYOnScreen) {
        boolean needInvalidate = false;
        float[] ptsOnBitmap = new float[2];
        float[] ptsOnScreen = new float[] {curXOnScreen, curYOnScreen};
        mDimensionManger.mapPointsFromInvertMatrix(ptsOnBitmap, ptsOnScreen);

        for (BaseLayer layer : mLayers) {
            if (layer.isDealTouchEvent()) {
                needInvalidate |= layer.onTouchUp(curXOnScreen, curYOnScreen, ptsOnBitmap[0], ptsOnBitmap[1]);
            }
        }
        if (needInvalidate) {
            mRequest.requestInvalidate();
            if (mIColorPicker != null && mCurLayer != null && (mCurLayer instanceof BrushLayer
                    || mCurLayer instanceof LightLineBrushLayer
                    || mCurLayer instanceof MosaicsLayer
                    || mCurLayer instanceof BackgroundBrushLayer)) {
                mIColorPicker.showColorPicker(IColorPicker.SHOW_DELAY_TIME);
            }
        }
        return true;
    }
}