package com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.viewholder;

import android.view.View;
import android.widget.ImageView;

import com.biotech.drawlessons.R;
import com.biotech.drawlessons.photoedit.resourcepicker.MediaResourcePickerView;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.event.ItemResourceClickEvent;
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


public class PhotoFrameViewHolder extends ImagesViewHolder implements View.OnClickListener {

    private final static boolean DEBUG = true;

    private final static String TAG = MediaResourcePickerView.PREFIX_TAG + "PhotoFrameViewHolder";

    // 右下角下载图标
    private ImageView mImgDownload;
    // 资源下载中的蒙版
    private ImageView mImgResourceMask;

    public PhotoFrameViewHolder(View itemView, RecyclerViewAdapter adapter) {
        super(itemView, adapter);
    }

    @Override
    protected void findViews() {
        super.findViews();
        mImgDownload = itemView.findViewById(R.id.img_resource_download);
        mImgResourceMask = itemView.findViewById(R.id.img_item_resource_mask);
    }

    @Override
    public void refreshView(int position, Data data) {
        super.refreshView(position, data);
//        if (data == null || !(data instanceof PhotoFrameData)) {
//            return;
//        }
//        PhotoFrameData tempData = (PhotoFrameData) data;
//        //todo:::
////        GlideUtils.setImageBmp(tempData.thumb_img_url, mImgResource);
//        // 未下载
//        if (((PhotoFrameData) data).needDownload()) {
//            mImgDownload.setVisibility(View.VISIBLE);
//            mImgResourceMask.setVisibility(View.GONE);
//        }
//        // 已下载 或 下载中
//        else {
//            mImgDownload.setVisibility(View.GONE);
//            int downloadStatus = ((PhotoFrameData) data).getDownloadStatus();
//            // 下载中
//            if (downloadStatus == Downloadable.DOWNLOADING) {
//                mImgResourceMask.setVisibility(View.VISIBLE);
//            }
//            // 已下载
//            else {
//                mImgResourceMask.setVisibility(View.GONE);
//            }
//        }
    }

    @Override
    public void onClick(View v) {
        int resId = v.getId();
        switch (resId) {
            case R.id.img_item_resource:
//                if (!PhotoFrameData.checkTypeValid(mData)) {
//                    return;
//                }
//                // todo:::
////                // 未下载
////                if (((PhotoFrameData) mData).needDownload()) {
////                    if (NetworkUtils.isConnect(SnsApplication.getInstance())
////                            && ((PhotoFrameData) mData).mDownloadRetryTime < BaseData.RETRY_DOWNLOAD_TIME) {
////                        ((PhotoFrameData) mData).mDownloadRetryTime++;
////                        PhotoEditDownloadManager.getInstance().download((Downloadable) mData);
////
////                        mImgDownload.setVisibility(View.VISIBLE);
////                        mImgResourceMask.setVisibility(View.GONE);
////                    } else {
//////                        ToastModel.showRed(SnsApplication.getInstance(), R.string.no_network_error);
////                    }
////                }
//                // 已下载 或 下载中
//                else {
//                    mImgDownload.setVisibility(View.GONE);
//                    int downloadStatus = ((PhotoFrameData) mData).getDownloadStatus();
//                    // 下载中
//                    if (downloadStatus == Downloadable.DOWNLOADING) {
//                        return;
//                    }
//                    // 已下载
//                    else {
//                        // 选中相关资源
//                        // todo:::
////                        EventHelper2.getDefault().post(new ItemResourceClickEvent(mData));
//                        ((PhotoFrameData) mData).mDownloadRetryTime = 0;
//                    }
//                }
                break;
        }

    }
}
