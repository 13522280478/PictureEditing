package com.biotech.drawlessons.photoedit.utils;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;

/**
 * Created by xintu on 2018/2/17.
 * 可以旋转的矩形，创建的时候，必须要传一个矩形的四个顶点，如果传的不是矩形，会报错
 */

public class RotatableRectF {
    private float[] pt0, pt1, pt2, pt3;
    private float[] initPt0, initPt1, initPt2, initPt3;
    private Matrix mMatrix;
    private boolean mRectChange;

    //          ---------line1
    //          |
    //      p0 \|/   p1
    //       --------
    //      |        |
    //      |        |<------ line2
    //      |        |
    //       --------
    //     p3        p2
    public RotatableRectF(float[] pt0, float pt1[], float pt2[], float[] pt3) {
        init(pt0, pt1, pt2, pt3);
    }

    public RotatableRectF(RectF rectF) {
        init(new float[]{rectF.left, rectF.top},
                new float[]{rectF.right, rectF.top},
                new float[]{rectF.right, rectF.bottom},
                new float[]{rectF.left, rectF.bottom});
    }

    private void init(float[] pt0, float pt1[], float pt2[], float[] pt3) {
        if (pt0.length < 2 || pt1.length < 2 || pt2.length < 2 || pt3.length < 2) {
            throw new ArrayIndexOutOfBoundsException();
        }
        this.pt0 = pt0;
        this.pt1 = pt1;
        this.pt2 = pt2;
        this.pt3 = pt3;
        initPt0 = new float[]{pt0[0], pt0[1]};
        initPt1 = new float[]{pt1[0], pt1[1]};
        initPt2 = new float[]{pt2[0], pt2[1]};
        initPt3 = new float[]{pt3[0], pt3[1]};
//        if (!isRectangle()) {
//            throw new IllegalArgumentException("Not support create quadrangle is no a rectangular");
//        }
        mMatrix = new Matrix();
    }

    public RectF toRect() {
        ensurePoints();
        return new RectF(pt3[0], pt3[1], pt1[0], pt1[1]);
    }

    private float getTwoPointsDistance(float[] firstPoint, float[] secondPoint) {
        return (float) Math.sqrt(Math.pow((firstPoint[0] - secondPoint[0]), 2) + Math.pow((firstPoint[1] - secondPoint[1]), 2));
    }

    public void linkPath(Path path) {
        ensurePoints();
        path.moveTo(pt0[0], pt0[1]);
        path.lineTo(pt1[0], pt1[1]);
        path.lineTo(pt2[0], pt2[1]);
        path.lineTo(pt3[0], pt3[1]);
        path.close();
    }

    public float centerX() {
        ensurePoints();
        return (pt2[0] + pt0[0]) / 2;
    }

    public float centerY() {
        ensurePoints();
        return (pt2[1] + pt0[1]) / 2;
    }

    public float[] getPoint0() {
        ensurePoints();
        return new float[]{pt0[0], pt0[1]};
    }

    public float[] getPoint1() {
        ensurePoints();
        return new float[]{pt1[0], pt1[1]};
    }

    public float[] getPoint2() {
        ensurePoints();
        return new float[]{pt2[0], pt2[1]};
    }

    public float[] getPoint3() {
        ensurePoints();
        return new float[]{pt3[0], pt3[1]};
    }

    private float getMinX() {
        float minX = pt0[0];
        if (minX > pt1[0]) minX = pt1[0];
        if (minX > pt2[0]) minX = pt1[0];
        if (minX > pt2[0]) minX = pt1[0];
        return minX;
    }

    private float getMaxX() {
        float maxX = pt0[0];
        if (maxX < pt1[0]) maxX = pt1[0];
        if (maxX < pt2[0]) maxX = pt1[0];
        if (maxX < pt2[0]) maxX = pt1[0];
        return maxX;
    }

