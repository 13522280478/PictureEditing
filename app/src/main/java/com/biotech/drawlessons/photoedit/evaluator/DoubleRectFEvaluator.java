package com.biotech.drawlessons.photoedit.evaluator;

import android.animation.TypeEvaluator;
import android.graphics.RectF;

/**
 * Created by xintu on 2018/2/1.
 * 在 cropper 从缩小状态变换到拉满的状态时，图片的 drawable 也需要同时变换，所以需要两个 rect
 * 这里按照之前一个 rect 的思路，直接新建一个两个 rect 的类，传入的时候，两个 rect 同时设置 start 和 end 值。
 * 感觉好傻...不过管他呢，能用就行，哇哈哈哈哈哈哈哈
 */

public class DoubleRectFEvaluator implements TypeEvaluator<DoubleRectF> {

    @Override
    public DoubleRectF evaluate(float fraction, DoubleRectF startValue, DoubleRectF endValue) {
        RectF firstRect = new RectF();
        RectF secondRect = new RectF();
        firstRect.set(
                startValue.getFirstRect().left
                        + (endValue.getFirstRect().left - startValue.getFirstRect().left) * fraction,
                startValue.getFirstRect().top
                        + (endValue.getFirstRect().top - startValue.getFirstRect().top) * fraction,
                startValue.getFirstRect().right
                        + (endValue.getFirstRect().right - startValue.getFirstRect().right) * fraction,
                startValue.getFirstRect().bottom
                        + (endValue.getFirstRect().bottom - startValue.getFirstRect().bottom) * fraction);

        secondRect.set(
                startValue.getSecondRect().left
                        + (endValue.getSecondRect().left - startValue.getSecondRect().left) * fraction,
                startValue.getSecondRect().top
                        + (endValue.getSecondRect().top - startValue.getSecondRect().top) * fraction,
                startValue.getSecondRect().right
                        + (endValue.getSecondRect().right - startValue.getSecondRect().right) * fraction,
                startValue.getSecondRect().bottom
                        + (endValue.getSecondRect().bottom - startValue.getSecondRect().bottom) * fraction);
        return new DoubleRectF(firstRect, secondRect);
    }

}
