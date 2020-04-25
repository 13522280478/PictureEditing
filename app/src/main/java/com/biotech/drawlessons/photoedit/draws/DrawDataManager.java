package com.biotech.drawlessons.photoedit.draws;

import android.content.Context;
import android.text.TextUtils;


import com.biotech.drawlessons.photoedit.utils.BitmapsManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by xintu on 2018/3/1.
 */

public class DrawDataManager {
    private HashMap<String, RestoreBean> mDatas = new HashMap<>();
    private HashMap<String, String> mUrlDatas = new HashMap<>();

    public static DrawDataManager getInstance() {
        return InnerClass.instance;
    }

    public void updateRestoreBean(Context context, String originalUri, RestoreBean restoreBean) {
        mDatas.put(originalUri, restoreBean);
        mUrlDatas.put(BitmapsManager.getSaveEditedUri(context, originalUri), originalUri);
    }

    public RestoreBean getRestoreBean(String bitmapUrl) {
        return mDatas.get(bitmapUrl);
    }

    public void clearRestoreBean() {
        mDatas.clear();
        mUrlDatas.clear();
    }

    public boolean isContainsOriginalUrl(String bitmapUrl) {
        return mDatas.containsKey(bitmapUrl);
    }

    public boolean isEditedUrl(String editedUrl) {
        return mUrlDatas.containsKey(editedUrl);
    }

    public String getOriginalUrl(String editedUrl) {
        return mUrlDatas.get(editedUrl);
    }

    public int size() {
        return mDatas.size();
    }

    private static class InnerClass {
        private static final DrawDataManager instance = new DrawDataManager();
    }
}
