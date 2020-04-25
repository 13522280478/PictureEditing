package com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.viewholder;

import android.view.View;
import android.widget.ImageView;

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


public class ImagesViewHolder extends BaseViewHolder implements View.OnClickListener{

    protected ImageView mImgResource;

    public ImagesViewHolder(View itemView, RecyclerViewAdapter adapter) {
        super(itemView, adapter);

        findViews();

        initListeners();
    }

    protected void findViews() {
        mImgResource = itemView.findViewById(R.id.img_item_resource);
    }

    protected void initListeners() {
        mImgResource.setOnClickListener(this);
    }

    @Override
    public void refreshView(int position, Data data) {
        super.refreshView(position, data);
    }

    @Override
    public void onClick(View v) {

    }
}
