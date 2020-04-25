package com.biotech.drawlessons.photoedit.tools;


import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;


import androidx.recyclerview.widget.RecyclerView;

import com.biotech.drawlessons.BaseApplication;
import com.biotech.drawlessons.R;
import com.biotech.drawlessons.photoedit.utils.BitmapsManager;
import com.biotech.drawlessons.photoedit.utils.IPhotoEditType;
import com.biotech.drawlessons.photoedit.views.RoundedImageView;

import java.util.List;

public class ColorPickerAdapter extends RecyclerView.Adapter<ColorPickerAdapter.ViewHolder> implements OnClickListener {
    //颜色数据
    private List<ColorPickerBean> mData;
    private ColorPickerView.OnColorPickerListener mOnItemClickListener;

    private Context mContext;

    public ColorPickerAdapter(Context context, List<ColorPickerBean> mData) {
        this.mData = mData;
        mContext = context;
    }

    public void setOnItemClickListener(ColorPickerView.OnColorPickerListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.color_item, viewGroup, false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        //是否点击
        //TODO:日夜间模式
        viewHolder.mRec.setBorderColor(getResources().getColor(R.color.Blk_5));
        if (mData.get(position).getClick()) {
            viewHolder.itemView.setBackgroundDrawable(getResources().getDrawable(R.drawable.color_picker_item_bg_light));
        } else {
            viewHolder.itemView.setBackgroundDrawable(null);
        }

        //内容
        if (mData.get(position).getType() == IPhotoEditType.BRUSH_NORMAL_COLOR) {
            viewHolder.mRec.setImageDrawable(getResources().getDrawable(mData.get(position).getColorRes()));

        } else if (mData.get(position).getType() == IPhotoEditType.BRUSH_LIGHT_COLOR) {
            viewHolder.mRec.setImageDrawable(getResources().getDrawable(R.drawable.ic_light_brush));

        } else if (mData.get(position).getType() == IPhotoEditType.BRUSH_BLOCK_MOSAICS) {
            viewHolder.mRec.setImageDrawable(getResources().getDrawable(R.drawable.ic_block_mosaics_light));

        } else if (mData.get(position).getType() == IPhotoEditType.BRUSH_MOSAICS) {
            viewHolder.mRec.setImageDrawable(getResources().getDrawable(R.drawable.ic_brush_mosaics));

        } else if (mData.get(position).getType() == IPhotoEditType.BRUSH_STICKERS) {
            viewHolder.mRec.setImageDrawable(getResources().getDrawable(R.drawable.ic_second_sticker1));
        } else if (mData.get(position).getType() == IPhotoEditType.BRUSH_BACKGROUND) {
            if (BitmapsManager.KEY_FIRST_BACKGROUND_BRUSH.equals(mData.get(position).getBitmapType())) {
                viewHolder.mRec.setImageDrawable(getResources().getDrawable(R.drawable.bg_first_background_brush));
            } else if (BitmapsManager.KEY_SECOND_BACKGROUND_BRUSH.equals(mData.get(position).getBitmapType())){
                viewHolder.mRec.setImageDrawable(getResources().getDrawable(R.drawable.bg_second_background_brush));
            }
        }

        viewHolder.mRec.setTag(R.id.item_position, position);
        viewHolder.mRec.setOnClickListener(this);
    }

    public void setColorSelected(int color) {
        boolean foundColor = false;
        if (mData != null) {
            for (ColorPickerBean bean : mData) {
                if (bean == null) {
                    return;
                }
                if (getResources().getColor(bean.getColorRes()) == color) {
                    bean.setClick(true);
                    foundColor = true;
                } else {
                    bean.setClick(false);
                    foundColor |= foundColor;
                }
            }

            if (!foundColor) {
                // 如果找不到颜色，就默认选第一个颜色
                mData.get(0).setClick(true);
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getTag(R.id.item_position) == null || !(v.getTag(R.id.item_position) instanceof Integer)) {
            return;
        }
        int position = (int) v.getTag(R.id.item_position);
        if (position >= mData.size()) {
            return;
        }
        for (ColorPickerBean bean : mData) {
            if (bean.getClick()) {
                bean.setClick(false);
            }
        }
        ColorPickerBean clickBean = mData.get(position);
        clickBean.setClick(true);
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onColorItemClick(position, clickBean);
        }
        notifyDataSetChanged();
    }

    private Resources getResources() {
        return BaseApplication.getInstance().getResources();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //正方形笔触选择器
        RoundedImageView mRec;

        private ViewHolder(View itemView) {
            super(itemView);
            this.mRec = itemView.findViewById(R.id.iv_round);
        }
    }
}
