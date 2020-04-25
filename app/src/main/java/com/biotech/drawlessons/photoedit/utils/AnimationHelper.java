package com.biotech.drawlessons.photoedit.utils;

import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.RectF;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.biotech.drawlessons.photoedit.evaluator.AnimMatrixEvaluator;
import com.biotech.drawlessons.photoedit.evaluator.DoubleRectF;
import com.biotech.drawlessons.photoedit.evaluator.DoubleRectFEvaluator;
import com.biotech.drawlessons.photoedit.evaluator.MatrixInfo;
import com.biotech.drawlessons.photoedit.evaluator.RectFEvaluator;


/**
 * Created by xintu on 2018/2/12.
 */

public class AnimationHelper {

    public void startRectAnim(RectF fromRect, RectF toRect, ValueAnimator.AnimatorListener listener) {
        startRectAnim(fromRect, toRect, listener, null);
    }

    public void startRectAnim(RectF fromRect, RectF toRect, ValueAnimator.AnimatorListener listener, ValueAnimator.AnimatorUpdateListener adapter) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofObject(this,
                "rect", new RectFEvaluator(), fromRect, toRect);
        if (listener != null) {
            objectAnimator.addListener(listener);
        }
        if (adapter != null) {
            objectAnimator.addUpdateListener(adapter);
        }
        objectAnimator.setDuration(200).start();
    }

    public void startDoubleRectAnim(DoubleRectF fromDoubleRect, DoubleRectF toDoubleRect,
                                    ValueAnimator.AnimatorUpdateListener adapter) {
        startDoubleRectAnim(fromDoubleRect, toDoubleRect, null, adapter);
    }

    public void startDoubleRectAnim(DoubleRectF fromDoubleRect, DoubleRectF toDoubleRect, ValueAnimator.AnimatorListener listener, ValueAnimator.AnimatorUpdateListener adapter) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofObject(this, "doubleRect",
                new DoubleRectFEvaluator(), fromDoubleRect, toDoubleRect);
        if (listener != null) {
            objectAnimator.addListener(listener);
        }
        if (adapter != null) {
            objectAnimator.addUpdateListener(adapter);
        }
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setDuration(200).start();
    }

    public void startMatrixInfoAnim(MatrixInfo fromMatrixInfo, MatrixInfo toMatrixInfo,
                                    ValueAnimator.AnimatorUpdateListener adapter) {
        startMatrixInfoAnim(fromMatrixInfo, toMatrixInfo, null, adapter);
    }

    public void startMatrixInfoAnim(MatrixInfo fromMatrixInfo, MatrixInfo toMatrixInfo,
                                    ValueAnimator.AnimatorListener listener,
                                    ValueAnimator.AnimatorUpdateListener adapter) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofObject(this, "MatrixInfo",
                new AnimMatrixEvaluator(), fromMatrixInfo, toMatrixInfo);
        if (listener != null) {
            objectAnimator.addListener(listener);
        }
        if (adapter != null) {
            objectAnimator.addUpdateListener(adapter);
        }
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setDuration(200).start();
    }

    public static void startLayoutMarginBottomAnim(int fromValue, int toValue, final View view, AnimatorListenerAdapter listener) {
        if (view.getLayoutParams() == null || !(view.getLayoutParams() instanceof RelativeLayout.LayoutParams)) {
            return;
        }
        ValueAnimator animator = ValueAnimator.ofInt(fromValue, toValue);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ((RelativeLayout.LayoutParams) view.getLayoutParams()).bottomMargin = (int) animation.getAnimatedValue();
                view.requestLayout();
            }
        });
        if (listener != null) {
            animator.addListener(listener);
        }
        animator.setDuration(200).start();
    }

    public static ValueAnimator getLayoutMarginBottomAnim(int fromValue, int toValue, final View view) {
        return getLayoutMarginBottomAnim(fromValue, toValue, view, null);
    }

    public static ValueAnimator getLayoutMarginBottomAnim(int fromValue, int toValue, final View view,
                                                          AnimatorListenerAdapter listener) {
        if (view.getLayoutParams() == null || !(view.getLayoutParams() instanceof RelativeLayout.LayoutParams)) {
            return null;
        }
        ValueAnimator animator = ValueAnimator.ofInt(fromValue, toValue);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ((RelativeLayout.LayoutParams) view.getLayoutParams()).bottomMargin = (int) animation.getAnimatedValue();
                view.requestLayout();
            }
        });
        if (listener != null) {
            animator.addListener(listener);
        }
        animator.setDuration(200);
        return animator;
    }

    public void setRect(RectF rect) {
    }

    public void setDoubleRect(DoubleRectF doubleRect) {
    }

    public void setMatrixInfo(MatrixInfo matrixInfo) {

    }
}
