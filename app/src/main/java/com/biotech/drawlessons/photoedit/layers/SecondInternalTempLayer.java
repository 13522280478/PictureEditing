package com.biotech.drawlessons.photoedit.layers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

import com.biotech.drawlessons.photoedit.utils.BitmapsManager;
import com.biotech.drawlessons.photoedit.utils.IPhotoEditType;

/**
 * Created by xintu on 2018/3/30.
 */

public class SecondInternalTempLayer extends BaseLayer{
    private BitmapsManager mManager;

    public SecondInternalTempLayer(ILayerParent parent, Matrix matrix, BitmapsManager manager) {
        super(parent, IPhotoEditType.LAYER_SECOND_INTERNAL_BITMAP, matrix);
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
        Bitmap bitmap = mManager.getSecondTempBitmap();
        if (bitmap == null || mMatrix == null) {
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
}
