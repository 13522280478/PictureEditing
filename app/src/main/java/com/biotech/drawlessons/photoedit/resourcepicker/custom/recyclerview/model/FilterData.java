package com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.model;


import androidx.annotation.DrawableRes;

/***************************************************************************************************
 * 描述：滤镜数据类型
 *
 * 作者：champion
 *
 * 时间：18/2/11
 **************************************************************************************************/


public class FilterData extends BaseData {

    public String name;
    public String filterType;
    public @DrawableRes
    int drawableRes;
    public int type;
    private int position;
    public boolean selected;

    public FilterData(String filterType,String name) {
        this.name = name;
        this.filterType = filterType;
    }

    @Override
    public int getViewItemType() {
        return Data.IMAGE_FILTER_TYPE;
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
    }


    @Override
    public int getPosition() {
        return position;
    }
}
