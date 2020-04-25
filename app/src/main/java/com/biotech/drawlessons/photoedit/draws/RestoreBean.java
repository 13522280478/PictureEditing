package com.biotech.drawlessons.photoedit.draws;

import android.graphics.Matrix;
import android.graphics.RectF;


import com.biotech.drawlessons.photoedit.evaluator.MatrixInfo;

import java.util.LinkedList;

/**
 * Created by xintu on 2018/3/1.
 */

public class RestoreBean {
    private LinkedList<BaseDrawData> mBrushDatas;
    private LinkedList<StickerData> mStickerDatas;
    private Matrix matrix;
    private RectF clipRect;
    private MatrixInfo doneCropMatrixInfo;
    private boolean needClip;
    private String filterType;
    // 是否是 camera 的 photo，如果是，就不应该取 restore 的 matrix，因为拍照界面 matrix 一直是，单位矩阵
    // 如果直接更新，会有问题
    private boolean isCameraPhoto;
    public RestoreBean(Matrix matrix, RectF clipRect, MatrixInfo doneCropMatrixInfo, boolean needClip) {
        this.matrix = matrix;
        this.clipRect = clipRect;
        this.doneCropMatrixInfo = doneCropMatrixInfo;
        this.needClip = needClip;
        mBrushDatas = new LinkedList<>();
        mStickerDatas = new LinkedList<>();
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public String getFilterType() {
        return filterType;
    }

    public MatrixInfo getDoneCropMatrixInfo() {
        return doneCropMatrixInfo;
    }

    public RectF getClipRect() {
        return clipRect;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public LinkedList<BaseDrawData> getBrushDatas() {
        return mBrushDatas;
    }

    public LinkedList<StickerData> getStickerDatas() {
        return mStickerDatas;
    }

    public void setNeedClip(boolean needClip) {
        this.needClip = needClip;
    }

    public boolean getNeedClip() {
        return needClip;
    }

    public void setIsCameraPhoto(boolean isCameraPhoto) {
        this.isCameraPhoto = isCameraPhoto;
    }

    public boolean isCameraPhoto() {
        return isCameraPhoto;
    }

}
