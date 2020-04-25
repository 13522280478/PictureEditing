package com.biotech.drawlessons.photoedit.evaluator;


/**
 * Created by xintu on 2018/2/17.
 */

public class MatrixInfo {
    private float pivotX, pivotY;
    private float rotateDegree;
    private float scale;

    public MatrixInfo(float pivotX, float pivotY, float scale, float rotateDegree) {
        this.pivotX = pivotX;
        this.pivotY = pivotY;
        this.scale = scale;
        this.rotateDegree = rotateDegree;
    }

    public MatrixInfo(MatrixInfo info) {
        set(info);
    }

    public void set(MatrixInfo info) {
        this.pivotX = info.getPivotX();
        this.pivotY = info.getPivotY();
        this.scale = info.getScale();
        this.rotateDegree = info.getRotateDegree();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        MatrixInfo r = (MatrixInfo) obj;

        return pivotX == r.pivotX && pivotY == r.pivotY && rotateDegree == r.rotateDegree && scale == r.scale;
    }

    public float getScale() {
        return scale;
    }

    public float getPivotX() {
        return pivotX;
    }

    public float getPivotY() {
        return pivotY;
    }

    public float getRotateDegree() {
        return rotateDegree;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setPivotX(float pivotX) {
        this.pivotX = pivotX;
    }

    public void setPivotY(float pivotY) {
        this.pivotY = pivotY;
    }

    public void setRotateDegree(float rotateDegree) {
        this.rotateDegree = rotateDegree;
    }

    @Override
    public String toString() {
        return "pivotX = " + pivotX + ", pivotY = " + pivotY + ", scale = " + scale + ", rotateDegree = " + rotateDegree;
    }
}
