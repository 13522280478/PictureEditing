package com.biotech.drawlessons.photoedit.resourcepicker;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;


import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.biotech.drawlessons.R;
import com.biotech.drawlessons.photoedit.resourcepicker.base.AbsPageAdapter;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.PageAdapter;
import com.biotech.drawlessons.photoedit.views.ActivityDragLayout;

import java.util.List;

/**
 * Created by xintu on 2018/3/11.
 */

public class DragMediaResourcePickerView extends ActivityDragLayout {
    private MediaResourcePickerView mResourcePickerView;
    private Context mContext;
    private ObjectAnimator mShowAnimator, mDismissAnimator;

    public DragMediaResourcePickerView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public DragMediaResourcePickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public DragMediaResourcePickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mShowAnimator = ObjectAnimator.ofFloat(mResourcePickerView, "translationY", h, 0);
        mShowAnimator.setDuration(200);

        mDismissAnimator = ObjectAnimator.ofFloat(mResourcePickerView, "translationY", h, 0);
        mDismissAnimator.setDuration(200);
    }

    private void init() {
        initView();
        initListener();
        setEnableDrag(true);
    }

    private void initView() {
        mResourcePickerView = new MediaResourcePickerView(mContext);
        int marginTop = (int) mContext.getResources().getDimension(R.dimen.resource_picker_margin_top);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.topMargin = marginTop;

        addView(mResourcePickerView, params);
    }

    private void initListener() {
        mResourcePickerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (recyclerView == null) {
                    return;
                }
                setEnableDrag(!recyclerView.canScrollVertically(-1));
            }
        });
    }

    public ValueAnimator getShowAnimator() {
        if (mShowAnimator == null) {
            mShowAnimator = ObjectAnimator.ofFloat(mResourcePickerView, "translationY", getRootView().getHeight(), 0);
            mShowAnimator.setDuration(200);
        }
        return mShowAnimator;
    }

    public ValueAnimator getDismissAnimator(int draggedValue) {
        if (mDismissAnimator == null) {
            mDismissAnimator = ObjectAnimator.ofFloat(mResourcePickerView, "translationY", 0, getRootView().getHeight() - draggedValue);
            mDismissAnimator.setDuration(200);
        } else {
            mDismissAnimator.setFloatValues(0, getHeight() - draggedValue);
        }

        return mDismissAnimator;
    }


    /**
     * =============这部分都是 MediaResourcePickerView 的方法，这里只是提供调用的接口==========
     */
    public void setCurItem(int index) {
        mResourcePickerView.setCurItem(index);
    }

    // 根据传过来的数据和mode设置不同的样式
    // 我其实也很不想这么写，但是没办法，同一个弹窗有两种不同的样式，只能这么来区分
    public void setDatas(List<PageAdapter.TabData> tabDatas, List<PageAdapter.PageContentData> contentDatas, int mode) {
        mResourcePickerView.setDatas(tabDatas, contentDatas, mode);
    }

    public void setAdapter(AbsPageAdapter adapter) {
        mResourcePickerView.setAdapter(adapter);
    }

    public void setOnResourceItemClickListener(MediaResourcePickerView.OnResourceItemClickListener listener) {
        mResourcePickerView.setOnResourceItemClickListener(listener);
    }

    public void notifyDataSetChange() {
        mResourcePickerView.notifyDataSetChanged();
    }

    public int getCurrentPageIndex() {
        return mResourcePickerView.getCurrentPageIndex();
    }


}
