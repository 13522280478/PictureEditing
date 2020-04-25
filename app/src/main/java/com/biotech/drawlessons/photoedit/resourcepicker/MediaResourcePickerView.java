package com.biotech.drawlessons.photoedit.resourcepicker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.biotech.drawlessons.R;
import com.biotech.drawlessons.photoedit.indicator.IndicatorViewPager;
import com.biotech.drawlessons.photoedit.indicator.ScrollIndicatorView;
import com.biotech.drawlessons.photoedit.resourcepicker.base.AbsPageAdapter;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.PageAdapter;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.event.ItemResourceClickEvent;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.model.Data;

import java.util.List;

/***************************************************************************************************
 * 描述：
 *
 * 作者：champion
 *
 * 时间：18/2/8
 **************************************************************************************************/


public class MediaResourcePickerView extends RelativeLayout
        implements IndicatorViewPager.OnIndicatorPageChangeListener, View.OnClickListener {

    public final static String PREFIX_TAG = "MPV_";

    private final static String TAG = PREFIX_TAG + "MediaResourcePickerView";

    // 两种不同的模式UI不一样
    public static final int BEAUTY_MODE = 1;
    public static final int STICKER_MODE = 2;

    private Context mContext;
    // 联动ViewPager
    private IndicatorViewPager mIndicatorViewPager;
    // 顶部可滑动tab
    private ScrollIndicatorView mTabIndicator;
    // 内容ViewPager
    private ViewPager mViewPager;
    private ImageView mIvCancel;
    private View mVDivider;


    private AbsPageAdapter<PageAdapter.TabData, PageAdapter.PageContentData> mIndicatorAdapter;

    public interface OnResourceItemClickListener {
        void onResourceClick(Data data);
        void onClearResourceClick();
    }

    private OnResourceItemClickListener mItemClickListener;


    public MediaResourcePickerView(Context context) {
        this(context, null);
    }

    public MediaResourcePickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediaResourcePickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    // public void setTabDatas(List<PageAdapter.TabData> datas) {
    //     if (mIndicatorAdapter == null) return;
    //     mIndicatorAdapter.setTabDatas(datas);
    //     mIndicatorAdapter.notifyDataSetChanged();
    // }
    //
    // public void setContentTabs(List<PageAdapter.PageContentData> datas) {
    //     if (mIndicatorAdapter == null) return;
    //     mIndicatorAdapter.setContentDatas(datas);
    //     mIndicatorAdapter.notifyDataSetChanged();
    // }

    public void setCurItem(int index) {
        if (mIndicatorAdapter == null || mIndicatorAdapter == null) return;
        if (!mIndicatorAdapter.indexValid(index)) return;
        mIndicatorViewPager.setCurrentItem(index, true);
    }

    // public void setDatas(List<PageAdapter.TabData> tabDatas, List<PageAdapter.PageContentData> contentDatas) {
    //     if (mIndicatorAdapter == null) return;
    //     if (tabDatas == null || contentDatas == null) return;
    //     if (tabDatas.size() != contentDatas.size()) return;
    //     this.mIndicatorAdapter.setDatas(tabDatas, contentDatas);
    // }

    public void setDatas(List<PageAdapter.TabData> tabDatas,
                         List<PageAdapter.PageContentData> contentDatas, int mode) {
        if (mIndicatorAdapter == null) return;
        if (tabDatas == null || contentDatas == null) return;
        if (tabDatas.size() != contentDatas.size()) return;
        if (mode == BEAUTY_MODE) {
            mIvCancel.setVisibility(VISIBLE);
            mVDivider.setVisibility(VISIBLE);
        } else if (mode == STICKER_MODE) {
            mIvCancel.setVisibility(GONE);
            mVDivider.setVisibility(GONE);
        }
        this.mIndicatorAdapter.setDatas(tabDatas, contentDatas);
    }

    public void setAdapter(AbsPageAdapter<PageAdapter.TabData, PageAdapter.PageContentData> adapter) {
        this.mIndicatorAdapter = adapter;
        this.mIndicatorAdapter.notifyDataSetChanged();
    }

    public void setOnResourceItemClickListener(OnResourceItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public AbsPageAdapter getAdapter() {
        return mIndicatorAdapter;
    }

    public void notifyDataSetChanged() {
        mIndicatorAdapter.notifyDataSetChanged();
    }

    private void init() {
        inflate(mContext, R.layout.layout_media_resource_picker, this);

        findViews();
        initViews();
        initListeners();

//        EventHelper2.getDefault().register(this);
    }


    private void findViews() {
        mViewPager = findViewById(R.id.vp_content);
        mTabIndicator = findViewById(R.id.tabs_indicator);
        mVDivider = findViewById(R.id.v_left_divider);
        mIvCancel = findViewById(R.id.img_cancel);
        mIvCancel.setOnClickListener(this);
    }

    private void initViews() {
        mIndicatorViewPager = new IndicatorViewPager(mTabIndicator, mViewPager);
        mIndicatorAdapter = new PageAdapter(mContext);
        mIndicatorViewPager.setAdapter(mIndicatorAdapter);
    }

    private void initListeners() {
        mIndicatorViewPager.setOnIndicatorPageChangeListener(this);
    }

    public int getCurrentPageIndex() {
        return mIndicatorViewPager == null ? 0 : mIndicatorViewPager.getCurrentItem();
    }

    public void setOnScrollListener(RecyclerView.OnScrollListener listener) {
        mIndicatorAdapter.setOnRecyclerViewScrollListener(listener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_cancel:
                if (mItemClickListener != null) {
                    mItemClickListener.onClearResourceClick();
                }
                break;
        }
    }

    /**
     * 滑动改变的回调
     *
     * @param preItem
     * @param currentItem
     */
    @Override
    public void onIndicatorPageChange(int preItem, int currentItem) {
        View curView = mTabIndicator.getItemView(currentItem);
        View preView = mTabIndicator.getItemView(preItem);

        if (curView == null || preView == null) return;

        if (mIndicatorAdapter != null) {
            mIndicatorAdapter.onIndicatorPageChange(preItem, currentItem, preView, curView);
        }

        // if (view != null) {
        //     if (!indexGetValid(currentItem, mTabDatas)) return;
        //     TabData item = mTabDatas.get(currentItem);
        //     ImageView imgView = view.findViewById(R.id.img_tab_icon);
        //     if (imgView != null) {
        //         imgView.setImageResource(item.iconIdSelectLight);
        //     }
        // }
        //
        // View preView = mTabIndicator.getItemView(preItem);
        // if (view != null) {
        //     if (!indexGetValid(preItem, mTabDatas)) return;
        //     TabDataItem item = mTabDatas.get(preItem);
        //     ImageView imgView = preView.findViewById(R.id.img_tab_icon);
        //     if (imgView != null) {
        //         imgView.setImageResource(item.iconIdUnSelectLight);
        //     }
        // }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onItemResourceClickEvent(ItemResourceClickEvent event) {
        if (mItemClickListener != null) {
            mItemClickListener.onResourceClick(event.mData);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        EventHelper2.getDefault().unregister(this);
        mIndicatorAdapter.onRecyclerViewDetachedFromWindow();
    }

}
