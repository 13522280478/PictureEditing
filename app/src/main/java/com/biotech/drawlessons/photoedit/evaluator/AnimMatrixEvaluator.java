package com.biotech.drawlessons.photoedit.evaluator;

import android.animation.TypeEvaluator;

/**
 * Created by xintu on 2018/2/17.
 */

public class AnimMatrixEvaluator implements TypeEvaluator<MatrixInfo> {

    @Override
    public MatrixInfo evaluate(float fraction, MatrixInfo startValue, MatrixInfo endValue) {
        float pivotX = startValue.getPivotX() + (endValue.getPivotX() - startValue.getPivotX()) * fraction;
        float pivotY = startValue.getPivotY() + (endValue.getPivotY() - startValue.getPivotY()) * fraction;
        float scale = startValue.getScale() + (endValue.getScale() - startValue.getScale()) * fraction;
        float rotateDegree = startValue.getRotateDegree()
                + (endValue.getRotateDegree() - startValue.getRotateDegree()) * fraction;
        return new MatrixInfo(pivotX, pivotY, scale, rotateDegree);
    }
}
