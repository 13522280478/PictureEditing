package com.biotech.drawlessons.photoedit.draws;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;

import com.biotech.drawlessons.photoedit.utils.BitmapsManager;
import com.biotech.drawlessons.photoedit.utils.IPhotoEditType;


/**
 * Created by xintu on 2018/4/2.
 */

public class BackgroundBrush extends BaseBrush {
    private String mBackgroundUrl;
    private BitmapsManager mBitmapManager;
    private BitmapShader mShader;
    private Paint mPaint;

    public BackgroundBrush(Matrix matrix, String bitmapUrl,
                           BitmapsManager bitmapsManager, Paint paint, float strokeWidth) {
        super(IPhotoEditType.BRUSH_BACKGROUND, matrix, DrawOnWhere.DRAW_ON_SCREEN);
        mBackgroundUrl = bitmapUrl;
        mBitmapManager = bitmapsManager;
        mStroke = strokeWidth;
        Bitmap bitmap = mBitmapManager.getBitmap(mBackgroundUrl);
        mPaint = paint;
        if (bitmap != null) {
            mShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        }
        mPaint.setShader(mShader);
        mDrawData.strokeWidth = mStroke;
        mDrawData.bitmapUrl = bitmapUrl;
    }

    public static BackgroundBrush createBrush(BaseDrawData drawData, Matrix matrix, BitmapsManager bitmapsManager, Paint paint, float strokeWidth) {
        BackgroundBrush brush = new BackgroundBrush(matrix, drawData.bitmapUrl, bitmapsManager, paint, strokeWidth);
        brush.setDrawOnWhere(DrawOnWhere.DRAW_ON_INTERNAL_BITMAP);
        brush.mDrawData = new BaseDrawData(drawData.type);
        brush.mDrawData.copy(drawData);
        brush.mPath = brush.mDrawData.path;

        return brush;
    }

    @Override
    public void draw(Canvas canvas) {
        if (mShader == null) {
            return;
        }
        mPaint.setShader(mShader);
        if (mDrawOnWhere == DrawOnWhere.DRAW_ON_SCREEN) {
            mPaint.setStrokeWidth(mStroke * mRealScaleValue);
            mShader.setLocalMatrix(mMatrix);
        } else {
            mPaint.setStrokeWidth(mStroke);
            mShader.setLocalMatrix(null);
        }
        canvas.drawPath(mPath, mPaint);
    }
}
