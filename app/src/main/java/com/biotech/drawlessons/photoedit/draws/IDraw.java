package com.biotech.drawlessons.photoedit.draws;

import android.graphics.Canvas;

/**
 * Created by xintu on 2017/12/4.
 */

public interface IDraw {
    void draw(Canvas canvas);

    void onTouchDown(float curXOnScreen, float curYOnScreen, float curXOnBitmap, float curYOnBitmap);

    void onTouchMove(float curXOnScreen, float curYOnScreen, float lastXOnScreen, float lastYOnScreen,
                     float curXOnBitmap, float curYOnBitmap, float lastXOnBitmap, float lastYOnBitmap);

    void onTouchUp(float curXOnScreen, float curYOnScreen, float curXOnBitmap, float curYOnBitmap);
}
