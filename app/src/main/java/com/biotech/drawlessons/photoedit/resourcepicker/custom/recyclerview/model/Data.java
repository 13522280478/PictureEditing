package com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.model;

/***************************************************************************************************
 * 描述：接口用于定义 page content里 RecyclerView里数据类型
 *
 * 作者：champion
 *
 * 时间：18/2/11
 **************************************************************************************************/

public interface Data {

    // 普通四个图片并排的类型
    int FOUR_IMAGE_TYPE = 1;

    // 相框类型 类别分割线
    int CATEGORY_LINE = 2;

    // 滤镜类型
    int IMAGE_FILTER_TYPE = 3;

    // 相框类型
    int PHOTO_FRAME_TYPE = 4;

    // 贴纸类型
    int STICKER_TYPE = 5;


    int getViewItemType();

    void setPosition(int position);

    int getPosition();
}
