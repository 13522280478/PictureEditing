package com.biotech.drawlessons.photoedit.tools;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.biotech.drawlessons.R;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.model.FilterData;
import com.biotech.drawlessons.photoedit.test.FakeData;

import java.util.ArrayList;

/**
 * Created by xintu on 2018/2/21.
 */

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<FilterData> mDatas;
    private FilterDataClickListener mListener;

    public FilterAdapter(Context context) {
        mContext = context;
        initFakeDatas();
    }

    public void setDatas(ArrayList<FilterData> datas) {
        mDatas = datas;
    }

    private void initFakeDatas() {
        mDatas = FakeData.buildFilterDataList();
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(FilterDataClickListener listener) {
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.filter_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.ivFilter = view.findViewById(R.id.iv_filter);
        holder.tvFilter = view.findViewById(R.id.tv_filter);
        holder.vMask = view.findViewById(R.id.v_mask);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final FilterData bean = mDatas.get(position);
        if (bean == null) {
            return;
        }
        holder.tvFilter.setText(bean.name);
        if (position == 0 && bean.drawableRes != 0) {
            holder.ivFilter.setImageDrawable(mContext.getResources().getDrawable(bean.drawableRes));
        } else {
            // todo:::
//            GlideUtils.setImageBmp(BitmapsManager.getFilterUri(bean.filterType), holder.ivFilter);
        }
        if (bean.selected) {
            holder.vMask.setVisibility(View.VISIBLE);
        } else {
            holder.vMask.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onFilterItemClick(holder.itemView, holder.getAdapterPosition(), bean);
                    selectFilter(bean.filterType);
                }
            }
        });
    }

    public void selectFilter(String filterType) {
        if (TextUtils.isEmpty(filterType)) {
            return;
        }
        for (FilterData data : mDatas) {
            if (filterType.equals(data.filterType)) {
                data.selected = true;
            } else {
                data.selected = false;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFilter;
        TextView tvFilter;
        View vMask;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }


}
