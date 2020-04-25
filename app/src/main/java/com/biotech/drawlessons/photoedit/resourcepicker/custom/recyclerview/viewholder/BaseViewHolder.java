package com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.viewholder;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.RecyclerViewAdapter;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.model.Data;

/***************************************************************************************************
 * 描述：
 *
 * 作者：champion
 *
 * 时间：18/2/7
 **************************************************************************************************/


public class BaseViewHolder extends RecyclerView.ViewHolder {

    protected Data mData;
    protected RecyclerViewAdapter mAdapter;

    public BaseViewHolder(View itemView, RecyclerViewAdapter adapter) {
        super(itemView);
        this.mAdapter = adapter;
    }

    public void refreshView(int position, Data data) {
        mData = data;
        mData.setPosition(position);
    }

}
