package com.biotech.drawlessons.photoedit.layers;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;

/**
 * Created by xintu on 2018/1/29.
 */

public interface ILayer {
    void onSizeChange(int w, int h, int oldw, int oldh);
    void onDraw(Canvas canvas);
    void updateMatrix(Matrix matrix);
    void updateClipRectStatus(boolean isClip, RectF clipRect);
}
