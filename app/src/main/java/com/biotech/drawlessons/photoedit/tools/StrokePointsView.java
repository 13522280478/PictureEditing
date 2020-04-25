package com.biotech.drawlessons.photoedit.tools;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.biotech.drawlessons.R;
import com.biotech.drawlessons.UtilsKt;


/**
 * Created by xintu on 2018/2/26.
 */

public class StrokePointsView extends View {
    private final static int SMALL_POINT_RADIUS = (int) UtilsKt.dp2px(3);
    private final static int SMALL_POINT_CLICK_RING_SIZE = (int) UtilsKt.dp2px(12);
    private final static int MIDDLE_POINT_RADIUS = (int) UtilsKt.dp2px(5);
    private final static int MIDDLE_POINT_CLICK_RING_SIZE = (int) UtilsKt.dp2px(16);
    private final static int LARGE_POINT_RADIUS = (int) UtilsKt.dp2px(7);
    private final static int LARGE_POINT_CLICK_RING_SIZE = (int) UtilsKt.dp2px(20);
    private final static int VIEW_BACKGROUND_RADIUS = (int) UtilsKt.dp2px(19);
    private final static int CANCEL_CLICK_DISTANCE = 10;
    private Context mContext;
    private int mPointsWidth, mPointsHeight;
    private Paint mPointsPaint, mRingPaint, mBgPaint;
    private RectF mPointsLayoutRect, mSmallPtClickRect, mMidPtClickRect, mLargePtClickRect;
    private int mBackgroundColor, mRingColor;
    private PointState mSmallPoint, mMidPoint, mLargePoint;
    private int mClickRingStroke;
    private IStrokeWidthChange mStrokeChangeListener;
    private boolean mLongPressed;
    private CheckForLongPress mCheckForLongPress;
    private float mTouchDownX, mTouchDownY;
    private int mLargeRectOff, mMidRectOff, mSmallRectOff;

    public StrokePointsView(Context context) {
        this(context, null);
    }

    public StrokePointsView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StrokePointsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mPointsLayoutRect = new RectF();
        mSmallPtClickRect = new RectF();
        mMidPtClickRect = new RectF();
        mLargePtClickRect = new RectF();

        mBackgroundColor = mContext.getResources().getColor(R.color.Blk_1_alpha_50);
        mClickRingStroke = (int) UtilsKt.dp2px(1);
        mRingColor = mContext.getResources().getColor(R.color.Ylw_1);
        initPaint();
        initPoints();

        mLargeRectOff = (int) UtilsKt.dp2px( 6);
        mMidRectOff = (int) UtilsKt.dp2px(  10);
        mSmallRectOff = (int) UtilsKt.dp2px(  14);
        mPointsWidth = (int) mContext.getResources().getDimension(R.dimen.stroke_width_util_width);
        mPointsHeight = (int) mContext.getResources().getDimension(R.dimen.layout_stroke_points_height);

        mPointsLayoutRect.set(0, 0, mPointsWidth, mPointsHeight);
        mSmallPtClickRect.set((mPointsWidth - SMALL_POINT_CLICK_RING_SIZE) / 2,
                mPointsHeight * 5 / 6 - SMALL_POINT_CLICK_RING_SIZE / 2,
                (mPointsWidth + SMALL_POINT_CLICK_RING_SIZE) / 2,
                mPointsHeight * 5 / 6 + SMALL_POINT_CLICK_RING_SIZE / 2);

        mMidPtClickRect.set((mPointsWidth - MIDDLE_POINT_CLICK_RING_SIZE) / 2,
                (mPointsHeight - MIDDLE_POINT_CLICK_RING_SIZE) / 2,
                (mPointsWidth + MIDDLE_POINT_CLICK_RING_SIZE) / 2,
                (mPointsHeight + MIDDLE_POINT_CLICK_RING_SIZE) / 2);

