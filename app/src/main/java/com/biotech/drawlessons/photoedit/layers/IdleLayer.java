package com.biotech.drawlessons.photoedit.layers;


import com.biotech.drawlessons.photoedit.utils.IPhotoEditType;

/**
 * Created by xintu on 2018/2/13.
 * 默认的layer，不做任何事情。当切换到这个layer的时候，其他layer会回调 switchToOtherLayer 方法，
 * 就在回调的方法内处理具体的事情，比如说在stickerLayer中，如果切换到这个layer，就可以处理touch事件等。
 */

public class IdleLayer extends BaseLayer {
    public IdleLayer(ILayerParent parent) {
        super(parent, IPhotoEditType.LAYER_IDL, null);
    }

    @Override
    public void switchToThisLayer() {
        setIsDealDrawEvent(false);
        setIsDealTouchEvent(false);
    }

    @Override
    public void switchToOtherLayer(BaseLayer otherLayerType) {
        setIsDealDrawEvent(false);
        setIsDealTouchEvent(false);
    }
}
