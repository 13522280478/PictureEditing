package com.biotech.drawlessons.photoedit.evaluator;

import android.animation.TypeEvaluator;
import android.graphics.RectF;

/**
 * Created by xintu on 2018/1/31.
 */

public class RectFEvaluator implements TypeEvaluator<RectF> {

    @Override
    public RectF evaluate(float fraction, RectF startValue, RectF endValue) {
        return new RectF( startValue.left + (endValue.left - startValue.left) * fraction,
                startValue.top + (endValue.top - startValue.top) * fraction,
                startValue.right + (endValue.right - startValue.right) * fraction,
                startValue.bottom + (endValue.bottom - startValue.bottom) * fraction);
    }
}
