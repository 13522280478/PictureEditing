package com.biotech.drawlessons.photoedit.gesture;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

/**
 * Created by xintu on 2018/2/4.
 */

public class TouchGestureDetector {
    private static final int INVALID_POINTER_ID = -1;
    private int mActivePointerId = INVALID_POINTER_ID;
    private int mPtID1 = INVALID_POINTER_ID, mPtID2 = INVALID_POINTER_ID;
    private float mPt1X, mPt1Y, mPt2X, mPt2Y;
    private int mActivePointerIndex = 0;
    private ScaleGestureDetector mDetector;

    private VelocityTracker mVelocityTracker;
    private boolean mIsDragging;
    private float mLastTouchX;
    private float mLastTouchY;
    private final float mTouchSlop;
    private final float mMinimumVelocity;
    private ITouchGesture mListener;
    private float mLastAngle;

    public TouchGestureDetector(Context context, ITouchGesture listener) {
        final ViewConfiguration configuration = ViewConfiguration
                .get(context);
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mTouchSlop = configuration.getScaledTouchSlop();

        ScaleGestureDetector.OnScaleGestureListener mScaleListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scaleFactor = detector.getScaleFactor();

                if (Float.isNaN(scaleFactor) || Float.isInfinite(scaleFactor)) {
                    return false;
                }

                mListener.onScale(scaleFactor, detector.getFocusX(), detector.getFocusY());
                return true;
            }
        };
        mDetector = new ScaleGestureDetector(context, mScaleListener);
        mListener = listener;
    }

    public void setOnTouchGestureListener(ITouchGesture listener) {
        mListener = listener;
    }

    private float getActiveX(MotionEvent ev) {
        try {
            return ev.getX(mActivePointerIndex);
        } catch (Exception e) {
            return ev.getX();
        }
    }

    private float getActiveY(MotionEvent ev) {
        try {
            return ev.getY(mActivePointerIndex);
        } catch (Exception e) {
            return ev.getY();
        }
    }

    public boolean isScaling() {
        return mDetector.isInProgress();
    }

    public boolean isDragging() {
        return mIsDragging;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        try {
            mDetector.onTouchEvent(ev);
            return processTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            // Fix for support lib bug, happening when onDestroy is called
            return true;
        }
    }

    private boolean processTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = event.getPointerId(0);
                mVelocityTracker = VelocityTracker.obtain();
                if (null != mVelocityTracker) {
                    mVelocityTracker.addMovement(event);
                }

                mLastTouchX = getActiveX(event);
                mLastTouchY = getActiveY(event);
                if (mListener != null) {
                    mListener.onTouchDown(mLastTouchX, mLastTouchY);
                }
                mIsDragging = false;
                mPtID1 = event.getPointerId(event.getActionIndex());
                break;

            case MotionEvent.ACTION_MOVE:
                final float x = getActiveX(event);
                final float y = getActiveY(event);
                final float dx = x - mLastTouchX, dy = y - mLastTouchY;

                if (!mIsDragging) {
                    // Use Pythagoras to see if drag length is larger than
                    // touch slop
                    mIsDragging = Math.sqrt((dx * dx) + (dy * dy)) >= mTouchSlop;
                }

                if (mIsDragging) {
                    mListener.onTouchMove(x, y, mLastTouchX, mLastTouchY);
                    mLastTouchX = x;
                    mLastTouchY = y;

                    if (null != mVelocityTracker) {
                        mVelocityTracker.addMovement(event);
                    }
                }

                if(mPtID1 != INVALID_POINTER_ID && mPtID2 != INVALID_POINTER_ID){
                    float mPt1_x = event.getX(event.findPointerIndex(mPtID1));
                    float mPt1_y = event.getY(event.findPointerIndex(mPtID1));
                    float mPt2_x = event.getX(event.findPointerIndex(mPtID2));
                    float mPt2_y = event.getY(event.findPointerIndex(mPtID2));

                    float mCurrAngle = computeRotationAngle(mPt2X, mPt2Y, mPt1X, mPt1Y, mPt2_x, mPt2_y, mPt1_x, mPt1_y);

                    //TODO:这块 pointer index 需要重新再写，写的太随意了
                    if (Math.abs(mCurrAngle) >= 3) {
                        mListener.onRotate(mLastAngle - mCurrAngle);
                    }

                    mLastAngle = mCurrAngle;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                mActivePointerId = INVALID_POINTER_ID;
                // Recycle Velocity Tracker
                if (null != mVelocityTracker) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                break;

            case MotionEvent.ACTION_UP:
                mActivePointerId = INVALID_POINTER_ID;
                if (mIsDragging) {
                    if (null != mVelocityTracker) {
                        mLastTouchX = getActiveX(event);
                        mLastTouchY = getActiveY(event);

                        // Compute velocity within the last 1000ms
                        mVelocityTracker.addMovement(event);
                        mVelocityTracker.computeCurrentVelocity(1000);

                        final float vX = mVelocityTracker.getXVelocity(), vY = mVelocityTracker
                                .getYVelocity();

                        // If the velocity is greater than minVelocity, call
                        // listener
                        if (Math.max(Math.abs(vX), Math.abs(vY)) >= mMinimumVelocity) {
                            mListener.onFling(mLastTouchX, mLastTouchY, -vX, -vY);
                        }
                    }
                }
                if (mListener != null) {
                    mListener.onTouchUp(mLastTouchX, mLastTouchY);
                }

                // Recycle Velocity Tracker
                if (null != mVelocityTracker) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                mPtID1 = INVALID_POINTER_ID;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                final int pointerIndex = getPointerIndex(event.getAction());
                final int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mActivePointerId = event.getPointerId(newPointerIndex);
                    mLastTouchX = event.getX(newPointerIndex);
                    mLastTouchY = event.getY(newPointerIndex);
                }
                mPtID2 = INVALID_POINTER_ID;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                mPtID2 = event.getPointerId(event.getActionIndex());

                mPt1X = event.getX(event.findPointerIndex(mPtID1));
                mPt1Y = event.getY(event.findPointerIndex(mPtID1));
                mPt2X = event.getX(event.findPointerIndex(mPtID2));
                mPt2Y = event.getY(event.findPointerIndex(mPtID2));
                break;

        }

        mActivePointerIndex = event.findPointerIndex(mActivePointerId != INVALID_POINTER_ID ? mActivePointerId
                        : 0);
        return true;
    }

    private int getPointerIndex(int action) {
        return (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
    }

    /**
     * 计算旋转的角度
     * pt1X、pt1Y：第二根手指按下时，第一根手指的x、y
     * pt2X、pt2Y：第二根手指按下时，第二根手指的x、y
     * <p>
     * pt1_X、pt_1Y：移动之后第一根手指的x、y
     * pt2_X、pt_2Y：移动之后第二根手指的x、y
     */
    private float computeRotationAngle(float pt2X, float pt2Y, float pt1X,
                                       float pt1Y, float pt2_X, float pt2_Y,
                                       float pt1_X, float pt1_Y) {
        float angle;
        float angle1 = (float) Math.atan2((pt2Y - pt1Y), (pt2X - pt1X));
        float angle2 = (float) Math.atan2((pt2_Y - pt1_Y), (pt2_X - pt1_X));

        angle = ((float) Math.toDegrees(angle1 - angle2)) % 360;
        return angle;
    }
}
