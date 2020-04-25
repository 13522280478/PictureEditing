package com.biotech.drawlessons.photoedit.draws;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;


import com.biotech.drawlessons.photoedit.utils.BitmapsManager;
import com.biotech.drawlessons.photoedit.utils.IPhotoEditType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by xintu on 2017/12/10.
 * 默认这个笔刷只能直接绘制在图片上
 */

public class BrushMosaicsBrush extends BaseBrush {
    private Paint mPaint;
    private LinkedList<Info> mInfos;
    private Matrix mLocalMatrix;
    private int mBitmapWidth, mBitmapHeight;
    private String mBrushName;
    private Info mCurInfo;
    private BitmapsManager mManager;
    private boolean mNeedDraw;

    private class Info{
        public float x, y;
        public float angle;
        public int color;

        Info (float x, float y, float angle, int color) {
            this.x = x;
            this.y = y;
            this.angle = angle;
            this.color = color;
        }
    }

    public BrushMosaicsBrush(Matrix matrix, Paint paint, String brushName, BitmapsManager manager) {
        super(IPhotoEditType.BRUSH_MOSAICS, matrix, DrawOnWhere.DRAW_ON_INTERNAL_BITMAP);

        mPaint = paint;
        mBrushName = brushName;
        mInfos = new LinkedList<>();
        mLocalMatrix = new Matrix();
        mManager = manager;
        mBitmapWidth = mManager.getOriginalBitmap().getWidth();
        mBitmapHeight = mManager.getOriginalBitmap().getHeight();

        mDrawData.bitmapUrl = mBrushName;
        mDrawData.pointsWithColors = new LinkedList<>();
        mDrawData.path = mPath;
    }

    public BrushMosaicsBrush(BaseDrawData drawData, Matrix matrix, Paint paint, BitmapsManager manager) {
        this(matrix, paint, drawData.bitmapUrl, manager);

        BaseDrawData.PointWithColor firstPoint = drawData.pointsWithColors.pollFirst();
        float lastX = firstPoint.xOnBitmap, lastY = firstPoint.yOnBitmap;

        mInfos.add(new Info(lastX, lastY, 0, firstPoint.color));
        Iterator<BaseDrawData.PointWithColor> iterator = drawData.pointsWithColors.iterator();

        while (iterator.hasNext()) {
            BaseDrawData.PointWithColor pt = iterator.next();
            float curX = pt.xOnBitmap, curY = pt.yOnBitmap;
            float degrees = (float) Math.toDegrees(Math.atan2(pt.yOnBitmap - lastY, pt.xOnBitmap - lastX));
            mInfos.add(new Info(curX, curY, degrees, pt.color));
        }
        mDrawData.copy(drawData);
    }

    @Override
    public void onTouchDown(float curXOnScreen, float curYOnScreen, float curXOnBitmap, float curYOnBitmap) {
        super.onTouchDown(curXOnScreen, curYOnScreen, curXOnBitmap, curYOnBitmap);

        if (curXOnBitmap > 0 && curYOnBitmap > 0 && curXOnBitmap < mBitmapWidth
                && curYOnBitmap < mBitmapHeight) {
            int pixel = mManager.getOriginalBitmap().getPixel((int) curXOnBitmap, (int) curYOnBitmap);
            mDrawData.pointsWithColors.add(new BaseDrawData.PointWithColor(curXOnBitmap, curYOnBitmap, pixel));
        }
    }

    @Override
    public void onTouchMove(float curXOnScreen, float curYOnScreen, float lastXOnScreen, float lastYOnScreen,
                            float curXOnBitmap, float curYOnBitmap, float lastXOnBitmap, float lastYOnBitmap) {
        if ((Math.abs(curXOnBitmap - lastXOnBitmap) > 4 || Math.abs(curYOnBitmap - lastYOnBitmap) > 4)
                && curXOnBitmap > 0 && curYOnBitmap > 0 && curXOnBitmap < mBitmapWidth
                && curYOnBitmap < mBitmapHeight) {
            float degrees = (float) Math.toDegrees(Math.atan2(lastYOnBitmap - curYOnBitmap, lastXOnBitmap - curXOnBitmap));
            int pixel = mManager.getOriginalBitmap().getPixel((int) curXOnBitmap, (int) curYOnBitmap);
            mCurInfo = new Info(curXOnBitmap, curYOnBitmap, degrees, pixel);
            mInfos.add(mCurInfo);

            mDrawData.pointsWithColors.add(new BaseDrawData.PointWithColor(curXOnBitmap, curYOnBitmap, pixel));
            mNeedDraw = true;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (!isTouchUpped()) {
            // 因为这个笔刷是直接在图片上绘制，如果手指一直在滑动，之前绘制的东西不会被清屏
            // 所以只要绘制最新的 info 就好了，没有必要一次把之前的 info 又遍历一次
            if (mNeedDraw) {
                drawBrushByInfo(canvas, mCurInfo);
                mNeedDraw = false;
            }
        } else {
            drawListBrush(canvas);
        }

    }

    /**
     * 为啥又写了一个方法，主要是for循环会取很多次brushBitmap，其实取一次就OK
     * */
    private void drawListBrush(Canvas canvas){
        Bitmap brushBitmap = mManager.getBitmap(mBrushName);
        Iterator<Info> iterator = mInfos.iterator();
        while (iterator.hasNext()) {
            Info info = iterator.next();
            mPaint.setColorFilter(new PorterDuffColorFilter(info.color, PorterDuff.Mode.SRC_ATOP));
            mLocalMatrix.setTranslate(info.x - brushBitmap.getWidth()/2, info.y - brushBitmap.getHeight()/2);
            mLocalMatrix.postRotate(info.angle, info.x, info.y);
            mLocalMatrix.postScale(2f/mRealScaleValue, 2f/mRealScaleValue, info.x, info.y);
            canvas.drawBitmap(brushBitmap, mLocalMatrix, mPaint);
        }
    }

    private void drawBrushByInfo(Canvas canvas,Info info) {
        mPaint.setColorFilter(new PorterDuffColorFilter(info.color, PorterDuff.Mode.SRC_ATOP));
        Bitmap brushBitmap = mManager.getBitmap(mBrushName);
        mLocalMatrix.setTranslate(info.x - brushBitmap.getWidth()/2, info.y - brushBitmap.getHeight()/2);
        mLocalMatrix.postRotate(info.angle, info.x, info.y);
        mLocalMatrix.postScale(2f/mRealScaleValue, 2f/mRealScaleValue, info.x, info.y);
        canvas.drawBitmap(brushBitmap, mLocalMatrix, mPaint);
    }
}