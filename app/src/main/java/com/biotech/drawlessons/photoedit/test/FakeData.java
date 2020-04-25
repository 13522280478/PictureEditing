package com.biotech.drawlessons.photoedit.test;


import com.biotech.drawlessons.R;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.PageAdapter;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.model.BaseData;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.model.FilterData;

import java.util.ArrayList;
import java.util.List;

/***************************************************************************************************
 * 描述：
 *
 * 作者：champion
 *
 * 时间：18/2/11
 **************************************************************************************************/


public class FakeData {
    public static final String ORIGINAL_FILTER_TYPE = "original";
    public static final String IFAMARO_FILTER_NAME = "流年";
    public static final String IFEARLYBIRD_FILTER_NAME = "城南旧事";
    public static final String IFHUDSON_FILTER_NAME = "且听风吟";
    public static final String IFLOMO_FILTER_NAME = "布达佩斯";
    public static final String IFNASHVILLE_FILTER_NAME = "暖茶";
    public static final String IFSIERRA_FILTER_NAME = "自然";
    public static final String IFSUTRO_FILTER_NAME = "夏卡尔";

    public static void createContentData() {

    }

    public static ArrayList<BaseData> buildRecendDatas() {
        ArrayList<BaseData> datas = new ArrayList<>();
        return datas;
    }


    public static ArrayList<FilterData> buildFilterDatas() {
        FilterData data = new FilterData("日光", "日光");

        ArrayList<FilterData> datas = new ArrayList<>();
        datas.add(data);
        return datas;
    }

    public static PageAdapter.TabData buildPhotoFrameTabData(boolean showName) {
        return new PageAdapter.TabData(
                R.drawable.icon_picture_frame_selected_light,
                R.drawable.icon_picture_frame_selected_night,
                R.drawable.icon_picture_frame_unselected_light,
                R.drawable.icon_picture_frame_unselected_night, showName,"相框");
    }

    public static PageAdapter.TabData buildRecentTabData(boolean showName) {
        return new PageAdapter.TabData(
                R.drawable.icon_media_edit_beauty_recent_use_selected_light,
                R.drawable.icon_media_edit_beauty_recent_use_selected_night,
                R.drawable.icon_media_edit_beauty_recent_use_unselected_light,
                R.drawable.icon_media_edit_beauty_recent_use_unselected_night,
                showName, "最近");
    }

    public static PageAdapter.TabData buildFilterTabData(boolean showName) {
        return new PageAdapter.TabData(
                R.drawable.icon_filter_selected_light,
                R.drawable.icon_filter_selected_night,
                R.drawable.icon_filter_unselected_light,
                R.drawable.icon_filter_unselected_night, showName,"滤镜");
    }

    public static PageAdapter.PageContentData buildFilterContentData() {
        PageAdapter.PageContentData contentData = new PageAdapter.PageContentData();
        contentData.mContentList = new ArrayList<>();
        contentData.mContentList.addAll(buildFilterDataList());
        return contentData;
    }

    public static ArrayList<FilterData> buildFilterDataList() {
        ArrayList<FilterData> res = new ArrayList<>();
        FilterData filterData = new FilterData(ORIGINAL_FILTER_TYPE, "原图");
        filterData.selected = true;
        filterData.drawableRes = R.drawable.bg_filter_view_preview;

        FilterData filterData1 = new FilterData("IFAmaro", IFAMARO_FILTER_NAME);
        FilterData filterData2 = new FilterData("IFEarlybird",IFEARLYBIRD_FILTER_NAME);
        FilterData filterData3 = new FilterData("IFHudson",IFHUDSON_FILTER_NAME);
        FilterData filterData4 = new FilterData("IFLomo",IFLOMO_FILTER_NAME);
        FilterData filterData5 = new FilterData("IFNashville",IFNASHVILLE_FILTER_NAME);
        FilterData filterData6 = new FilterData("IFSierra",IFSIERRA_FILTER_NAME);
        FilterData filterData7 = new FilterData("IFSutro",IFSUTRO_FILTER_NAME);
        res.add(filterData);
        res.add(filterData1);
        res.add(filterData2);
        res.add(filterData3);
        res.add(filterData4);
        res.add(filterData5);
        res.add(filterData6);
        res.add(filterData7);

        return res;
    }
}
