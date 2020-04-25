package com.biotech.drawlessons.photoedit.draws;

import android.graphics.Matrix;

import com.biotech.drawlessons.photoedit.tools.DimensionManager;
import com.biotech.drawlessons.photoedit.utils.BitmapsManager;

/**
 * Created by xintu on 2018/3/27.
 * 图片的贴纸，这些贴纸默认会被绘制在屏幕上（拿到的 canvas 是 mDrawingBoard 传过来的 canvas）。
 * 图片贴纸的优先级比文字贴纸的优先级低，也就是说添加文字的时候，会覆盖图片贴纸
 */

public class BitmapSticker extends BaseSticker {
    public BitmapSticker(int type, BitmapsManager manager, Matrix matrix, Matrix stickerMatrix, String stickerUri, float centerXOnBitmap, float centerYOnBitmap, DimensionManager dimensionManager) {
        super(type, manager, matrix, stickerMatrix, stickerUri, centerXOnBitmap, centerYOnBitmap, dimensionManager);
    }
}
