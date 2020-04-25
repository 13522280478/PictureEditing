package com.biotech.drawlessons.photoedit.resourcepicker.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.recyclerview.widget.RecyclerView;

import com.biotech.drawlessons.photoedit.indicator.IndicatorViewPager;
import com.biotech.drawlessons.photoedit.resourcepicker.MediaResourcePickerView;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.PageContentViewHolder;
import com.biotech.drawlessons.photoedit.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/***************************************************************************************************
 * 描述：顶部tab 内容pager 的UI和数据的适配器
 *
 * 作者：champion
 *
 * 时间：18/2/6
 **************************************************************************************************/


public abstract class AbsPageAdapter<M1, M2> extends IndicatorViewPager.IndicatorViewPagerAdapter {

    private final static String TAG = MediaResourcePickerView.PREFIX_TAG + "AbsPageAdapter";

    private final static boolean DEBUG = true;


    protected List<M1> mTabDatas;

    protected List<M2> mContentDatas;

    protected List<IViewHolder> mTabViewHolders;
    protected List<IViewHolder> mPageContentViewHolders;

    private LayoutInflater mInflater;
    private RecyclerView.OnScrollListener mScrollListener;

    public AbsPageAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }


    public void setDatas(List<M1> tabDatas, List<M2> contentDatas) {
        if (tabDatas == null || contentDatas == null) {
            return;
        }
        if (tabDatas.size() != contentDatas.size()) {
            return;
        }
        this.mTabDatas = tabDatas;
        this.mContentDatas = contentDatas;
        notifyDataSetChanged();
    }

    public boolean indexValid(int index) {
        return indexGetValid(index, mTabDatas) && indexGetValid(index, mContentDatas);
    }

    @Override
    public void refreshNowTab() {
//        LogUtils.d(DEBUG, TAG, "refreshNowTab");
    }

    // 页数
    @Override
    public int getCount() {
        return mTabDatas == null ? 0 : mTabDatas.size();
    }

    @Override
    public View getViewForTab(int position, View convertView, ViewGroup container) {
//        LogUtils.d(DEBUG, TAG, "getViewForTab position: " + position);
        if (mTabViewHolders == null) {
            mTabViewHolders = new ArrayList<>();
        }
        IViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = createViewHolderForTab();
            viewHolder.init(mInflater, position, container);

             mTabViewHolders.add(viewHolder);
        } else {
            viewHolder = (IViewHolder) convertView.getTag();
        }
        viewHolder.bindViewData(position, mTabDatas.get(position));
        convertView = viewHolder.getItemView();
        convertView.setTag(viewHolder);
        return convertView;
    }

    @Override
    public View getViewForPage(int position, View convertView, ViewGroup container) {
//        LogUtils.d(DEBUG, TAG, "getViewForPage position: " + position);
        if (mPageContentViewHolders == null) {
            mPageContentViewHolders = new ArrayList<>();
        }
        IViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = createViewHolderForContent();
            viewHolder.init(mInflater, position, container);

            mPageContentViewHolders.add(viewHolder);
        } else {
            viewHolder = (IViewHolder) convertView.getTag();
        }
        if (mScrollListener != null) {
            ((PageContentViewHolder)viewHolder).getRecyclerView().clearOnScrollListeners();
            ((PageContentViewHolder)viewHolder).getRecyclerView().addOnScrollListener(mScrollListener);
        }

        viewHolder.bindViewData(position, mContentDatas.get(position));
        convertView = viewHolder.getItemView();
        convertView.setTag(viewHolder);
        return convertView;
    }

    public void setOnRecyclerViewScrollListener(RecyclerView.OnScrollListener listener) {
        mScrollListener = listener;
    }

    public void onRecyclerViewDetachedFromWindow() {
        if (mTabViewHolders != null) {
            for (IViewHolder tabs : mTabViewHolders) {
                tabs.onRecyclerViewDetachedFromWindow();
            }
        }

        if (mPageContentViewHolders != null) {
            for (IViewHolder content : mPageContentViewHolders) {
                content.onRecyclerViewDetachedFromWindow();
            }
        }
    }

    public List<M1> getTabDatas() {
        return mTabDatas;
    }

    public List<M2> getContentDatas() {
        return mContentDatas;
    }

    protected boolean indexGetValid(int index, List datas) {
        return CollectionUtils.isLargerThan(datas, index);
    }

    public abstract void onIndicatorPageChange(int preItem, int currentItem, View preView, View curView);

    protected abstract IViewHolder createViewHolderForTab();

    protected abstract IViewHolder createViewHolderForContent();

}
