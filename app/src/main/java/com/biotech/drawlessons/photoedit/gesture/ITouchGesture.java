package com.biotech.drawlessons.photoedit.gesture;

/**
 * Created by xintu on 2018/2/4.
 */

public interface ITouchGesture {
    boolean onTouchMove(float curX, float curY, float lastX, float lastY);

    boolean onScale(float scaleFactor, float focusX, float focusY);

    boolean onRotate(float angle);

    void onFling(float startX, float startY, float velocityX, float velocityY);

    boolean onTouchDown(float curX, float curY);

    boolean onTouchUp(float curX, float curY);
}
