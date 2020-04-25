package com.biotech.drawlessons.photoedit.draws;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;


import com.biotech.drawlessons.photoedit.utils.BitmapsManager;
import com.biotech.drawlessons.photoedit.utils.IPhotoEditType;

import java.util.LinkedList;

/**
 * Created by xintu on 2017/12/4.
 * 这个类型的brush就是贴纸的笔刷。首先我们会有一个队列的图片，当我们移动的时候，达到一定距离的时候，会绘制一个
 * 贴纸，继续移动，开始绘制另外的贴纸。
 */

public class StickerBrush extends BaseBrush {
    private String mStickersUri;
    private Paint mPaint;
    private PathMeasure mPathMeasure;
    private float pos[] = new float[2], tan[] = new float[2];
    private int mBitmapWidth, mBitmapHeight;
    private Matrix mBitmapMatrix;
    private int mBitmapCount;
    private BitmapsManager mManager;
    private float disToDrawNext;
    private float mDefaultDisToDraw;
    private float mStickerScale;
    private float mPathDis;

    public StickerBrush(Matrix matrix, String stickerUri, Paint paint, BitmapsManager manager) {
        super(IPhotoEditType.BRUSH_STICKERS, matrix, DrawOnWhere.DRAW_ON_SCREEN);
        mStickersUri = stickerUri;
        mManager = manager;
        mBitmapCount = mManager.getStickerList(mStickersUri).size();
        mPaint = paint;

        mBitmapMatrix = new Matrix();
        mPathMeasure = new PathMeasure(mPath, false);

        mDefaultDisToDraw = 150f / mRealScaleValue;

        disToDrawNext = mDefaultDisToDraw;
        mStickerScale = 1f;

        mDrawData.bitmapUrl = mStickersUri;
    }

    public static StickerBrush createStickerBrush(BaseDrawData drawData, Matrix matrix, Paint paint,
                                                  BitmapsManager manager) {
        StickerBrush brush = new StickerBrush(matrix, drawData.bitmapUrl, paint, manager);
        brush.setDrawOnWhere(DrawOnWhere.DRAW_ON_INTERNAL_BITMAP);
        brush.mDrawData = new BaseDrawData(drawData.type);
        brush.mDrawData.copy(drawData);
        brush.mPath = brush.mDrawData.path;

        return brush;
    }

    @Override
    public void onTouchMove(float curXOnScreen, float curYOnScreen, float lastXOnScreen,
                            float lastYOnScreen, float curXOnBitmap, float curYOnBitmap,
                            float lastXOnBitmap, float lastYOnBitmap) {
        super.onTouchMove(curXOnScreen, curYOnScreen, lastXOnScreen, lastYOnScreen,
                curXOnBitmap, curYOnBitmap, lastXOnBitmap, lastYOnBitmap);
    }

    @Override
    public void draw(Canvas canvas) {
        mPathMeasure.setPath(mPath, false);
        float pathLength = mPathMeasure.getLength();
        int pathIndex = 0;
        int bitmapIndex;
        if (mDrawOnWhere == DrawOnWhere.DRAW_ON_SCREEN) {
            disToDrawNext = mDefaultDisToDraw * mRealScaleValue;
            mStickerScale = mRealScaleValue;
        } else if (mDrawOnWhere == DrawOnWhere.DRAW_ON_INTERNAL_BITMAP){
            disToDrawNext = mDefaultDisToDraw;
            mStickerScale = 1f;
        }
        // 这里不要从0开始，如果从0开始，计算第一个点的旋转角度就不对了
        for (float i = 1f; i < pathLength; i += disToDrawNext) {
            mPathMeasure.getPosTan(i, pos, tan);
            float degrees = (float) (Math.atan2(tan[1], tan[0]) * 180.0 / Math.PI);
            canvas.save();
            bitmapIndex = pathIndex % mBitmapCount;
            mBitmapWidth = mManager.getStickerList(mStickersUri).get(bitmapIndex).getWidth();
            mBitmapHeight = mManager.getStickerList(mStickersUri).get(bitmapIndex).getHeight();

            mBitmapMatrix.setTranslate(pos[0] - mBitmapWidth/2, pos[1] - mBitmapHeight/2);
            mBitmapMatrix.postRotate(degrees, pos[0], pos[1]);
            mBitmapMatrix.postScale(mStickerScale, mStickerScale, pos[0], pos[1]);
            canvas.drawBitmap(mManager.getStickerList(mStickersUri).get(bitmapIndex),
                    mBitmapMatrix, null);
            canvas.restore();
            pathIndex++;
        }
    }
}