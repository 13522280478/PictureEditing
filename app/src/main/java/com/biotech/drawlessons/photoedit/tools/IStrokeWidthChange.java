package com.biotech.drawlessons.photoedit.tools;

/**
 * Created by xintu on 2018/1/29.
 */

public interface IStrokeWidthChange {
    int STOKE_LEVEL_SMALL = -1;
    int STOKE_LEVEL_NORMAL = 0;
    int STOKE_LEVEL_LARGE = 1;

    void onStrokeWidthChange(int strokeLevel);
}