        mLargePtClickRect.set((mPointsWidth - LARGE_POINT_CLICK_RING_SIZE) / 2,
                (mPointsHeight / 3 - LARGE_POINT_CLICK_RING_SIZE) / 2,
                (mPointsWidth + LARGE_POINT_CLICK_RING_SIZE) / 2,
                (mPointsHeight / 3 + LARGE_POINT_CLICK_RING_SIZE) / 2);
    }

    private void initPaint() {
        mPointsPaint = new Paint();
        mPointsPaint.setAntiAlias(true);
        mPointsPaint.setStyle(Paint.Style.FILL);
        mPointsPaint.setColor(Color.WHITE);

        mRingPaint = new Paint();
        mRingPaint.setAntiAlias(true);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setColor(mRingColor);
        mRingPaint.setStrokeWidth(mClickRingStroke);

        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgPaint.setColor(mBackgroundColor);
    }

    private void initPoints() {
        mSmallPoint = new PointState(PointState.SMALL, false);
        mMidPoint = new PointState(PointState.MIDDLE, true);
        mLargePoint = new PointState(PointState.LARGE, false);
    }

    public void setOnStrokeChangeListener(IStrokeWidthChange listener) {
        mStrokeChangeListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (mCheckForLongPress == null) {
                    mCheckForLongPress = new CheckForLongPress();
                }
                getHandler().postDelayed(mCheckForLongPress, 500);
                mLongPressed = false;
                mTouchDownX = event.getX();
                mTouchDownY = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                float mTouchUpX = event.getX();
                float mTouchUpY = event.getY();
                getHandler().removeCallbacks(mCheckForLongPress);
                if (!mLongPressed && Math.abs(mTouchUpX - mTouchDownX) < CANCEL_CLICK_DISTANCE
                        && Math.abs(mTouchUpY - mTouchDownY) < CANCEL_CLICK_DISTANCE) {
                    checkPointsClick(mTouchUpX, mTouchUpY);
                }
                break;
        }
        return true;
    }

    private void checkPointsClick(float mTouchUpX, float mTouchUpY) {
        if (isContainInRect(mSmallPtClickRect, mTouchUpX, mTouchUpY, mSmallRectOff)) {
            if (mStrokeChangeListener != null) {
                mStrokeChangeListener.onStrokeWidthChange(IStrokeWidthChange.STOKE_LEVEL_SMALL);
            }
            mSmallPoint.setIsClicked(true);
            mMidPoint.setIsClicked(false);
            mLargePoint.setIsClicked(false);
            invalidate();

        } else if (isContainInRect(mMidPtClickRect, mTouchUpX, mTouchUpY, mMidRectOff)) {
            if (mStrokeChangeListener != null) {
                mStrokeChangeListener.onStrokeWidthChange(IStrokeWidthChange.STOKE_LEVEL_NORMAL);
            }
            mSmallPoint.setIsClicked(false);
            mMidPoint.setIsClicked(true);
            mLargePoint.setIsClicked(false);
            invalidate();

        } else if (isContainInRect(mLargePtClickRect, mTouchUpX, mTouchUpY, mLargeRectOff)) {
            if (mStrokeChangeListener != null) {
                mStrokeChangeListener.onStrokeWidthChange(IStrokeWidthChange.STOKE_LEVEL_LARGE);
            }
            mSmallPoint.setIsClicked(false);
            mMidPoint.setIsClicked(false);
            mLargePoint.setIsClicked(true);
            invalidate();
        }
    }

    private boolean isContainInRect(RectF rectF, float pointX, float pointY, float offset) {
        return rectF.left < rectF.right && rectF.top < rectF.bottom  // check for empty first
                && pointX >= rectF.left - offset && pointX < rectF.right + offset
                && pointY >= rectF.top - offset && pointY < rectF.bottom + offset;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (h > mPointsHeight) {
            mPointsLayoutRect.set(0, 0, w, mPointsHeight);
        } else {
            mPointsLayoutRect.set(0, 0, w, h);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRoundRect(mPointsLayoutRect, VIEW_BACKGROUND_RADIUS, VIEW_BACKGROUND_RADIUS, mBgPaint);
        drawPoints(canvas);
    }

    // TODO:夜间模式
    private void drawPoints(Canvas canvas) {
        int centerX = (int) mPointsLayoutRect.centerX();

        if (mLargePoint.isClicked()) {
            canvas.drawArc(mLargePtClickRect, 0, 360, false, mRingPaint);
        }
        canvas.drawCircle(centerX, mPointsHeight / 6, LARGE_POINT_RADIUS, mPointsPaint);

        if (mMidPoint.isClicked()) {
            canvas.drawArc(mMidPtClickRect, 0, 360, false, mRingPaint);
        }
        canvas.drawCircle(centerX, mPointsHeight / 2, MIDDLE_POINT_RADIUS, mPointsPaint);

        if (mSmallPoint.isClicked()) {
            canvas.drawArc(mSmallPtClickRect, 0, 360, false, mRingPaint);
        }
        canvas.drawCircle(centerX, mPointsHeight * 5 / 6, SMALL_POINT_RADIUS, mPointsPaint);
    }

    private class PointState {
        private final static int SMALL = 0;
        private final static int MIDDLE = 1;
        private final static int LARGE = 2;
        private boolean isClicked;
        private int pointSize;

        PointState(int pointSize, boolean isClicked) {
            this.pointSize = pointSize;
            this.isClicked = isClicked;
        }

        public void setIsClicked(boolean isClicked) {
            this.isClicked = isClicked;
        }

        public boolean isClicked() {
            return isClicked;
        }

        public int getPointSize() {
            return pointSize;
        }
    }

    private class CheckForLongPress implements Runnable {
        @Override
        public void run() {
            mLongPressed = true;
        }
    }
}
