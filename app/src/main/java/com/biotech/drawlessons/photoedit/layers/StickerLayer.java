package com.biotech.drawlessons.photoedit.layers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.view.ViewConfiguration;

import com.biotech.drawlessons.photoedit.draws.BaseSticker;
import com.biotech.drawlessons.photoedit.draws.BitmapSticker;
import com.biotech.drawlessons.photoedit.draws.StickerData;
import com.biotech.drawlessons.photoedit.draws.TextSticker;
import com.biotech.drawlessons.photoedit.tools.DimensionManager;
import com.biotech.drawlessons.photoedit.utils.BitmapsManager;
import com.biotech.drawlessons.photoedit.utils.DrawInvoker;
import com.biotech.drawlessons.photoedit.utils.IPhotoEditType;


/**
 * Created by xintu on 2018/1/29.
 */

public class StickerLayer extends BaseLayer {
    private Context mContext;
    private DrawInvoker mInvoker;
    private BaseSticker mCurSticker;
    private PaintFlagsDrawFilter mPaintFilter;
    private float mTouchDownX, mTouchDownY, mTouchUpX, mTouchUpY;
    private RectF mDeletableRect;
    private Paint mPaint;
    private int mViewWidth, mViewHeight;
    private BitmapsManager mBitmapManager;
    private DimensionManager mDimensionManager;
    private boolean mLongPressed;
    private int mLongPressTimeOut;
    private CheckForLongPress mCheckForLongPress;
    private final static int CANCEL_CLICK_DISTANCE = 20;
    private boolean mDeletableState;
    private StickerStateListener mListener;
    public interface StickerStateListener {
        void onStickerClick(BaseSticker sticker);
        void onStickerPressStateChange(BaseSticker sticker, boolean pressed);
        void onStickerDeleteStateChange(BaseSticker sticker, boolean isDeletable);
    }

    public StickerLayer(ILayerParent parent, Context context, DrawInvoker invoker, Matrix matrix,
                        int viewWidth, int viewHeight, BitmapsManager manager, DimensionManager dimensionManager) {
        super(parent, IPhotoEditType.LAYER_STICKER, matrix);
        mContext = context;
        mInvoker = invoker;
        mBitmapManager = manager;
        mPaintFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mDimensionManager = dimensionManager;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        onSizeChange(viewWidth, viewHeight, 0, 0);
        mViewWidth = viewWidth;
        mViewHeight = viewHeight;
        try {
            mLongPressTimeOut = ViewConfiguration.getLongPressTimeout();
        } catch (Exception e) {
            mLongPressTimeOut = 500;
        }
    }

    @Override
    public void onSizeChange(int w, int h, int oldw, int oldh) {
        mViewWidth = w;
        mViewHeight = h;

        if (mDeletableRect == null) {
            mDeletableRect = new RectF();
        }
    }

    @Override
    public void switchToThisLayer() {
        setIsDealDrawEvent(true);
        setIsDealTouchEvent(true);
    }

    @Override
    public void switchToOtherLayer(BaseLayer otherLayerType) {
        if (otherLayerType.getLayerType() == IPhotoEditType.LAYER_IDL) {
            setIsDealDrawEvent(true);
            setIsDealTouchEvent(true);
        } else {
            setIsDealDrawEvent(true);
            setIsDealTouchEvent(false);
        }
    }

    @Override
    public void updateMatrix(Matrix matrix) {
        mMatrix = matrix;
    }

    public void setStickerClickListener(StickerStateListener listener){
        mListener = listener;
    }

    public void addBitmapSticker(String stickerUri) {
        float[] ptsOnBitmap = new float[2];
        float[] ptsOnScreen = new float[]{mViewWidth/2, mViewHeight/2};
        mDimensionManager.mapPointsFromInvertMatrix(ptsOnBitmap, ptsOnScreen);
        mInvoker.addSticker(new BitmapSticker(IPhotoEditType.STICKER_BITMAP, mBitmapManager, mMatrix, null, stickerUri, ptsOnBitmap[0], ptsOnBitmap[1], mDimensionManager));
    }

    public BaseSticker createBitmapSticker(StickerData data) {
        float[] ptsOnBitmap = new float[2];
        float[] ptsOnScreen = new float[]{mViewWidth/2, mViewHeight/2};
        mDimensionManager.mapPointsFromInvertMatrix(ptsOnBitmap, ptsOnScreen);
        return new BitmapSticker(IPhotoEditType.STICKER_BITMAP, mBitmapManager, mMatrix, data.getStickerMatrix(), data.bitmapUrl, ptsOnBitmap[0], ptsOnBitmap[1], mDimensionManager);
    }

