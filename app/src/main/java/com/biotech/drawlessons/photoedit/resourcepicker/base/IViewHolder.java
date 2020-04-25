package com.biotech.drawlessons.photoedit.resourcepicker.base;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/***************************************************************************************************
 * 描述：
 *
 * 作者：champion
 *
 * 时间：18/2/6
 **************************************************************************************************/


public interface IViewHolder<M> {

    void init(LayoutInflater inflater, int position, ViewGroup container);

    /**
     * 绑定和View相关联的数据
     */
    void bindViewData(int position, M model);

    View getItemView();

    void onViewSelected(M model);

    void onViewUnSelected(M model);

    void onRecyclerViewDetachedFromWindow();

}
