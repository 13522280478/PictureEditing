package com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.viewholder;

import android.view.View;

import com.biotech.drawlessons.R;
import com.biotech.drawlessons.photoedit.draws.StickerData;
import com.biotech.drawlessons.photoedit.resourcepicker.MediaResourcePickerView;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.RecyclerViewAdapter;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.model.BaseData;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.model.Data;
import com.biotech.drawlessons.photoedit.resourcepicker.download.Downloadable;
import com.biotech.drawlessons.photoedit.resourcepicker.download.PhotoEditDownloadManager;


/***************************************************************************************************
 * 描述：相框类型 view holder
 *
 * 作者：champion
 *
 * 时间：18/2/11
 **************************************************************************************************/


public class StickerViewHolder extends ImagesViewHolder implements View.OnClickListener {

    private final static boolean DEBUG = true;

    private final static String TAG = MediaResourcePickerView.PREFIX_TAG + "StickerViewHolder";

    public StickerViewHolder(View itemView, RecyclerViewAdapter adapter) {
        super(itemView, adapter);
    }

    @Override
    protected void findViews() {
        super.findViews();
    }

    @Override
    public void refreshView(int position, Data data) {
        super.refreshView(position, data);
        if (data == null || !(data instanceof StickerData)) return;
        final StickerData tempData = (StickerData) data;
//        // 未下载
//        if (tempData.needDownload()) {
//             if (NetworkUtils.isConnect(SnsApplication.getInstance())
//                     && tempData.mDownloadRetryTime < BaseData.RETRY_DOWNLOAD_TIME) {
//                 tempData.mDownloadRetryTime++;
//                PhotoEditDownloadManager.getInstance().download((Downloadable) mData);
//             }
//        }
//        // 已下载
//        else if (tempData.getDownloadStatus() != Downloadable.DOWNLOADING){
//            tempData.mDownloadRetryTime = 0;
//            GlideUtils.setImageBmp(((StickerData) data).getLocalPath(), mImgResource);
//
//        }
    }

    @Override
    public void onClick(View v) {
        int resId = v.getId();
        switch (resId) {
            case R.id.img_item_resource:
//                if (!StickerData.checkTypeValid(mData)) return;
//                // 未下载
//                if (((StickerData) mData).needDownload()) {
//                    return;
//                }
//                // 已下载 或 下载中
//                else {
//                    int downloadStatus = ((StickerData) mData).getDownloadStatus();
//                    // 下载中
//                    if (downloadStatus == Downloadable.DOWNLOADING) {
//                        return;
//                    }
//                    // 已下载
//                    else {
//                        // 选中相关资源
//                        EventHelper2.getDefault().post(new ItemResourceClickEvent(mData));
//                    }
//                }
                break;
        }

    }
}
