package com.biotech.drawlessons.photoedit.layers;

import android.graphics.Rect;
import android.os.Handler;

/**
 * Created by xintu on 2018/2/8.
 */

public interface ILayerParent {
    void requestInvalidate();
    void requestInvalidate(Rect rect);
    Handler getHandler();
}
