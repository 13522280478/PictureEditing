package com.biotech.drawlessons.photoedit.draws;

import android.graphics.Matrix;

/**
 * Created by xintu on 2018/3/3.
 */

public class StickerData extends BaseDrawData {
    public Matrix stickerMatrix;
    public String text;
    public float[][] mapPoints = new float[4][2];

    public StickerData(int type, Matrix matrix) {
        super(type);
        stickerMatrix = matrix;
    }

    public StickerData(int type) {
        super(type);
    }

    public void setStickerMatrix(Matrix matrix) {
        stickerMatrix = matrix;
    }

    public Matrix getStickerMatrix() {
        return stickerMatrix;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setMapPoints(float[][] mapPoints) {
        this.mapPoints[0][0] = mapPoints[0][0];
        this.mapPoints[0][1] = mapPoints[0][1];
        this.mapPoints[1][0] = mapPoints[1][0];
        this.mapPoints[1][1] = mapPoints[1][1];
        this.mapPoints[2][0] = mapPoints[2][0];
        this.mapPoints[2][1] = mapPoints[2][1];
        this.mapPoints[3][0] = mapPoints[3][0];
        this.mapPoints[3][1] = mapPoints[3][1];
    }

    public void copy(StickerData baseDrawData) {
        super.copy(baseDrawData);
        if (stickerMatrix == null) {
            stickerMatrix = new Matrix();
        }
        stickerMatrix.set(baseDrawData.stickerMatrix);
        text = baseDrawData.text;
        setMapPoints(baseDrawData.mapPoints);
    }
}