package com.biotech.drawlessons.photoedit.layers;

import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import com.biotech.drawlessons.photoedit.draws.BaseBrush;
import com.biotech.drawlessons.photoedit.draws.BaseDrawData;
import com.biotech.drawlessons.photoedit.draws.LightLineBrush;
import com.biotech.drawlessons.photoedit.tools.DimensionManager;
import com.biotech.drawlessons.photoedit.utils.BitmapsManager;
import com.biotech.drawlessons.photoedit.utils.DrawInvoker;
import com.biotech.drawlessons.photoedit.utils.IPhotoEditType;
import com.biotech.drawlessons.photoedit.views.DrawingBoardView;

/**
 * Created by xintu on 2018/2/6.
 * 因为在开启硬件加速的情况下，paint 的 setMaskFilter 会失效，所以直接在屏幕上绘制的话，无法达到效果。
 * 所以新建这一层 layer，在绘制发光线条的时候，会直接在 internal bitmap 里面绘制，然后，再显示 internal bitmap
 * 就行了。
 */

public class LightLineBrushLayer extends BaseLayer{
    private LightLineBrush mCurrBrush;
    private float[] mv = new float[9];
    private float mTouchDownX, mTouchDownY, mTouchUpX, mTouchUpY;
    private DrawInvoker mInvoker;
    private Paint mPaint, mOuterPaint;
    private BlurMaskFilter filter;
    private BitmapsManager mBitmapManager;
    private DimensionManager mDimensionManager;

    public LightLineBrushLayer(ILayerParent parent, DrawInvoker invoker, Matrix matrix, BitmapsManager manager, DimensionManager dimensionManager) {
        super(parent, IPhotoEditType.LAYER_LIGHT_LINE_BRUSH, matrix);
        mInvoker = invoker;
        mBitmapManager = manager;
        mDimensionManager = dimensionManager;

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(dimensionManager.getStrokeWidth());
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);

        mOuterPaint = new Paint();
        mOuterPaint.setAntiAlias(true);
        mOuterPaint.setColor(0xFFFF00FF);
        mOuterPaint.setStyle(Paint.Style.STROKE);
        mOuterPaint.setStrokeWidth(10f);
        mOuterPaint.setStrokeJoin(Paint.Join.ROUND);
        mOuterPaint.setStrokeCap(Paint.Cap.ROUND);
        mOuterPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
//        mOuterPaint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.OUTER));

        filter = new BlurMaskFilter(10, BlurMaskFilter.Blur.OUTER);
        setIsNeedSecondTempBitmap(true);
    }

    @Override
    public void switchToThisLayer() {
        setIsDealDrawEvent(false);
        setIsDealTouchEvent(true);
        mBitmapManager.erasInternalBitmap();
        mBitmapManager.ensureSecondTempBitmap();
        mBitmapManager.erasSecondTempBitmap();
        // 把低于brush排序的笔刷绘制到 secondBitmap
        mInvoker.drawUnderOrderBrushOnSecondBitmap(IPhotoEditType.Order.BRUSH, true);
        // 把高于brush排序的笔刷绘制到 internalBitmap
        mInvoker.drawUpperOrderBrushOnInternalBitmap(IPhotoEditType.Order.BRUSH, false);
    }

    @Override
    public void switchToOtherLayer(BaseLayer otherLayerType) {
        setIsDealTouchEvent(false);
        setIsDealDrawEvent(false);
    }

    @Override
    public void updateClipRectStatus(boolean isClip, RectF clipRect) {
        super.updateClipRectStatus(isClip, clipRect);
    }

    @Override
    public boolean onTouchDown(float curXOnScreen, float curYOnScreen, float curXOnBitmap, float curYOnBitmap) {
        mTouchDownX = curXOnScreen;
        mTouchDownY = curYOnScreen;
        if (!mInvoker.isLightLineCountValidate()) {
            // todo::: toash
//            ToastModel.showRed(SnsApplication.getInstance(), R.string.material_count_invalidate);
            return false;
        }
        mCurrBrush = createLightLineBrush();
        mCurrBrush.onTouchDown(curXOnScreen, curYOnScreen, curXOnBitmap, curYOnBitmap);
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

        mBitmapManager.erasInternalBitmap();
        mInvoker.drawBrushOnInternalBitmap(mCurrBrush);
        // todo:这里其实是有问题的，因为把上层清空之后，如果上层还有别的笔刷，就应该把笔刷重新绘制出来，但是由于现在 Order.BRUSH 的优先级很高，没有比他优先级更高的笔刷，所以现在直接画是没有问题的。
        return true;
    }

    @Override
    public boolean onTouchUp(float curXOnScreen, float curYOnScreen, float curXOnBitmap, float curYOnBitmap) {
        mTouchUpX = curXOnScreen;
        mTouchUpY = curYOnScreen;
        return (mCurrBrush != null) && checkToAddPath(mCurrBrush);
    }

    public LightLineBrush createLightLineBrush(BaseDrawData data) {
        return LightLineBrush.createBrush(data, mMatrix, mPaint, mOuterPaint, filter);
    }

    private LightLineBrush createLightLineBrush() {
        return new LightLineBrush(mMatrix, mPaint, mOuterPaint, mDimensionManager.getStrokeWidth(), filter);
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
            // 清空 internalBitmap
            mBitmapManager.erasInternalBitmap();
            // 同时把刚刚绘制的笔刷绘制到 secondBitmap
            mInvoker.drawLastBrushOnSecondBitmap();
            mCurrBrush = null;
        }

        return res;
    }
}
