package com.biotech.drawlessons.photoedit.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Shader;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by xintu on 2018/2/5.
 * 因为很多 brush 和 sticker 都会用到图片，但是新建 brush 和 sticker 的时候，里面的 bitmap 全部是新建的话，
 * 是完全没有必要的，不同的图片在内存中存在一份就够了。
 * 因为这个原因新建了这么一个 manager。
 * 之所以创建成一个单例，是因为产品希望最多能够同时编辑四张图片，所以我们在进入 PhotoWallActivity 时注册这个
 * manager，然后在 PhotoWallActivity finish 的时候注销这个类。
 */

public class BitmapsManager {
    public static final String KEY_BRUSH_MOSAICS = "key_brush_mosaics";
    public static final String KEY_STICKER_LIST = "key_sticker_list";
    // 这里写的比较着急，就这么先写了，之后可以再重构
    public static final String KEY_FIRST_BACKGROUND_BRUSH = "key_first_background_brush";
    public static final String KEY_SECOND_BACKGROUND_BRUSH = "key_second_background_brush";
    // 用 hashMap 来存图片，可以防止保存多张uri相同，但是对象指针不同的图片
    private HashMap<String, Bitmap> mBitmapMap;
    private HashMap<String, ArrayList<Bitmap>> mStickerListMap;
    private Bitmap mInternalBitmap, mSecondTempBitmap, mPhotoFrameBitmap, mOriginalBitmap;
    private BitmapShader mMosaicsShader;
    private Canvas mInternalCanvas, mSecondTempCanvas;
    private String mPhotoFrameUri;
    private String mOriginalUri;
    private int mInternalWidth, mInternalHeight;

    public BitmapsManager() {
        init();
    }

    public BitmapsManager(String bitmapUri, Bitmap originalBitmap) {
        mOriginalUri = bitmapUri;
        mOriginalBitmap = originalBitmap;
        init();
    }

    public static String generateTextStickerUri(String stickerText, int color) {
        return "bitmapManager_text_" + stickerText + color;
    }

    //
    public static String getSaveEditedUri(Context context, String bitmapUri) {
        // 保存当前的图片到本地路径，供外面的photoWall使用
        String editedBitmapName = StoragePathProxy.getEditedBitmapName(bitmapUri);
        String packageFileName = FileUtil.getEditPhotoDCIMPath(context);
        return packageFileName + "/" + editedBitmapName;
    }

    public static String getFilterUri(Context context, String filterType) {
        String packageFileName = FileUtil.getFilterPreviewPhotoPath(context);
        return packageFileName + "/" + filterType + ".png";
    }

    private void init() {
        mBitmapMap = new HashMap<String, Bitmap>();
    }

    // original的图片只保存一份
    public void replaceOriginalBitmap(String uri, Bitmap bitmap) {
        if (mOriginalBitmap != null) {
            if (!mOriginalBitmap.isRecycled()) {
                mOriginalBitmap.recycle();
            }
            removeBitmap(mOriginalUri);
        }
        mOriginalBitmap = bitmap;
        mOriginalUri = uri;
    }

    public Bitmap getOriginalBitmap() {
        return mOriginalBitmap;
    }

    public void saveBitmap(String uri, Bitmap bitmap) {
        mBitmapMap.put(uri, bitmap);
    }

    public Bitmap getBitmap(String uri) {
        return mBitmapMap.get(uri);
    }

    public void removeBitmap(String uri) {
        Bitmap bitmap = mBitmapMap.get(uri);
        if (bitmap != null) {
            mBitmapMap.remove(uri);
            bitmap.recycle();
        }
    }

    public String getOriginalSmallPicUri() {
        return "small_pic_" + mOriginalUri;
    }

    public Bitmap getOriginalSmallPic() {
        return getBitmap(getOriginalSmallPicUri());
    }

    public void ensureSecondTempBitmap() {
        if (mSecondTempBitmap == null) {
            mSecondTempBitmap = createSecondTempBitmap();
        }
    }

    public void removeAllBitmap() {
        if (mBitmapMap != null) {
            mBitmapMap.clear();
        }

        if (mStickerListMap != null) {
            mStickerListMap.clear();
        }

        if (mInternalBitmap != null) {
            mInternalBitmap.recycle();
            mInternalBitmap = null;
        }
        if (mSecondTempBitmap != null) {
            mSecondTempBitmap.recycle();
            mSecondTempBitmap = null;
        }
        if (mOriginalBitmap != null) {
            mOriginalBitmap.recycle();
            mOriginalBitmap = null;
        }
        if (mPhotoFrameBitmap != null) {
            mPhotoFrameBitmap.recycle();
            mPhotoFrameBitmap = null;
        }
        mMosaicsShader = null;
        mInternalCanvas = null;
        mSecondTempCanvas = null;
    }

