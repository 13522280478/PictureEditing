package com.biotech.drawlessons.photoedit.resourcepicker.custom;

import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.biotech.drawlessons.R;
import com.biotech.drawlessons.UtilsKt;
import com.biotech.drawlessons.photoedit.resourcepicker.MediaResourcePickerView;
import com.biotech.drawlessons.photoedit.resourcepicker.base.IViewHolder;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.RecyclerViewAdapter;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.model.Data;


/***************************************************************************************************
 * 描述：每页内容的view holder
 *
 * 作者：champion
 *
 * 时间：18/2/6
 **************************************************************************************************/


public class PageContentViewHolder implements IViewHolder<PageAdapter.PageContentData> {

    private final static String TAG = MediaResourcePickerView.PREFIX_TAG + "PageContentViewHolder";
    private final static boolean DEBUG = false;

    private View mViewContainer;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdatper;

    private int mPreSpanSize = 0;
    private int mTop;
    private RecyclerView.ItemDecoration mDecoration = new RecyclerView.ItemDecoration() {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) view.getLayoutParams();
            // 占用列数
            int spanSize = params.getSpanSize();
            // 列index
            int spanIndex = params.getSpanIndex();
            int position = parent.getChildAdapterPosition(view);
            int top = 0;
            // 类别分割线
            if (spanSize == 4) {
                outRect.right = 0;
                outRect.left = 0;
                outRect.top = (int) UtilsKt.dp2px(3.5f);
                outRect.bottom = (int) UtilsKt.dp2px(3.5f);
            } else {
//                if (spanIndex == 0) {
//                    if (mPreSpanSize == 4) {
//                        top = 0;
//                    } else {
//                        top = ScreenUtils.dp2Px(mRecyclerView.getContext(), 21);
//                    }
//                }
                outRect.bottom = (int) UtilsKt.dp2px(10.5f);
                outRect.top = (int) UtilsKt.dp2px(10.5f);
            }
            mPreSpanSize = spanSize;
        }
    };

    @Override
    public void init(LayoutInflater inflater, int position, ViewGroup container) {
        mViewContainer = inflater.inflate(R.layout.layout_media_resource_grid_view, container, false);

        mRecyclerView = mViewContainer.findViewById(R.id.item_rgv);
        mRecyclerView.removeItemDecoration(mDecoration);
        mRecyclerView.addItemDecoration(mDecoration);
    }

    @Override
    public void bindViewData(int position, final PageAdapter.PageContentData model) {
        mTop = (int) UtilsKt.dp2px(21);
        if (mAdatper == null) {
            mAdatper = new RecyclerViewAdapter(mRecyclerView.getContext());
            mRecyclerView.setAdapter(mAdatper);
            mRecyclerView.setLayoutManager(new GridLayoutManager(mRecyclerView.getContext(), 4));
            mRecyclerView.setItemAnimator(null);
        }

        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int contentPosition) {
                int type = model.mContentList.get(contentPosition).getViewItemType();
                if (type == Data.CATEGORY_LINE) {
                    return 4;
                }
                return 1;
            }
        });

//        mAdatper.setDatas(model.mContentList);
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    public View getItemView() {
        return mViewContainer;
    }

    @Override
    public void onViewSelected(PageAdapter.PageContentData model) {
        mAdatper.notifyDataSetChanged();
    }

    @Override
    public void onViewUnSelected(PageAdapter.PageContentData model) {

    }

    @Override
    public void onRecyclerViewDetachedFromWindow() {
        if (mAdatper != null) {
            mAdatper.unRegister();
        }
    }
}
