package com.biotech.drawlessons.photoedit.layers;


import android.graphics.Matrix;
import android.text.TextUtils;

import com.biotech.drawlessons.photoedit.draws.BaseDrawData;
import com.biotech.drawlessons.photoedit.draws.PhotoFrameDraw;
import com.biotech.drawlessons.photoedit.utils.BitmapsManager;
import com.biotech.drawlessons.photoedit.utils.DrawInvoker;
import com.biotech.drawlessons.photoedit.utils.IPhotoEditType;


/**
 * Created by xintu on 2018/2/13.
 * 默认的layer，不做任何事情。当切换到这个layer的时候，其他layer会回调 switchToOtherLayer 方法，
 * 就在回调的方法内处理具体的事情，比如说在stickerLayer中，如果切换到这个layer，就可以处理touch事件等。
 */

public class PhotoFrameLayer extends BaseLayer {
    private DrawInvoker mInvoker;
    private BitmapsManager mManager;
    private PhotoFrameDraw mCurPhotoFrame;

    public PhotoFrameLayer(ILayerParent parent, Matrix matrix, DrawInvoker invoker, BitmapsManager manager) {
        super(parent, IPhotoEditType.LAYER_PHOTO_FRAME, matrix);
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

    public void setPhotoFrameUri(String uri) {
        if (TextUtils.isEmpty(uri)) {
            return;
        }
        if (mCurPhotoFrame == null) {
            mCurPhotoFrame = new PhotoFrameDraw(mMatrix, mManager);
        }
        if (uri.equals(mCurPhotoFrame.getPhotoUrl())) {
            return;
        }
        mCurPhotoFrame.setPhoto(uri);
        mInvoker.setPhotoFrame(mCurPhotoFrame);
        mInvoker.drawPhotoFrame();
    }

    public void clearPhotoFrame() {
        if (mCurPhotoFrame != null) {
            mCurPhotoFrame.clearPhoto();
            mCurPhotoFrame = null;
        }
    }

    public PhotoFrameDraw createPhotoFrame(BaseDrawData data, BitmapsManager manager){
        PhotoFrameDraw draw = PhotoFrameDraw.createPhotoDraw(data, mMatrix, manager);
        draw.setPhoto(data.bitmapUrl);
        return draw;
    }
}