    public Bitmap getPhotoFrameBitmap(String uri) {
        // 已经有一张photoFrame的图片了
        if (mPhotoFrameBitmap != null) {
            // 这个时候判断一下存在的这张图片和请求的这张图片是否是同一张
            if (mBitmapMap.containsKey(uri)) {
                // 如果是，只需要拿到这张图即可
                return mPhotoFrameBitmap;
            }
            // 如果请求的这张图片不在map中，说明需要拿到另一张图
            else {
                //这个时候，先把old photoFrame 从 map 中删除
                mBitmapMap.remove(mPhotoFrameUri);
                createAndSavePhotoFrameBitmap(uri);
                return mPhotoFrameBitmap;
            }
        } else {
            createAndSavePhotoFrameBitmap(uri);
            return mPhotoFrameBitmap;
        }
    }

    private void createAndSavePhotoFrameBitmap(String uri) {
        // 同时根据路径获取新的photoFrame
        mPhotoFrameBitmap = BitmapUtils.createMaxSizeBitmapFromUri(uri, mOriginalBitmap.getWidth(), mOriginalBitmap.getHeight());
        mPhotoFrameUri = uri;
        if (mPhotoFrameBitmap == null) {
            return;
        }
        // 把新的bitmap（包括新键）添加进map中
        mBitmapMap.put(mPhotoFrameUri, mPhotoFrameBitmap);
    }

    /**
     * 把 secondTemp 的图片内容，绘制在 internal 图片的下面
     */
    public void drawSecondTempBitmapUnderInternalBitmap() {
        if (mInternalBitmap == null || mSecondTempBitmap == null) return;
        // 1.先把 internal 的内容绘制到 secondTemp 上
        mSecondTempCanvas.drawBitmap(mInternalBitmap, 0, 0, null);

        // 2.把 internal 的内容清空
        erasInternalBitmap();

        // 3.把 secondTemp 的内容重新绘制到 mInternal 上
        mInternalCanvas.drawBitmap(mSecondTempBitmap, 0, 0, null);
    }

    public void releaseSecondTempBitmap() {
        if (mSecondTempBitmap != null) {
            mSecondTempBitmap.recycle();
            mSecondTempBitmap = null;

            mSecondTempCanvas = null;
        }
    }

    public void releaseMosaicShader() {
        mMosaicsShader = null;
    }

    private Bitmap createSecondTempBitmap() {
        mSecondTempBitmap = createTempBitmap(mSecondTempBitmap, mOriginalBitmap.getWidth(), mOriginalBitmap.getHeight());
        // 更新 canvas
        mSecondTempCanvas = new Canvas(mSecondTempBitmap);
        return mSecondTempBitmap;
    }

    public Bitmap createInternalBitmap() {
        if (mOriginalBitmap == null) {
            return null;
        }
        mInternalBitmap = createTempBitmap(mInternalBitmap, mOriginalBitmap.getWidth(), mOriginalBitmap.getHeight());
        mInternalCanvas = new Canvas(mInternalBitmap);
        return mInternalBitmap;
    }

    // 创建中间层的图片
    private Bitmap createTempBitmap(Bitmap bitmap, int width, int height) {
        // 这个时候没有缓存的中间层图片
        if (bitmap == null) {
            mInternalWidth = width;
            mInternalHeight = height;
            // 新建图片
            bitmap = createTransparentBitmap(width, height);
            // 保存到队列中
            saveBitmap(getInternalBitmapUri(width, height), bitmap);
        }
        // 这个时候队列中是有中间层的图片的，但是有可能的情况是中间层的图片宽高是和所需要的图片宽高不一样
        else {
            // 如果需要的图片和缓存的图片是一样的宽高
            if (width == mInternalWidth && height == mInternalHeight) {
                if (bitmap.isRecycled()) {
                    bitmap = createTransparentBitmap(width, height);
                }
                bitmap.eraseColor(Color.TRANSPARENT);
            } else {
                // 如果缓存的图片宽高和所需要的对应不上
                // 先把缓存的图片删除
                mBitmapMap.remove(getInternalBitmapUri(mInternalWidth, mInternalHeight));
                // 新建一张空白的图片
                bitmap = createTransparentBitmap(width, height);
                // 同时把图片缓存进队列
                mInternalWidth = width;
                mInternalHeight = height;
                mBitmapMap.put(getInternalBitmapUri(mInternalWidth, mInternalHeight), bitmap);
            }
        }
        return bitmap;
    }

    public Bitmap getInternalBitmap() {
        if (mInternalBitmap == null) {
            mInternalBitmap = createInternalBitmap();
        }
        return mInternalBitmap;
    }

    public Bitmap getSecondTempBitmap() {
        if (mSecondTempBitmap == null) {
            mSecondTempBitmap = createSecondTempBitmap();
            // 更新 canvas
            mSecondTempCanvas = new Canvas(mSecondTempBitmap);
        }
        return mSecondTempBitmap;
    }

    public Canvas getInternalCanvas() {
        if (mInternalCanvas == null) {
            createInternalBitmap();
        }
        return mInternalCanvas;
    }

