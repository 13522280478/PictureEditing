package com.biotech.drawlessons.photoedit.draws;

import android.graphics.Canvas;
import android.graphics.Matrix;

import com.biotech.drawlessons.photoedit.tools.DimensionManager;
import com.biotech.drawlessons.photoedit.utils.BitmapsManager;
import com.biotech.drawlessons.photoedit.utils.IPhotoEditType;
import com.biotech.drawlessons.photoedit.utils.StringUtil;


/**
 * Created by xintu on 2017/12/13.
 */

public class TextSticker extends BaseSticker {
    private String mStickerText;
    private int mColor;
    private int mTextLength;
    // 当文字正在编辑的时候，编辑框后边的sticker应该不显示
    private boolean mIsEditing;
    public TextSticker(BitmapsManager manager, Matrix matrix, Matrix stickerMatrix, String stickerUri,
                       String stickerText, int color, float centerXOnBitmap, float centerYOnBitmap,
                       DimensionManager dimensionManager) {

        super(IPhotoEditType.STICKER_TEXT, manager, matrix, stickerMatrix, stickerUri,
                centerXOnBitmap, centerYOnBitmap, dimensionManager);

        mStickerText = stickerText;
        mPaint.setAntiAlias(true);
        this.mColor = color;
        updateTextLength();
    }

    public void setText(String text) {
        mStickerText = text;
        updateTextLength();
    }

    private void updateTextLength() {
        mTextLength = StringUtil.getAdjustTextLength(mStickerText);
    }

    public void setIsEditing(boolean isEditing) {
        mIsEditing = isEditing;
    }

    public boolean getIsEditing() {
        return mIsEditing;
    }

    public int getTextLength() {
        return mTextLength;
    }

    public String getText() {
        return mStickerText;
    }

    public void setColor(int color) {
        this.mColor = color;
    }

    public int getColor() {
        return mColor;
    }

    public void updateTextStickerStatus(String text, int color, String bitmapUri) {
        mStickerText = text;
        mColor = color;
        replaceStickerUri(bitmapUri);
    }

    @Override
    public StickerData getStickerData() {
        StickerData data = new StickerData(getType(), mMatrix);
        data.bitmapUrl = mStickerUri;
        data.stickerMatrix = new Matrix(mSelfMatrix);
        data.setText(mStickerText);
        data.setColor(mColor);
        data.setMapPoints(mMapPoints);
        return data;
    }

    @Override
    public void draw(Canvas canvas) {
        if (!mIsEditing) {
            super.draw(canvas);
        }
    }
}
