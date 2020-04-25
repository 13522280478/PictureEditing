package com.biotech.drawlessons.photoedit.evaluator;

import android.graphics.RectF;

/**
 * Created by xintu on 2018/2/1.
 */

public class DoubleRectF {
    private RectF mFirstRect, mSecondRect;

    public DoubleRectF(RectF firstRect, RectF secondRect) {
        mFirstRect = firstRect;
        mSecondRect = secondRect;
    }

    public RectF getFirstRect() {
        return mFirstRect;
    }

    public RectF getSecondRect() {
        return mSecondRect;
    }
}