    public Canvas getSecondTempCanvas() {
        if (mSecondTempCanvas == null) {
            createSecondTempBitmap();
        }
        return mSecondTempCanvas;
    }

    public void deleteInternalBitmap() {
        if (mInternalBitmap != null) {
            mBitmapMap.remove(getInternalBitmapUri(mInternalWidth, mInternalHeight));
            mInternalBitmap.recycle();
            mInternalBitmap = null;
        }
    }

    public void erasInternalBitmap() {
        erasBitmap(mInternalBitmap, Color.TRANSPARENT);
    }

    public void erasSecondTempBitmap() {
        erasBitmap(mSecondTempBitmap, Color.TRANSPARENT);
    }

    public void erasBitmap(Bitmap bitmap, int color) {
        if (bitmap != null) {
            bitmap.eraseColor(color);
        }
    }

    public void saveStickerList(String uri, ArrayList<Bitmap> bitmaps) {
        if (mStickerListMap == null) {
            mStickerListMap = new HashMap<>();
        }
        mStickerListMap.put(uri, bitmaps);
    }

    public ArrayList<Bitmap> getStickerList(String uri) {
        return mStickerListMap.get(uri);
    }

    private Bitmap createTransparentBitmap(int width, int height) {
        Bitmap bitmap;
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.TRANSPARENT);
        return bitmap;
    }

    private String getInternalBitmapUri(int width, int height) {
        return "internal_x_" + width + "_y_" + height;
    }

    public BitmapShader getMosaicsShader(int mosaicsWH) {
        if (mMosaicsShader == null) {
            mMosaicsShader = new BitmapShader(createMosaicsBitmap(mosaicsWH), Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        }
        return mMosaicsShader;
    }

    public Bitmap createMosaicsBitmap(int mosaicWH) {
        return createMosaicsBitmap(mOriginalBitmap, mosaicWH);
    }

    public Bitmap createMosaicsBitmap(Bitmap src, int mosaicWH) {
        Bitmap mosaics = src.copy(src.getConfig(), true);
        int[] pixels = new int[src.getWidth() * src.getHeight()];
        src.getPixels(pixels, 0, src.getWidth(), 0, 0, src.getWidth(), src.getHeight());
        setMosaicsPixels(pixels, mosaics, mosaicWH);
        return mosaics;
    }

    private void setMosaicsPixels(int[] originPixels, Bitmap dst, int mosaicsWH) {
        if (!dst.isMutable()) {
            throw new RuntimeException("can't modify unMutable bitmap");
        }
        int w = dst.getWidth();
        int h = dst.getHeight();

        int[] dstPixels = new int[w * h];
        // 遍历像素点，更改mosaics方块的颜色，以左顶点的颜色为准
        for (int i = 0; i < originPixels.length; i++) {
            int pxLine = i / w;
            int pxColumn = i % w;
            int mscLine = pxLine / mosaicsWH;
            int mscColumn = pxColumn / mosaicsWH;
            // 自己画个图来看看就明白了...
            /** 画图来了：
             *  width = 10; height = 10
             *      0    1    2    3   4    5   6    7    8   9
             *  0   ●   ●   ●   ●   ●   ●   ●   ●   ●   ●
             *
             *  1   ●   ●   ●   ●   ●   ●   ●   ●   ●   ●
             *
             *  2   ●   ●   ●   ●   ●   ●   ●   ●   ●   ●
             *                             -----------
             *  3   ●   ●   ●   ●   ●   ● | *   ●   ● | ●
             *
             *  4   ●   ●   ●   ●   ●   ● | ●   ●   ● | ●
             *
             *  5   ●   ●   ●   ●   ●   ● | ●   ●   ● | ●
             *                             -----------
             *  6   ●   ●   ●   ●   ●   ●   ●   ●   ●   ●
             *
             *  7   ●   ●   ●   ●   ●   ●   ●   ●   ●   ●
             *
             *  8   ●   ●   ●   ●   ●   ●   ●   ●   ●   ●
             *
             *  9   ●   ●   ●   ●   ●   ●   ●   ●   ●   ●
             *
             *  假如我取 mscWH = 3，对于（6,3）这个点（图上打*的点），我们得到的 mscLine = 1, mscColumn = 2
             *  现在我们由 mscLine = 1, mscColumn = 2 反推出 * 所处的位置:
             *  用 mscLine * mscWH * width = 1 * 3 * 10 = 30, 31 ~ 40 是 column3 所处的位置，说明这个范围是对的
             *  再加上 Y的偏移量：mscColumn * mscWH = 2 * 3 = 6。
             *  算出来的结果是36，正是图上*所处的位置。 而对于37、38、46、47、48、56、57、58 这些点，
             *  计算之后都等于36，正是我们需要的效果。
             *  觉得是巧合的话，可以自己再找几个点验证一下
             * */
            dstPixels[i] = originPixels[mscLine * w * mosaicsWH + mscColumn * mosaicsWH];

        }
        dst.setPixels(dstPixels, 0, w, 0, 0, w, h);
    }

}