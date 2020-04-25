package com.biotech.drawlessons.photoedit.resourcepicker.custom;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.biotech.drawlessons.R;
import com.biotech.drawlessons.photoedit.resourcepicker.MediaResourcePickerView;
import com.biotech.drawlessons.photoedit.resourcepicker.base.IViewHolder;


/***************************************************************************************************
 * 描述：顶部tab view holder
 *
 * 作者：champion
 *
 * 时间：18/2/6
 **************************************************************************************************/


public class TabViewHolder implements IViewHolder<PageAdapter.TabData> {

    private final static String TAG = MediaResourcePickerView.PREFIX_TAG + "TabViewHolder";

    private View mViewContainer;

    private TextView mTvTabName;
    private ImageView mImgTab;

    public TabViewHolder() {
    }

    public TabViewHolder(View contentView) {
        this.mViewContainer = contentView;
        findViews();
    }


    @Override
    public void init(LayoutInflater inflater, int position, ViewGroup container) {
        mViewContainer = inflater.inflate(R.layout.item_media_resource_tab, container, false);
        findViews();
    }

    @Override
    public void bindViewData(int position, PageAdapter.TabData model) {
        if (model.showName) {
            mTvTabName.setVisibility(View.VISIBLE);
            mTvTabName.setText(model.name);
        } else {
            mTvTabName.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(model.iconSelectedUrl)) {
//            GlideUtils.setImageBmp(model.iconSelectedUrl, mImgTab);
        } else {
            mImgTab.setImageResource(model.iconIdUnSelectLight);
        }
    }

    @Override
    public View getItemView() {
        return mViewContainer;
    }

    @Override
    public void onViewSelected(PageAdapter.TabData model) {
        if (model == null) return;
        if (!TextUtils.isEmpty(model.iconSelectedUrl)) {
//            GlideUtils.setImageBmp(model.iconSelectedUrl, mImgTab);
        } else {
            mImgTab.setImageResource(model.iconIdSelectLight);
        }
//        mTvTabName.setTextColor(StateListModel.getColorStatelist(SnsApplication.getInstance(), StateListModel.Ylw_1));

    }

    @Override
    public void onViewUnSelected(PageAdapter.TabData model) {
        if (model == null) return;
        if (!TextUtils.isEmpty(model.iconUnselectedUrl)) {
//            GlideUtils.setImageBmp(model.iconUnselectedUrl, mImgTab);
        } else {
            mImgTab.setImageResource(model.iconIdUnSelectLight);
        }
//        mTvTabName.setTextColor(StateListModel.getColorStatelist(SnsApplication.getInstance(), StateListModel.Blk_5));
    }

    @Override
    public void onRecyclerViewDetachedFromWindow() {

    }

    private void findViews() {
        mTvTabName = mViewContainer.findViewById(R.id.tv_tab_name);
        mImgTab = mViewContainer.findViewById(R.id.img_tab_icon);
    }

}
