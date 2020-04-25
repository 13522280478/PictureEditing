package com.biotech.drawlessons.photoedit.layers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

import com.biotech.drawlessons.photoedit.utils.BitmapsManager;
import com.biotech.drawlessons.photoedit.utils.IPhotoEditType;


/**
 * Created by xintu on 2018/1/30.
 * 最基础的画图片的 layer，什么都不做，只是把 bitmap 画出来
 * 绘制分为两步：第一步先绘制白色背景、第二步开始绘制图片
 */

public class BaseBitmapLayer extends BaseLayer {

    private BitmapsManager mBitmapManager;

    public BaseBitmapLayer(ILayerParent parent, Matrix matrix, BitmapsManager manager){
        super(parent, IPhotoEditType.LAYER_BASE_BITMAP, matrix);
        mBitmapManager = manager;
        setIsDealDrawEvent(true);
        setIsDealTouchEvent(false);
    }

    @Override
    public void switchToThisLayer() {
        setIsDealDrawEvent(true);
        setIsDealTouchEvent(false);
    }

    @Override
    public void switchToOtherLayer(BaseLayer otherLayerType) {
        setIsDealDrawEvent(true);
        setIsDealTouchEvent(false);
    }

    @Override
    public void onDraw(Canvas canvas) {
        Bitmap bitmap = mBitmapManager.getOriginalBitmap();
        if (mMatrix != null && bitmap != null) {
            if (mNeedClip) {
                canvas.save();
                canvas.concat(mMatrix);
                canvas.clipRect(mClipRect);
                canvas.drawBitmap(bitmap, 0, 0, null);
                canvas.restore();
            } else {
                canvas.drawBitmap(bitmap, mMatrix, null);
            }
        }
    }

    @Override
    public void updateMatrix(Matrix matrix) {
        mMatrix = matrix;
    }
}
