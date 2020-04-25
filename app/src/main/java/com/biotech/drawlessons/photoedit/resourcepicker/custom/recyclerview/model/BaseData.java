package com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.model;


import com.biotech.drawlessons.photoedit.resourcepicker.download.Downloadable;

/**
 * Created by xintu on 2018/3/15.
 */

public class BaseData  {
    public int mDownloadRetryTime;
    public static final int RETRY_DOWNLOAD_TIME = 5;
    public String getDownloadUrl() {
        return null;
    }

    public String getDownloadLocalUrl() {
        return null;
    }

    public boolean needDownload() {
        return false;
    }

    public int getDownloadStatus() {
        return 0;
    }

    public void setDownloadStatus(int downloadStauts) {

    }

    public String getLocalPath() {
        return null;
    }

    public int getViewItemType() {
        return 0;
    }

    public void setPosition(int position) {

    }

    public int getPosition() {
        return 0;
    }
}
