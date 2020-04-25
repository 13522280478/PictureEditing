package com.biotech.drawlessons.photoedit.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;


import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by xintu on 2017/12/11.
 */

public class BitmapUtils {

    public static Bitmap createBitmapFromEditText(EditText editText) {
        int width = editText.getWidth();
        int height = editText.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        editText.draw(canvas);
        return bitmap;
    }

    public static Bitmap createBitmapFromString(Context context, String text, int color) {
        EditText editText = new EditText(context);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
        editText.setIncludeFontPadding(false);
        editText.setText(text);
        editText.setCursorVisible(false);
        editText.setBackgroundDrawable(null);
        editText.setTextColor(color);
        editText.setGravity(Gravity.CENTER);
        editText.setMaxWidth(context.getResources().getDisplayMetrics().widthPixels);
        editText.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editText.layout(0, 0, editText.getMeasuredWidth(), editText.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(editText.getMeasuredWidth(), editText.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        editText.draw(canvas);
        return bitmap;
    }

    public static Bitmap createMaxSizeBitmapFromUri(String uri, int maxW, int maxH) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        int rotateAngle = getPicRotate(uri);
        BitmapFactory.decodeFile(uri, options);
        int sampleSize = 1;
        int srcWidth;
        int srcHeight;
        if (rotateAngle == 90 || rotateAngle == 270) {
            srcWidth = options.outHeight;
            srcHeight = options.outWidth;
        } else {
            srcWidth = options.outWidth;
            srcHeight = options.outHeight;
        }

        Bitmap res;
        if (srcWidth > maxW || srcHeight > maxH) {
            // 先进行一次简单的缩小，然后再解析图片，如果不这么做，当传入一张超大图的时候，我们会直接把超大图
            // 读取到内存中，虽然当方法结束的时候，内存会被释放，但是突然传入的超大图也会造成内存抖动，可能会引起OOM
            // 这里为什么是 w * 2 和 h * 2。原因一：sampleSize只接收 2 次方的数，所以每一次缩小必定是上一次的二分之一
            // 原因二：如果上一缩放后的宽高小于2，比如说1.1，我们直接缩小1/2，会导致图片被压缩。
            while (srcWidth / sampleSize > maxW * 2 || srcHeight / sampleSize > maxH * 2) {
                sampleSize = sampleSize << 1;
            }
            // 真正的读取图片
            options.inSampleSize = sampleSize;
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(uri, options);

            int scaledW;
            int scaledH;

            scaledW = options.outWidth;
            scaledH = options.outHeight;

            // 但是这里读取出的图片宽高依然不对，我们需要再一次计算
            float scaleX;
            float scaleY;
            if (rotateAngle == 90 || rotateAngle == 270) {
                scaleX = ((float) maxH) / scaledW;
                scaleY = ((float) maxW) / scaledH;
            } else {
                scaleX = ((float) maxW) / scaledW;
                scaleY = ((float) maxH) / scaledH;
            }
            float scale = scaleX < scaleY ? scaleX : scaleY;

            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale, maxW / 2, maxH / 2);
            matrix.postRotate(rotateAngle, maxW / 2, maxH / 2);
            res = Bitmap.createBitmap(bitmap, 0, 0, scaledW, scaledH, matrix, false);
            bitmap.recycle();
        } else {
            options.inJustDecodeBounds = false;
            res = BitmapFactory.decodeFile(uri, options);
        }
        return res;
    }

    /**
     * 读取图片文件旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片旋转的角度
     */
    public static int getPicRotate(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 根据传入的图片和限定的大小，重新生成新的图片，目前不支持scaleType，默认都是center
     */
    public static Bitmap createFixSizeBitmap(Bitmap bitmap, int newW, int newH) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        float scaleX = (float) newW / w;
        float scaleY = (float) newH / h;
        float scale = scaleX < scaleY ? scaleX : scaleY;
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale, w / 2, h / 2);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, false);
    }

    /**
     * 将图片存入Sdcard中指定路径
     *
     * @param bitmap
     * @param path
     * @return
     */
    public static boolean saveBitmapToSdcard(Bitmap bitmap, int quality, String path) {
        if (bitmap == null || TextUtils.isEmpty(path)) {
            return false;
        }
        File file = null;
        FileOutputStream fos = null;
        try {
            file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            //
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            fos.flush();
            return true;
        } catch (Exception e) {
            //
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (Exception e) {
                }
            }
        }
        return false;
    }

    /**
     * 图片裁剪方法，这里默认图片已经经过旋转处理
     */
    public static Bitmap decodeRegionCrop(Bitmap bitmap, RectF rect, int rotation) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
//        RectF tempRect = new RectF(rect);
//        if (rotation != 0) {
//            matrix = new Matrix();
//            matrix.setRotate(rotation, rect.centerX(), rect.centerY());
//            matrix.mapRect(tempRect, rect);
//        }
        Rect dstRect = convertRectFToRect(rect);
        if (width < dstRect.width() || height < dstRect.height()) {
            return null;
        }

        Bitmap croppedImage = null;
        ByteArrayOutputStream stream = null;
        try {
            stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(byteArray, 0, byteArray.length, false);

            try {
                croppedImage = decoder.decodeRegion(dstRect, new BitmapFactory.Options());
                if (rotation != 0) {
                    Matrix matrix = new Matrix();
                    matrix.setRotate(rotation);
                    croppedImage = Bitmap.createBitmap(croppedImage, 0, 0,
                            croppedImage.getWidth(), croppedImage.getHeight(), matrix, true);
                }
            } catch (Exception e) {
                // Rethrow with some extra information
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();

        } finally {
            closeSilently(stream);
        }
        return croppedImage;
    }

    private static Rect convertRectFToRect(RectF rectF) {
        return new Rect((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
    }

    public static void closeSilently(@Nullable Closeable c) {
        if (c == null) {
            return;
        }
        try {
            c.close();
        } catch (Throwable t) {
            // Do nothing
        }
    }
}