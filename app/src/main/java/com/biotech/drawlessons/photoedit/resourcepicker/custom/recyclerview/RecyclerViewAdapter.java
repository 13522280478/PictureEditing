package com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.biotech.drawlessons.R;
import com.biotech.drawlessons.photoedit.resourcepicker.MediaResourcePickerView;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.event.DownloadStatusChangeEvent;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.model.Data;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.viewholder.BaseViewHolder;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.viewholder.CategoryLineViewHolder;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.viewholder.ImageFilterViewHolder;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.viewholder.ImagesViewHolder;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.viewholder.PhotoFrameViewHolder;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.viewholder.StickerViewHolder;

import java.util.List;


/***************************************************************************************************
 * 描述：
 *
 * 作者：champion
 *
 * 时间：18/2/6
 **************************************************************************************************/


public class RecyclerViewAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final static boolean DEBUG = true;

    private final static String TAG = MediaResourcePickerView.PREFIX_TAG + "RecyclerViewAdapter";

    private List<? extends Data> mDatas;

    private LayoutInflater mInflater;

    private Context mContext;

    public RecyclerViewAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);

//        EventHelper2.getDefault().register(this);
    }

    public void setDatas(List<? extends Data> datas) {
        this.mDatas = datas;
        notifyDataSetChanged();
    }


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case Data.FOUR_IMAGE_TYPE:
                view = mInflater.inflate(R.layout.item_photo_editor_image, parent, false);
                return new ImagesViewHolder(view, this);
            case Data.IMAGE_FILTER_TYPE:
                view = mInflater.inflate(R.layout.item_photo_editor_image, parent, false);
                return new ImageFilterViewHolder(view, this);
            case Data.PHOTO_FRAME_TYPE:
                view = mInflater.inflate(R.layout.item_photo_editor_image, parent, false);
                return new PhotoFrameViewHolder(view, this);
            case Data.CATEGORY_LINE:
                view = mInflater.inflate(R.layout.item_photo_editor_category_line, parent, false);
                return new CategoryLineViewHolder(view, this);
            case Data.STICKER_TYPE:
                view = mInflater.inflate(R.layout.item_photo_editor_image, parent, false);
                return new StickerViewHolder(view, this);
            default:
                break;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.refreshView(position, mDatas.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        if (mDatas == null || mDatas.size() == 0) {
            return 0;
        }
        Data data = mDatas.get(position);
        return data.getViewItemType();
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }


    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
//        EventHelper2.getDefault().unregister(this);
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadStatusChanged(DownloadStatusChangeEvent event) {
        if (event == null
                || event.mDownloadable == null
                || !(event.mDownloadable instanceof Data)) {
            return;
        }
        if (((Data) event.mDownloadable).getViewItemType() == getItemViewType(((Data) event.mDownloadable).getPosition())) {
            notifyItemChanged(((Data) event.mDownloadable).getPosition());
        }
    }

    public void unRegister() {
//        EventHelper2.getDefault().unregister(this);
    }

}
