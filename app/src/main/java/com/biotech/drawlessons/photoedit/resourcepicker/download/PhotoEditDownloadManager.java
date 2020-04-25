package com.biotech.drawlessons.photoedit.resourcepicker.download;

import android.text.TextUtils;


import com.biotech.drawlessons.photoedit.resourcepicker.MediaResourcePickerView;

import java.io.File;
import java.util.HashMap;

/***************************************************************************************************
 * 描述：
 *
 * 作者：champion
 *
 * 时间：18/2/12
 **************************************************************************************************/


public class PhotoEditDownloadManager {

    private final static boolean DEBUG = true;

    private final static String TAG = MediaResourcePickerView.PREFIX_TAG + "PhotoEditDownloadManager";

    private final static String PHOTO_EDIT_RESOURCE_PATH = "photo_edit";

    // 资源下载到本地的存储路径
    private String mLocalDownloadDirPath;

    private PhotoEditDownloadManager() {
    }

    public static PhotoEditDownloadManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final PhotoEditDownloadManager INSTANCE = new PhotoEditDownloadManager();
    }

//    public void download(final FileRequestListener httpCallback, String downloadUrl, String path) {
//        HashMap<String, String> params = new HashMap<String, String>();
//        Runnable uploadRunnable = ChatRunnableFactory.createPhotoEditDownloadRunnable(downloadUrl,
//                path, path, params, RequestParams.getHeaders(), new FileRequestListener() {
//                    @Override
//                    public void onSuccess(FileParams fileParams, String response) {
//
//                    }
//
//                    @Override
//                    public void onFailure(FileParams fileParams, long intercept, Exception e) {
//
//                    }
//
//                    @Override
//                    public void onLoad(FileParams fileParams, long total, long seek, boolean isUp) {
//
//                    }
//                });
//        ChatThreadManager.getInstance().submit(ChatConstants.TAG_THREAD_DOWNLOAD, uploadRunnable);
//    }
//
//    public void download(final Downloadable downloadable) {
//        HashMap<String, String> params = new HashMap<String, String>();
//        Runnable uploadRunnable = ChatRunnableFactory.createPhotoEditDownloadRunnable(
//                downloadable.getDownloadUrl(),
//                downloadable.getDownloadLocalUrl(),
//                downloadable.getDownloadLocalUrl(),
//                params, RequestParams.getHeaders(), new FileRequestListener() {
//                    @Override
//                    public void onSuccess(FileParams fileParams, String response) {
//                        downloadable.setDownloadStatus(Downloadable.DOWNLOAD_SUCCESS);
//                        EventHelper2.getDefault().post(new DownloadStatusChangeEvent(downloadable));
//                    }
//
//                    @Override
//                    public void onFailure(FileParams fileParams, long intercept, Exception e) {
//                        downloadable.setDownloadStatus(Downloadable.DOWNLOAD_FAILURE);
//                        EventHelper2.getDefault().post(new DownloadStatusChangeEvent(downloadable));
//                    }
//
//                    @Override
//                    public void onLoad(FileParams fileParams, long total, long seek, boolean isUp) {
//                        downloadable.setDownloadStatus(Downloadable.DOWNLOADING);
//                        EventHelper2.getDefault().post(new DownloadStatusChangeEvent(downloadable));
//                    }
//                });
//        ChatThreadManager.getInstance().submit(ChatConstants.TAG_THREAD_DOWNLOAD, uploadRunnable);
//    }
//
//
//    public String getDownloadLocalPath() {
//        if (TextUtils.isEmpty(mLocalDownloadDirPath)) {
//
//            // String filePath = "/data/data/com.example.photoeditor/" + PHOTO_EDIT_RESOURCE_PATH;
//
//            String filePath = StoragePathProxy.getPackageFileDirectory(null) + PHOTO_EDIT_RESOURCE_PATH;
//            File file = new File(filePath);
//            if (!file.exists()) {
//                file.mkdirs();
//            }
//            mLocalDownloadDirPath = filePath;
//        }
//        return mLocalDownloadDirPath;
//    }
//
//    /**
//     * 获得单个资源的下载到本地的名称
//     *
//     * @param key
//     *
//     * @return
//     */
//    public String getSingleDownloadName(String key) {
//        return getDownloadLocalPath() + "/" + MD5Builder.getMD5(key) + ".png";
//    }

}
