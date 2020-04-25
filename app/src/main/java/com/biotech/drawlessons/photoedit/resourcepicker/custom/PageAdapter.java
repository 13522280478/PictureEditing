package com.biotech.drawlessons.photoedit.resourcepicker.custom;

import android.content.Context;
import android.view.View;

import com.biotech.drawlessons.photoedit.resourcepicker.base.AbsPageAdapter;
import com.biotech.drawlessons.photoedit.resourcepicker.base.IViewHolder;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.model.BaseData;

import java.util.ArrayList;
import java.util.List;

/***************************************************************************************************
 * 描述：
 *
 * 作者：champion
 *
 * 时间：18/2/6
 **************************************************************************************************/


public class PageAdapter extends AbsPageAdapter<PageAdapter.TabData,
        PageAdapter.PageContentData> {


    public PageAdapter(Context context) {
        super(context);
    }


    @Override
    public void onIndicatorPageChange(int preItem, int currentItem, View preView, View curView) {
        if (!indexGetValid(currentItem, mTabDatas)) return;
        if (!indexGetValid(preItem, mTabDatas)) return;
        TabViewHolder preTabViewHolder = (TabViewHolder) preView.getTag();
        TabViewHolder curTabViewHolder = (TabViewHolder) curView.getTag();

        preTabViewHolder.onViewUnSelected(mTabDatas.get(preItem));
        curTabViewHolder.onViewSelected(mTabDatas.get(currentItem));
    }

    @Override
    protected IViewHolder createViewHolderForTab() {
        return new TabViewHolder();
    }

    @Override
    protected IViewHolder createViewHolderForContent() {
        return new PageContentViewHolder();
    }


    /**
     * 顶部tab的数据
     */
    public static class TabData {
        public int iconIdSelectLight;
        public int iconIdSelectNight;
        public int iconIdUnSelectLight;
        public int iconIdUnSelectNight;
        public String iconSelectedUrl;
        public String iconUnselectedUrl;
        public boolean showName;

        public String name;

        public TabData(int iconIdSelectLight,
                       int iconIdSelectNight,
                       int iconIdUnSelectLight,
                       int iconIdUnSelectNight,
                       boolean showName,
                       String name) {
            this.iconIdSelectLight = iconIdSelectLight;
            this.iconIdSelectNight = iconIdSelectNight;
            this.iconIdUnSelectLight = iconIdUnSelectLight;
            this.iconIdUnSelectNight = iconIdUnSelectNight;
            this.name = name;
            this.showName = showName;
        }

        public TabData(String iconSelectedUrl, String iconUnselectedUrl, boolean showName,String name) {
            this.iconSelectedUrl = iconSelectedUrl;
            this.iconUnselectedUrl = iconUnselectedUrl;
            this.name = name;
            this.showName = showName;
        }

        public void setShowName(boolean showName) {
            this.showName = showName;
        }
    }


    /**
     * 每一页的数据
     */
    public static class PageContentData {
        public String mBuryCode;
        public ArrayList<BaseData> mContentList;

    }
}
