package com.biotech.drawlessons.photoedit.resourcepicker.custom.event;


import com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.model.Data;

/***************************************************************************************************
 * 描述：单个资源点击事件
 *
 * 作者：champion
 *
 * 时间：18/2/13
 **************************************************************************************************/


public class ItemResourceClickEvent {

    public Data mData;

    public ItemResourceClickEvent(Data data) {
        this.mData = data;
    }

}
