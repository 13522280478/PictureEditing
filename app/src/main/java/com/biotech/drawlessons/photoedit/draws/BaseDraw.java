package com.biotech.drawlessons.photoedit.draws;

import android.graphics.Canvas;
import android.graphics.Matrix;

import com.biotech.drawlessons.photoedit.utils.IPhotoEditType;

/**
 * Created by xintu on 2018/2/16.
 */

public abstract class BaseDraw implements IPhotoEditType, IDraw {
    private int mType;
    private IPhotoEditType.Order mOrder;
    protected Matrix mMatrix;
    public float[] mv = new float[9];
    protected DrawOnWhere mDrawOnWhere;
    private int mIndexInList;
    protected float mRealScaleValue;
    public enum DrawOnWhere{
        DRAW_ON_INTERNAL_BITMAP, DRAW_ON_SECOND_TEMP_BITMAP, DRAW_ON_SCREEN
    }

    BaseDraw(int type, Matrix matrix, DrawOnWhere drawOnWhere) {
        this.mType = type;
        mMatrix = matrix;
        mDrawOnWhere = drawOnWhere;
        if (mMatrix != null) {
            matrix.getValues(mv);
        }
        float scaleX = mv[Matrix.MSCALE_X];
        float skewY = mv[Matrix.MSKEW_Y];
        mRealScaleValue = (float) Math.sqrt(scaleX * scaleX + skewY * skewY);
        setOrder(mType);
    }

    private void setOrder(int type) {
        switch (type) {
            case IPhotoEditType.BRUSH_STICKERS:
                mOrder = Order.BRUSH;
                break;

            case IPhotoEditType.BRUSH_LIGHT_COLOR:
                mOrder = Order.BRUSH;
                break;

            case IPhotoEditType.BRUSH_NORMAL_COLOR:
                mOrder = Order.BRUSH;
                break;

            case IPhotoEditType.BRUSH_BACKGROUND:
                mOrder = Order.BRUSH;
                break;

            case IPhotoEditType.BRUSH_MOSAICS:
                mOrder = Order.MOSAICS;
                break;

            case IPhotoEditType.BRUSH_BLOCK_MOSAICS:
                mOrder = Order.MOSAICS;
                break;

            case IPhotoEditType.STICKER_TEXT:
                mOrder = Order.STICKER;
                break;

            case IPhotoEditType.STICKER_BITMAP:
                mOrder = Order.STICKER;
                break;

            case IPhotoEditType.PHOTO_FRAME_DRAW:
                mOrder = Order.PHOTO_FRAME;
                break;
        }
    }

    public void setDrawOnWhere(DrawOnWhere where) {
        mDrawOnWhere = where;
    }

    public DrawOnWhere getDrawOnWhere() {
        return mDrawOnWhere;
    }

    @Override
    public int getType() {
        return mType;
    }

    @Override
    public void draw(Canvas canvas) {

    }

    public void setIndexInList(int index){
        mIndexInList = index;
    }

    public int getIndexInList() {
        return mIndexInList;
    }

    @Override
    public int getOrderNumber() {
        return mOrder.getOrder();
    }

    public Order getOrder() {
        return mOrder;
    }

    public void updateMatrix(Matrix matrix) {
        mMatrix = matrix;
    }


    public void onScale(float scaleFactor, float centerX, float centerY) {
    }

    public void onRotate(float angle) {
    }
}
