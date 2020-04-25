package com.biotech.drawlessons.photoedit.layers;

import android.graphics.RectF;

/**
 * Created by xintu on 2018/2/7.
 */

public interface ILayerRequest {
    void invalidate();
    void invalidate(RectF rectF);
}
