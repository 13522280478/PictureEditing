package com.biotech.drawlessons.photoedit.utilsmodel;

/**
 * Created by xintu on 2018/3/20.
 */

public interface IBrushMode {
    void onNormalBrushMode(int color);

    void onLightLineBrushMode();

    void onBlockMosaicsMode();

    void onBrushMosaicsMode();

    void onStickerListBrushMode();
}
