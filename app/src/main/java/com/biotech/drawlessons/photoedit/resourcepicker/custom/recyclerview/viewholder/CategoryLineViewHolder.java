package com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.viewholder;

import android.view.View;
import android.widget.TextView;

import com.biotech.drawlessons.R;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.RecyclerViewAdapter;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.model.Data;


/***************************************************************************************************
 * 描述：
 *
 * 作者：champion
 *
 * 时间：18/2/8
 **************************************************************************************************/


public class CategoryLineViewHolder extends BaseViewHolder {

    private TextView mTvCategoryName;

    public CategoryLineViewHolder(View itemView, RecyclerViewAdapter adapter) {
        super(itemView, adapter);

        findViews();
    }

    private void findViews() {
        mTvCategoryName = itemView.findViewById(R.id.tv_category);
    }

    @Override
    public void refreshView(int position, Data data) {
        super.refreshView(position, data);
//        if (data != null && data instanceof PhotoFrameData) {
//            mTvCategoryName.setText(((PhotoFrameData) data).mCategoryName);
//        }
    }
}
