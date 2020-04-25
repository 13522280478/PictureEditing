package com.biotech.drawlessons.photoedit.resourcepicker.download;

/***************************************************************************************************
 * 描述：可被下载的
 *
 * 作者：champion
 *
 * 时间：18/2/12
 **************************************************************************************************/


public interface Downloadable {

    String getDownloadUrl();

    String getDownloadLocalUrl();

    boolean needDownload();

    int getDownloadStatus();

    void setDownloadStatus(int downloadStauts);

    // 下载前
    int DOWNLOAD_BEFORE = 1;
    // 取消下载
    int DOWNLOAD_CANCEL = 2;
    // 下载失败
    int DOWNLOAD_FAILURE = 3;
    // 下载成功
    int DOWNLOAD_SUCCESS = 4;
    // 下载中
    int DOWNLOADING = 5;


    class DownloadWrapper implements Downloadable {
        @Override
        public String getDownloadUrl() {
            return null;
        }

        @Override
        public String getDownloadLocalUrl() {
            return null;
        }

        @Override
        public boolean needDownload() {
            return false;
        }

        @Override
        public int getDownloadStatus() {
            return 0;
        }

        @Override
        public void setDownloadStatus(int downloadStauts) {

        }
    }
}
