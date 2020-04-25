package com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.viewholder;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.ViewUtils;

import com.biotech.drawlessons.BaseApplication;
import com.biotech.drawlessons.R;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.RecyclerViewAdapter;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.model.Data;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.model.FilterData;
import com.biotech.drawlessons.photoedit.test.FakeData;


/***************************************************************************************************
 * 描述：滤镜类型view holder
 *
 * 作者：champion
 *
 * 时间：18/2/11
 **************************************************************************************************/


public class ImageFilterViewHolder extends ImagesViewHolder {

    // 滤镜名称
    private TextView mTvFilterName;

    public ImageFilterViewHolder(View itemView, RecyclerViewAdapter adapter) {
        super(itemView, adapter);
    }

    @Override
    protected void findViews() {
        super.findViews();
        mTvFilterName = itemView.findViewById(R.id.tv_item_name);
    }

    @Override
    public void refreshView(int position, Data data) {
        super.refreshView(position, data);
//        ViewUtils.show(mTvFilterName, mImgResource);
        mTvFilterName.setVisibility(View.VISIBLE);
        mImgResource.setVisibility(View.VISIBLE);
        if (data != null && data instanceof FilterData) {
            mTvFilterName.setText(((FilterData) data).name);

            if (FakeData.ORIGINAL_FILTER_TYPE.equals(((FilterData) data).filterType) && ((FilterData) data).drawableRes != 0) {
                mImgResource.setImageDrawable(BaseApplication.getInstance().getResources().getDrawable(R.drawable.bg_filter_view_preview));
            } else if (!TextUtils.isEmpty(((FilterData) data).filterType)){
//                GlideUtils.setImageBmp(BitmapsManager.getFilterUri(((FilterData) data).filterType), mImgResource);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int resId = v.getId();
        switch (resId) {
            case R.id.img_item_resource:
                // 选中相关资源
//                EventHelper2.getDefault().post(new ItemResourceClickEvent(mData));
                break;
        }

    }
}
