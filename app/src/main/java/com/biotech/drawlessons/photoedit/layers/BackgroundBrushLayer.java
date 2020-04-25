package com.biotech.drawlessons.photoedit.layers;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import com.biotech.drawlessons.photoedit.draws.BackgroundBrush;
import com.biotech.drawlessons.photoedit.draws.BaseBrush;
import com.biotech.drawlessons.photoedit.draws.BaseDrawData;
import com.biotech.drawlessons.photoedit.tools.DimensionManager;
import com.biotech.drawlessons.photoedit.tools.IStrokeWidthChange;
import com.biotech.drawlessons.photoedit.utils.BitmapsManager;
import com.biotech.drawlessons.photoedit.utils.DrawInvoker;
import com.biotech.drawlessons.photoedit.utils.IPhotoEditType;
import com.biotech.drawlessons.photoedit.views.DrawingBoardView;

import java.util.logging.Level;

/**
 * Created by xintu on 2018/4/2.
 */

public class BackgroundBrushLayer extends BaseLayer {
    private DrawInvoker mInvoker;
    private BaseBrush mCurrBrush;
    private int mType;
    private String mBackgroundUrl;
    private float mTouchDownX, mTouchDownY, mTouchUpX, mTouchUpY;
    private Paint mPaint;
    private BitmapsManager mBitmapManager;
    private DimensionManager mDimensionManger;
    private RectF mClipRectOnScreen;

    public BackgroundBrushLayer(ILayerParent parent, Matrix matrix, DrawInvoker invoker,
                                BitmapsManager manager, DimensionManager dimensionManager) {
        super(parent, IPhotoEditType.LAYER_BACKGROUND_BRUSH, matrix);
        mInvoker = invoker;
        mBitmapManager = manager;
        mDimensionManger = dimensionManager;

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mDimensionManger.getStrokeWidth());
        mPaint.setAntiAlias(true);

        mClipRectOnScreen = new RectF();
    }

    /**
     * 当处于裁剪模式的时候，我们绘制的图片在屏幕上显示的时候，不应该超过裁剪的框，也就是说被裁剪的部分显示黑色，
     * 所以我们需要设置 mClipRectOnScreen 的大小
     * */
    @Override
    public void updateClipRectStatus(boolean isClip, RectF clipRect) {
        super.updateClipRectStatus(isClip, clipRect);
        if (isClip) {
            mDimensionManger.mapRectFromMatrix(mClipRectOnScreen, clipRect);
        }
    }

    @Override
    public void switchToThisLayer() {
        super.switchToThisLayer();
        mBitmapManager.erasInternalBitmap();
        mBitmapManager.ensureSecondTempBitmap();
        mBitmapManager.erasSecondTempBitmap();
        // 把低于brush排序的笔刷绘制到 secondTempBitmap
        mInvoker.drawUnderOrderBrushOnSecondBitmap(IPhotoEditType.Order.BRUSH, true);
        // 把高于brush排序的笔刷绘制到 internalBitmap
        mInvoker.drawUpperOrderBrushOnInternalBitmap(IPhotoEditType.Order.BRUSH,false);
    }

    /**
     * 每次 onDraw 的时候，就把透明的 bitmap 绘制在屏幕上，同时如果 mCurrBrush 不为空，会把当前绘制的东西
     * 画在屏幕上
     * */
    @Override
    public void onDraw(Canvas canvas) {
        if (mCurrBrush == null) {
            return;
        }
        canvas.save();
        if (mNeedClip) {
            canvas.clipRect(mClipRectOnScreen);
        } else {
            canvas.clipRect(mDimensionManger.getCurDrawableRect());
        }

        mInvoker.drawBrush(canvas, mCurrBrush);
        canvas.restore();
    }

    /**
     * 每次 touchDown 的时候，需要根据不同的 type 生成对应的画笔，同时初始化 path
     * */
    @Override
    public boolean onTouchDown(float curXOnScreen, float curYOnScreen, float curXOnBitmap, float curYOnBitmap) {
        mTouchDownX = curXOnScreen;
        mTouchDownY = curYOnScreen;
        switch (mType) {
            case IPhotoEditType.BRUSH_BACKGROUND:
                mCurrBrush = createBackgroundBrush();
                mCurrBrush.onTouchDown(curXOnScreen, curYOnScreen, curXOnBitmap, curYOnBitmap);
                break;
        }
        return true;
    }

    /**
     * 每次 move 的时候，移动 path ，同时 invalidate 脏区域，通过 onDraw 方法，在屏幕上绘制出路径
     * */

    @Override
    public boolean onTouchMove(float curXOnScreen, float curYOnScreen, float lastXOnScreen, float lastYOnScreen,
                               float curXOnBitmap, float curYOnBitmap, float lastXOnBitmap, float lastYOnBitmap) {
        if (mCurrBrush == null) {
            return false;
        }
        mCurrBrush.onTouchMove(curXOnScreen, curYOnScreen, lastXOnScreen, lastYOnScreen,
                curXOnBitmap, curYOnBitmap, lastXOnBitmap, lastYOnBitmap);
        return true;
    }

    /**
     * up 的时候，检查画笔是否能够添加进入队列，如果可以添加进队列，就把刚刚绘制的东西画入透明的 bitmap 上，
     * 同时调用 onDraw 方法，清理屏幕上的画笔
     * */
    @Override
    public boolean onTouchUp(float curXOnScreen, float curYOnScreen, float curXOnBitmap, float curYOnBitmap) {
        mTouchUpX = curXOnScreen;
        mTouchUpY = curYOnScreen;
        return (mCurrBrush != null) && checkToAddPath(mCurrBrush);
    }

    /**
     * 通过 touch up - touch down 的距离来判断是点按还是移动
     * @return 是否把当前的 draw 添加到队列中
     * */
    private boolean checkToAddPath(BaseBrush baseBrush) {
        boolean res = false;
        if (Math.abs(mTouchDownY - mTouchUpY) > DrawingBoardView.VALIDATE_MOVE_RANGE
                || Math.abs(mTouchDownX - mTouchUpX) > DrawingBoardView.VALIDATE_MOVE_RANGE) {
            mInvoker.addBrushAndOrder(baseBrush);
            res = true;
            mCurrBrush.mapPathFromScreenToBitmap();
            mInvoker.drawBrushOnInternalBitmap(mCurrBrush);
            mCurrBrush = null;
        }
        return res;
    }

    public void setBackgroundBrushMode(String backgroundBrushUrl) {
        mType = IPhotoEditType.BRUSH_BACKGROUND;
        mBackgroundUrl = backgroundBrushUrl;
    }

    public BackgroundBrush createBackgroundBrush() {
        return new BackgroundBrush(mMatrix, mBackgroundUrl, mBitmapManager, mPaint, mDimensionManger.getStrokeWidth(IStrokeWidthChange.STOKE_LEVEL_LARGE));
    }

    public BackgroundBrush createBackgroundBrush(BaseDrawData data) {
        return BackgroundBrush.createBrush(data, mMatrix, mBitmapManager, mPaint, mDimensionManger.getStrokeWidth(IStrokeWidthChange.STOKE_LEVEL_LARGE));
    }
}
