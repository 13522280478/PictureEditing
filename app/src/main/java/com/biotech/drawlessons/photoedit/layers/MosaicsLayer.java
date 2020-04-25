package com.biotech.drawlessons.photoedit.layers;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import com.biotech.drawlessons.photoedit.draws.BaseBrush;
import com.biotech.drawlessons.photoedit.draws.BaseDraw;
import com.biotech.drawlessons.photoedit.draws.BaseDrawData;
import com.biotech.drawlessons.photoedit.draws.BlockMosaicBrush;
import com.biotech.drawlessons.photoedit.draws.BrushMosaicsBrush;
import com.biotech.drawlessons.photoedit.tools.DimensionManager;
import com.biotech.drawlessons.photoedit.tools.IStrokeWidthChange;
import com.biotech.drawlessons.photoedit.utils.BitmapsManager;
import com.biotech.drawlessons.photoedit.utils.DrawInvoker;
import com.biotech.drawlessons.photoedit.utils.IPhotoEditType;
import com.biotech.drawlessons.photoedit.views.DrawingBoardView;


/**
 * Created by xintu on 2018/1/30.
 * 马赛克层，可以绘制方块类型的马赛克和刷子类型的马赛克。
 * 该层其实是对原图进行绘制，因为需求是希望马赛克是在最底层，所以该层的层级应该比 InternalTempLayer 还要靠后
 * 但是我们也不应该再新建一个 InternalTempLayer 来保存马赛克了，因为这样会很耗费性能。
 * 因为是对原图进行绘制，所以不需要实现 onDraw 方法，只需要在 touchMove 的时候，改变 bitmap ，同时通知 view
 * 更新就行了。
 */

public class MosaicsLayer extends BaseLayer {
    private DrawInvoker mInvoker;
    private String mBrushName;
    private int mMosaicsWH;
    private Paint mPaint;

    private BaseBrush mCurrBrush;
    private int mType;
    private float mTouchDownX, mTouchDownY, mTouchUpX, mTouchUpY;
    private BitmapsManager mBitmapManager;
    private DimensionManager mDimensionManger;

    public MosaicsLayer(ILayerParent parent, DrawInvoker invoker, Matrix matrix, BitmapsManager manager,
                        DimensionManager dimensionManager) {
        super(parent, IPhotoEditType.LAYER_MOSAICS, matrix);
        mDimensionManger = dimensionManager;
        mInvoker = invoker;
        mBitmapManager = manager;
        mMosaicsWH = (int) (40 / mDimensionManger.getCurScale());
        initPaint();
        setIsNeedSecondTempBitmap(true);
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mDimensionManger.getStrokeWidth());
        mPaint.setAntiAlias(true);
    }

    @Override
    public void switchToThisLayer() {
        setIsDealDrawEvent(false);
        setIsDealTouchEvent(true);

        mBitmapManager.erasInternalBitmap();
        mBitmapManager.ensureSecondTempBitmap();
        mBitmapManager.erasSecondTempBitmap();
        // 把低于mosaics排序的笔刷绘制到 secondBitmap
        mInvoker.drawUnderOrderBrushOnSecondBitmap(IPhotoEditType.Order.MOSAICS, true);
        // 把高于mosaics排序的笔刷绘制到 internalBitmap
        mInvoker.drawUpperOrderBrushOnInternalBitmap(IPhotoEditType.Order.MOSAICS, false);
    }

    @Override
    public void switchToOtherLayer(BaseLayer otherLayerType) {
        setIsDealDrawEvent(false);
        setIsDealTouchEvent(false);
    }

    public void setBlockMosaicsMode() {
        mType = IPhotoEditType.BRUSH_BLOCK_MOSAICS;
    }

    public void setBrushMosaicsMode(String brushName) {
        mPaint.setShader(null);

        mBrushName = brushName;
        mType = IPhotoEditType.BRUSH_MOSAICS;
    }

    @Override
    public boolean onTouchDown(float curXOnScreen, float curYOnScreen, float curXOnBitmap, float curYOnBitmap) {
        mTouchDownX = curXOnScreen;
        mTouchDownY = curYOnScreen;
        switch (mType) {
            case IPhotoEditType.BRUSH_BLOCK_MOSAICS:
                mCurrBrush = createBlockMosaicsBrush();
                mCurrBrush.onTouchDown(curXOnScreen, curYOnScreen, curXOnBitmap, curYOnBitmap);
                break;

            case IPhotoEditType.BRUSH_MOSAICS:
                mCurrBrush = createBrushMosaicsBrush();
                mCurrBrush.onTouchDown(curXOnScreen, curYOnScreen, curXOnBitmap, curYOnBitmap);
                break;
        }
        return true;
    }

    @Override
    public boolean onTouchMove(float curXOnScreen, float curYOnScreen, float lastXOnScreen, float lastYOnScreen,
                               float curXOnBitmap, float curYOnBitmap, float lastXOnBitmap, float lastYOnBitmap) {
        if (mCurrBrush == null) {
            return false;
        }

        mCurrBrush.onTouchMove(curXOnScreen, curYOnScreen, lastXOnScreen, lastYOnScreen,
                curXOnBitmap, curYOnBitmap, lastXOnBitmap, lastYOnBitmap);
        mInvoker.drawBrushOnSecondTempBitmap(mCurrBrush);
        return true;
    }

    @Override
    public boolean onTouchUp(float curXOnScreen, float curYOnScreen, float curXOnBitmap, float curYOnBitmap) {
        mTouchUpX = curXOnScreen;
        mTouchUpY = curYOnScreen;
        return (mCurrBrush != null) && checkToAddPath(mCurrBrush);
    }

    @Override
    public void updateClipRectStatus(boolean isClip, RectF clipRect) {
        super.updateClipRectStatus(isClip, clipRect);
    }

    /**
     * 通过 touch up - touch down 的距离来判断是点按还是移动
     *
     * @return 是否把当前的 draw 添加到队列中
     */
    private boolean checkToAddPath(BaseBrush baseBrush) {
        boolean res = false;
        if (Math.abs(mTouchDownY - mTouchUpY) > DrawingBoardView.VALIDATE_MOVE_RANGE
                || Math.abs(mTouchDownX - mTouchUpX) > DrawingBoardView.VALIDATE_MOVE_RANGE) {
            mInvoker.addBrushAndOrder(baseBrush);
            res = true;
            mCurrBrush.mapPathFromScreenToBitmap();
            mCurrBrush.setDrawOnWhere(BaseDraw.DrawOnWhere.DRAW_ON_SECOND_TEMP_BITMAP);
            mCurrBrush = null;
        }

        return res;
    }

    @Override
    public void updateMatrix(Matrix matrix) {
        mMatrix = matrix;
    }

    public BlockMosaicBrush createBlockMosaicsBrush(BaseDrawData data) {
        return BlockMosaicBrush.createBrush(data, mMatrix, mPaint, mMosaicsWH, mBitmapManager);
    }

    private BlockMosaicBrush createBlockMosaicsBrush() {
        return new BlockMosaicBrush(mMatrix, mPaint, mDimensionManger.getStrokeWidth(IStrokeWidthChange.STOKE_LEVEL_LARGE), mMosaicsWH, mBitmapManager);
    }

    public BrushMosaicsBrush createBrushMosaicsBrush(BaseDrawData data) {
        return new BrushMosaicsBrush(data, mMatrix, mPaint, mBitmapManager);
    }

    private BrushMosaicsBrush createBrushMosaicsBrush() {
        return new BrushMosaicsBrush(mMatrix, mPaint, mBrushName, mBitmapManager);
    }
}