    public void addTextSticker(String stickerText, String bitmapUri, int color) {
        float[] ptsOnBitmap = new float[2];
        float[] ptsOnScreen = new float[]{mViewWidth/2, mViewHeight/2};
        mDimensionManager.mapPointsFromInvertMatrix(ptsOnBitmap, ptsOnScreen);
        mInvoker.addSticker(new TextSticker(mBitmapManager, mMatrix, null, bitmapUri, stickerText, color,
                ptsOnBitmap[0], ptsOnBitmap[1], mDimensionManager));
    }

    public TextSticker createTextSticker(StickerData data) {
        float[] ptsOnBitmap = new float[2];
        float[] ptsOnScreen = new float[]{mViewWidth/2, mViewHeight/2};
        mDimensionManager.mapPointsFromInvertMatrix(ptsOnBitmap, ptsOnScreen);
        return new TextSticker(mBitmapManager, mMatrix, data.stickerMatrix, data.bitmapUrl, data.text, data.color, ptsOnBitmap[0], ptsOnBitmap[1], mDimensionManager);
    }

    @Override
    public boolean onScale(float scaleFactor, float focusX, float focusY) {
        if (mCurSticker != null) {
            // 不为空，说明已经 touch 状态
            mCurSticker.onScale(scaleFactor, focusX, focusY);
            return true;
        }
        return false;
    }

    @Override
    public boolean onRotate(float angle) {
        if (mCurSticker != null) {
            mCurSticker.onRotate(angle);
            return true;
        }

        return false;
    }

    @Override
    public boolean onTouchDown(float curXOnScreen, float curYOnScreen, float curXOnBitmap, float curYOnBitmap) {
        mLongPressed = false;
        mCurSticker = mInvoker.getTouchedSticker(curXOnScreen, curYOnScreen);
        mTouchDownX = curXOnScreen;
        mTouchDownY = curYOnScreen;
        if (mCurSticker != null) {
            if (mCheckForLongPress == null) {
                mCheckForLongPress = new CheckForLongPress();
            }
            mParent.getHandler().postDelayed(mCheckForLongPress, mLongPressTimeOut);
            mInvoker.setStickerTouched(mCurSticker);
            mInvoker.orderStickers();
            if (mListener != null) {
                mListener.onStickerPressStateChange(mCurSticker, true);
                mDeletableState = mCurSticker.getCenterOnScreen()[1] > mDimensionManager.getInitViewRect().height();
                mListener.onStickerDeleteStateChange(mCurSticker, mDeletableState);
            }
            return true;
        }
        return true;
    }

    @Override
    public boolean onTouchMove(float curXOnScreen, float curYOnScreen, float lastXOnScreen, float lastYOnScreen,
                               float curXOnBitmap, float curYOnBitmap, float lastXOnBitmap, float lastYOnBitmap) {
        if (mCurSticker != null) {

            mCurSticker.onTouchMove(curXOnScreen, curYOnScreen, lastXOnScreen, lastYOnScreen,
                    curXOnBitmap, curYOnBitmap, lastXOnBitmap, lastYOnBitmap);
            boolean oldState = mDeletableState;
            mDeletableState = mCurSticker.getCenterOnScreen()[1] > mDimensionManager.getInitViewRect().height();
            if (mListener != null && oldState != mDeletableState) {
                mListener.onStickerDeleteStateChange(mCurSticker, mDeletableState);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchUp(float curXOnScreen, float curYOnScreen, float curXOnBitmap, float curYOnBitmap) {
        mTouchUpX = curXOnScreen;
        mTouchUpY = curYOnScreen;
        if (mCurSticker == null) {
            return false;
        }

        mParent.getHandler().removeCallbacks(mCheckForLongPress);
        if (mListener != null) {
            if (!mLongPressed && Math.abs(mTouchUpX - mTouchDownX) < CANCEL_CLICK_DISTANCE
                    && Math.abs(mTouchUpY - mTouchDownY) < CANCEL_CLICK_DISTANCE) {
                mListener.onStickerClick(mCurSticker);
            }
            mListener.onStickerPressStateChange(mCurSticker, false);
        }

        if (mDeletableState) {
            mInvoker.deleteSticker(mCurSticker);
            mDeletableState = false;
        }
        mCurSticker = null;
        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        mInvoker.drawAllStickerOnScreen(canvas);
    }

    private class CheckForLongPress implements Runnable {
        @Override
        public void run() {
            mLongPressed = true;
        }
    }
}
