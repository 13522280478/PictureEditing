package com.biotech.drawlessons.photoedit.layers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

import com.biotech.drawlessons.photoedit.utils.BitmapsManager;
import com.biotech.drawlessons.photoedit.utils.DrawInvoker;
import com.biotech.drawlessons.photoedit.utils.IPhotoEditType;


/**
 * Created by xintu on 2018/1/30.
 * 这一层是图片绘制的中间层。初始化该层的时候，会新建一个和原图一样大小的透明的 bitmap，用来绘制一些队列中
 * 的画笔。这样做，可以避免每一次 onDraw 需要遍历队列中所有的笔画。
 * 这是用空间来换时间的策略。具体的哪些笔画绘制在该层，由 LayerManager 来统一调度。
 */

public class InternalTempLayer extends BaseLayer {
    private DrawInvoker mInvoker;
    private BitmapsManager mManager;
    public InternalTempLayer(ILayerParent parent, Matrix matrix, DrawInvoker invoker, BitmapsManager manager) {
        super(parent, IPhotoEditType.LAYER_INTERNAL_BITMAP, matrix);
        mInvoker = invoker;
        mManager = manager;
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
        Bitmap bitmap = mManager.getInternalBitmap();
        if (bitmap == null) {
            return;
        }
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

    @Override
    public void updateMatrix(Matrix matrix) {
        mMatrix = matrix;
    }
}
