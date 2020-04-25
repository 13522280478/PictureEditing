package com.biotech.drawlessons.photoedit.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;


import java.io.File;

/**
 * Created by xuncao on 2017/4/1.
 */

public class StoragePathProxy {


    public static String DIRECTORY_MUSIC = "Music";
    public static String DIRECTORY_PODCASTS = "Podcasts";
    public static String DIRECTORY_RINGTONES = "Ringtones";
    public static String DIRECTORY_ALARMS = "Alarms";
    public static String DIRECTORY_NOTIFICATIONS = "Notifications";
    public static String DIRECTORY_PICTURES = "Pictures";
    public static String DIRECTORY_MOVIES = "Movies";
    public static String DIRECTORY_DOWNLOADS = "Download";
    public static String DIRECTORY_DCIM = "DCIM";
    public static String DIRECTORY_DOCUMENTS = "Documents";
    public static String DIRECTORY_SCREEN_SHOTS = "Pictures/Screenshots";
    public final static String EDITED_EXT = "_edited";

    private StoragePathProxy() {}

    /**
     * @param type
     *
     * example: type = "folder"
     * result: /storage/emulated/0/Android/data/com.sohu.newsclient/files/com.sohu.kan/folder
     *
     * example: type = null 或 type = ""
     * result: /storage/emulated/0/Android/data/com.sohu.newsclient/files/com.sohu.kan
     *
     * 如果不能获取上面的结果，会返回
     * /data/user/0/com.sohu.newsclient/app_steamer/com.sohu.kan/files
     * */
    public static String getPackageFileDirectory(Context context,String type) {
        return getPackageFileDirectoryFile(context,type).toString();
    }

    public static File getPackageFileDirectoryFile(Context context, String type) {
        File file = null;
        try {
            if (TextUtils.isEmpty(type)) {
                file = context.getExternalFilesDir("com.sohu.kan");
            } else {
                file = context.getExternalFilesDir("com.sohu.kan/" + type);
            }
        }catch (Exception e){
            // rom bug http://en.miui.com/thread-296766-1-3.html
        }

        if (file == null) {
            file = context.getFilesDir();
        }
        return file;
    }

    /**
     * result: /storage/emulated/0/Android/data/com.sohu.newsclient/files/com.sohu.kan/DCIM
     *
     * */
    public static String getPackageFileDCIMDirectory(Context context) {
        String path = getPackageFileDirectory(context, DIRECTORY_DCIM);
        return path;
    }

    /**
     * result: /storage/emulated/0/Android/data/com.sohu.newsclient/files/com.sohu.kan/Music
     *
     * */
    public static String getPackageFileMusicDirectory(Context context) {
        String path = getPackageFileDirectory(context, DIRECTORY_MUSIC);
        return path;
    }

    /**
     * 警告：慎用此方法
     *
     * 使用前必须检查是否已经获取READ_EXTERNAL_STORAGE权限
     *
     * 未获得权限严禁使用
     * */
    public static long getAvailableStorageSize() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            long availableStorage = availableBlocks * blockSize;
            return availableStorage;
        }
        return -1L;
    }

    public static String getEditedBitmapName(String bitmapPath) {
        // 保存当前的图片到本地路径，供外面的photoWall使用
        String[] uriArray = bitmapPath.split("/");
        String bitmapName = uriArray[uriArray.length - 1];
        StringBuilder builder = new StringBuilder(bitmapName);
        int lastIndex = builder.lastIndexOf(".");
        builder.insert(lastIndex, EDITED_EXT);

        return builder.toString();
    }
}
