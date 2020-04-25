package com.biotech.drawlessons.photoedit.tools;


import androidx.annotation.ColorRes;

/**
 * Created by yalingcai on 2017/12/6.
 */

public class ColorPickerBean extends BaseBean {
    private boolean isClicked;
    private @ColorRes
    int colorRes;
    private String mBitmapType;

    public ColorPickerBean(int type, @ColorRes int colorRes) {
        setType(type);
        this.colorRes = colorRes;
    }

    public ColorPickerBean(int type, boolean isClick, @ColorRes int colorData) {
        setType(type);
        setColorRes(colorData);
        this.isClicked = isClick;
    }

    public boolean getClick() {
        return isClicked;
    }

    public void setClick(Boolean click) {
        isClicked = click;
    }

    public @ColorRes
    int getColorRes() {
        return colorRes;
    }

    public void setColorRes(@ColorRes int colorRes) {
        this.colorRes = colorRes;
    }

    public void setBitmapType(String type) {
        mBitmapType = type;
    }

    public String getBitmapType() {
        return mBitmapType;
    }
}
