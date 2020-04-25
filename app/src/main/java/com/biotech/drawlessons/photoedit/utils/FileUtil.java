package com.biotech.drawlessons.photoedit.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.SparseArray;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.RoundingMode;
import java.text.ChoiceFormat;
import java.text.DecimalFormat;
import java.util.UUID;

/**
 * <p>
 * 功能 文件操作工具类
 * </p>
 * <p/>
 * <p>Copyright sohu.com 2016 All right reserved.</p>
 *
 * @author song 时间 16/8/30
 * @version 1.0
 *          <p>
 *          最后修改人 无
 */
public class FileUtil {
    private static final int FILE_SIZE_B = 1024;
    private static final int FILE_SIZE_KB = FILE_SIZE_B * 1024;
    private static final long FILE_SIZE_MB = FILE_SIZE_KB * 1024;
    private static final long FILE_SIZE_GB = FILE_SIZE_MB * 1024;

    private static final String PATH_ROOT = "/sns/chat";//根目录
    private static final String PATH_IMG = "/img";//图片目录
    private static final String PATH_IMG_COMPRESS = PATH_IMG + "/compress";//图片压缩临时目录
    private static final String PATH_IMG_UPLOAD = PATH_IMG + "/upload";//拍照上传目录
    private static final String PATH_IMG_DOWNLOAD = PATH_IMG + "/download";//查看原图保存目录
    private static final String PATH_SELF_IMG_DOWNLOAD = PATH_IMG + "/selfdownload";//查看原图保存目录


    private static final String PATH_VIDEO = "/video";//视频目录
    private static final String PATH_VOICE = "/voice";//语音目录
    private static final String PATH_FILE = "/file";//文件目录
    private static final String EXTENSION_NAME = ".sohu.cache";//文件格式后缀

    private static final String PATH_STICKER = "/sticker";//查看原图保存目录
    private static final String PATH_EDIT_PHOTO = "/edit_photo";
    private static final String PATH_FILTERS = "/filters";


    /*文件块描述*/
    public static class FileBlock {
        public long start;//起始字节位置
        public long end;//结束字节位置
        public int index;//文件分块索引
    }

    private FileUtil() {

    }



    /**
     * 获取系统相册路径
     *
     * @return 系统相册路径
     */
    public static String getDCIMPath(Context context) {

        String path = StoragePathProxy.getPackageFileDCIMDirectory(context);
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * 获取系统相册路径
     *
     * @return 系统相册路径
     */
    public static String getStickerDCIMPath(Context context) {

        String path = StoragePathProxy.getPackageFileDCIMDirectory(context)+PATH_STICKER;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * result: /storage/emulated/0/Android/data/com.sohu.newsclient/files/com.sohu.kan/DCIM/edit_photo
     * */
    public static String getEditPhotoDCIMPath(Context context) {
        String path = StoragePathProxy.getPackageFileDCIMDirectory(context) + PATH_EDIT_PHOTO;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * result: /storage/emulated/0/Android/data/com.sohu.newsclient/files/com.sohu.kan/DCIM/filters
     * */
    public static String getFilterPreviewPhotoPath(Context context) {
        String path = StoragePathProxy.getPackageFileDCIMDirectory(context) + PATH_FILTERS;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }


}
