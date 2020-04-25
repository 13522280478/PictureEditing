package com.biotech.drawlessons.photoedit.resourcepicker.custom.event;


import com.biotech.drawlessons.photoedit.resourcepicker.download.Downloadable;

/***************************************************************************************************
 * 描述：资源下载状态变化
 *
 * 作者：champion
 *
 * 时间：18/2/13
 **************************************************************************************************/


public class DownloadStatusChangeEvent {

    public Downloadable mDownloadable;

    public DownloadStatusChangeEvent(Downloadable downloadable) {
        this.mDownloadable = downloadable;
    }
}