    private float getMinY() {
        float minY = pt0[1];
        if (minY > pt1[1]) minY = pt1[1];
        if (minY > pt2[1]) minY = pt1[1];
        if (minY > pt2[1]) minY = pt1[1];
        return minY;
    }

    private float getMaxY() {
        float maxY = pt0[1];
        if (maxY < pt1[1]) maxY = pt1[1];
        if (maxY < pt2[1]) maxY = pt1[1];
        if (maxY < pt2[1]) maxY = pt1[1];
        return maxY;
    }

    public void setRotate(float angle) {
        setRotate(angle, centerX(), centerY());
    }

    private void setRotate(float angle, float px, float py) {
        mMatrix.setRotate(angle, px, py);
        mRectChange = true;
    }

    public void postTranslate(float dx, float dy) {
        mMatrix.postTranslate(dx, dy);
        mRectChange = true;
    }

    public void postRotate(float angle) {
        postRotate(angle, centerX(), centerY());
    }

    public void postRotate(float angle, float px, float py) {
        mMatrix.postRotate(angle, px, py);
        mRectChange = true;
    }

    public void postScale(float sx, float sy) {
        postScale(sx, sy, centerX(), centerY());
    }

    public void setScale(float sx, float sy, float px, float py) {
        mMatrix.setScale(sx, sy, px, py);
        mRectChange = true;
    }

    public void postScale(float sx, float sy, float px, float py) {
        mMatrix.postScale(sx, sy, px, py);
        mRectChange = true;
    }

    public float getLine1Dis() {
        ensurePoints();
        return getTwoPointsDistance(pt0, pt1);
    }

    public float getLine2Dis() {
        ensurePoints();
        return getTwoPointsDistance(pt1, pt2);
    }

    // 判断是否是矩形。本质就是利用勾股定理，判断两条直角边和一个斜边的关系，
    // 是否满足 pow(a,2) + pow(b,2) == pow(c,2)
    public boolean isRectangle() {
        //calculate distances needed for P.Theorem
        double dist0to3 = Math.pow(pt0[0] - pt3[0], 2) + Math.pow(pt0[1] - pt3[1], 2);
        double dist0to1 = Math.pow(pt0[0] - pt1[0], 2) + Math.pow(pt0[1] - pt1[1], 2);
        double dist2to3 = Math.pow(pt2[0] - pt3[0], 2) + Math.pow(pt2[1] - pt3[1], 2);
        double dist2to1 = Math.pow(pt2[0] - pt1[0], 2) + Math.pow(pt2[1] - pt1[1], 2);
        double dist1to3 = Math.pow(pt1[0] - pt3[0], 2) + Math.pow(pt1[1] - pt3[1], 2);

        //sum a^2 + b^2
        double ab1 = dist0to3 + dist0to1;
        double ab2 = dist2to3 + dist2to1;

        //calculate (a^2+b^2) - c^2
        double ab_c1 = Math.abs(ab1 - dist1to3);
        double ab_c2 = Math.abs(ab2 - dist1to3);

        //return the result of whether the angles are very close to a
        //right angle or not close
        return ((ab_c1 < 1e-6) && (ab_c2 < 1e-6));
    }

    private void ensurePoints() {
        if (mRectChange) {
            mMatrix.mapPoints(pt0, initPt0);
            mMatrix.mapPoints(pt1, initPt1);
            mMatrix.mapPoints(pt2, initPt2);
            mMatrix.mapPoints(pt3, initPt3);
            mRectChange = false;
        }
    }

    @Override
    public String toString() {
        ensurePoints();
        return "\npt0 = {" + pt0[0] + ", " + pt0[1] + "},\npt1 = {" + pt1[0] + ", " + pt1[1] + "},\npt2 = {" + pt2[0] + ", " + pt2[1] + "},\npt3 = {" + pt3[0] + ", " + pt3[1] + "}\ncenterX = " + ((pt2[0] + pt0[0]) / 2) + "\ncenterY = " + ((pt2[1] + pt0[1]) / 2);
    }
}
