package com.biotech.drawlessons.photoedit.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

import com.biotech.drawlessons.UtilsKt;


public abstract class AbstractDragLayout extends RelativeLayout {

    private final static String TAG = "AbstractDragLayout";
    private int mHeight;

//    private final static int SCREEN_HEIGHT = Global.screenHeight;
    public final static int ORIENTATION_VERTICAL = 1;
    public final static int ORIENTATION_HORIZONTAL = 2;

    protected View mChildView;

    private ViewDragHelper mViewDragHelper;
    protected boolean mIsRelease = false;
    protected boolean mNeedClose = false;
    private boolean mLegalAngle, mAngleComputed, mLimitAngle = false;

    // 第一次action down时的位置
    protected float mInitMotionX;
    protected float mInitMotionY;

    private OnDragStateChangeListener mDragStateListener;
    protected int mTop;
    private int mCloseDistance;
    protected float mDragOffset;
    private float mMinAngle, mMaxAngle;
    private int mLimitOrientation;
    protected boolean mIsFirstLayout = true;
    // 开始拖拽
    protected boolean mBeginDrag = false;
    private DragHelperCallback mListener;
    private Context mContext;

    public AbstractDragLayout(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public AbstractDragLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public AbstractDragLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragCallback());
        mCloseDistance = closeDistance(mContext);
        mHeight = getResources().getDisplayMetrics().heightPixels;
    }

    interface DragHelperCallback {
        boolean tryCaptureView(View child, int pointerId);
        void onViewCaptured(View capturedChild, int activePointerId);
        int clampViewPositionHorizontal(View child, int left, int dx);
        int clampViewPositionVertical(View child, int top, int dy);
    }

    // 设置下拉多少距离之后触发关闭回调
    public abstract int closeDistance(Context context);


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        // 多点触控 不拦截
        if (ev.getPointerCount() > 1) {
            return false;
        }
        if (ev.getAction() == MotionEvent.ACTION_CANCEL || ev.getAction() == MotionEvent.ACTION_UP) {
            mViewDragHelper.cancel();
            return false;
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                // 在onTouchEvent里不会掉第一次的down事件
                // 为啥？
                mInitMotionX = ev.getX();
                mInitMotionY = ev.getY();
                break;
            }

        }
        try {
            return mViewDragHelper.shouldInterceptTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 开启角度限制的功能，在某个角度之内，才能允许拖动
     * */
    public void setLimitAngles(float minAngle, float maxAngle, int orientation){
        mMinAngle = minAngle;
        mMaxAngle = maxAngle;
        mLimitAngle = true;
        mLimitOrientation = orientation;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();


        switch (event.getAction() & MotionEventCompat.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mInitMotionX = x;
                mInitMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                // 如果限制角度，需要滑动角度做一个判断：当滑动距离超过6dp的时候，计算滑动的角度，
                // 如果角度大于min且小于max的时候，判断角度合法
                if (mLimitAngle && !mLegalAngle && !mAngleComputed) {
                    float angle = 0;
                    // 如果是横向的限制，判断x的距离，如果是纵向的限制，判断y的距离
                    if (((mLimitOrientation == ORIENTATION_HORIZONTAL)
                            && Math.abs(x - mInitMotionX) > UtilsKt.dp2px(6))
                            || ((mLimitOrientation == ORIENTATION_VERTICAL)
                            && Math.abs(y - mInitMotionY) > UtilsKt.dp2px(6))) {
                        angle = (float) Math.toDegrees(Math.atan2(y - mInitMotionY, x - mInitMotionX));
                        mAngleComputed = true;

                    }
                    if (angle > mMinAngle && angle < mMaxAngle) {
                        mLegalAngle = true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                // 只有是向下drag才去关闭
                if (y > mInitMotionY) {
                    float dx = x - mInitMotionX;
                    float dy = y - mInitMotionY;
                    // 如果限制角度，判断是否关闭的同时，需要判断角度是否合法
                    if (mLimitAngle){
                        mNeedClose = mLegalAngle && dy > mCloseDistance;
                    } else {
                        mNeedClose = dy > mCloseDistance;
                    }
                }
                mLegalAngle = false;
                mAngleComputed = false;
                break;
        }
        // 如果开启了角度限制，计算出的角度不合法的话，这次滑动无效
        if (mLimitAngle && !mLegalAngle && mAngleComputed) {
            return false;
        }
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mChildView = getChildAt(0);
    }

    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }


    public void setOnDragStateChangeListener(OnDragStateChangeListener listener) {
        this.mDragStateListener = listener;
    }

    /**
     * 子类可重写此方法
     * 该方法在listener的onClose回调之前调用
     *
     * @param top 被拖拽的view的getTop()
     */
    protected void onClose(int top) {
    }

    public void resetData() {
        mIsRelease = false;
        mNeedClose = false;

        mInitMotionX = 0f;
        mInitMotionY = 0f;

        mTop = 0;
        mDragOffset = 0f;
        mIsFirstLayout = true;
        mBeginDrag = false;
    }


    public interface OnDragStateChangeListener {
        /**
         * 拖拽到一定范围后需要关闭的回调
         *
         * @param top 被拖拽的view的getTop()
         */
        void onClose(int top);

        /**
         * 拖拽过程中view位置改变的回调
         *
         * @param top         被拖拽view的getTop()
         * @param mDragOffset 相对于初始位置的移动比例
         */
        void onViewPositionChange(int top, float mDragOffset);

        /**
         * 开始拖拽操作
         */
        void onBeginDrag();
    }

    private class ViewDragCallback extends ViewDragHelper.Callback {

        @Override
        public int getViewHorizontalDragRange(View child) {
            return 1;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return 1;
        }

        /**
         * 尝试捕获子view，一定要返回true
         *
         * @param child     尝试捕获的view
         * @param pointerId 指示器id？
         *                  这里可以决定哪个子view可以拖动
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            if (!mIsRelease) {
                mIsRelease = true;
            }
            if (mListener != null){
                return mListener.tryCaptureView(child, pointerId);
            }
            return true;
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            if (mListener != null) {
                mListener.onViewCaptured(capturedChild, activePointerId);
            }
            super.onViewCaptured(capturedChild, activePointerId);
        }

        /**
         * 水平拖拽时 view不能超出屏幕边界
         *
         * @param child
         * @param left
         * @param dx
         * @return
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (mListener != null){
                return mListener.clampViewPositionHorizontal(child, left, dx);
            }
            int leftBound = getPaddingLeft();
            int rightBound = getWidth() - mChildView.getWidth();
            int newLeft = Math.min(Math.max(left, leftBound), rightBound);
            return newLeft;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            if (mListener != null){
                return mListener.clampViewPositionVertical(child, top, dy);
            }
            return top;
        }


        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            mIsRelease = true;
            super.onViewReleased(releasedChild, xvel, yvel);
            // 回到原位置
            if (false == mNeedClose) {
                int top = 0;
                if (releasedChild.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    top = ((MarginLayoutParams) releasedChild.getLayoutParams()).topMargin;
                }
                mViewDragHelper.settleCapturedViewAt(0, top);
                ViewCompat.postInvalidateOnAnimation(AbstractDragLayout.this);
            } else {
                int top = mChildView.getTop();
                // 弹回到小屏View
                onClose(top);
                if (mDragStateListener != null) {
                    mDragStateListener.onClose(top);
                }
            }
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            mTop = top;
            mDragOffset = (float) Math.abs(top) / mHeight;
            if (mDragStateListener != null) {
                mDragStateListener.onViewPositionChange(top, mDragOffset);
            }

            if (!mIsRelease) {
                return;
            }
            if (!mNeedClose) {
                return;
            }
            requestLayout();
//            float scaleX = (float) mReturnViewWidth / (float) mChildW;
//            float scaleY = (float) mReturnViewHeight / (float) mChildH;
//
//            LogUtils.d(TAG, "scale x: " + scaleX);
//            LogUtils.d(TAG, "scale y: " + scaleY);
//
//            float scaleYReal = ((float) top) * scaleY / (mReturnTop);
//            float scaleXReal = ((float) left) * scaleX / (mReturnLeft);
//            if (scaleXReal > 1f) {
//                scaleXReal = 1f;
//            }
//            if (scaleYReal > 1f) {
//                scaleYReal = 1f;
//            }
//            LogUtils.d(TAG, "real scale y: " + scaleYReal);
//            LogUtils.d(TAG, "real scale x: " + scaleXReal);
//            mChildView.setPivotX(0);
//            mChildView.setPivotY(0);
//            mChildView.setScaleY(scaleYReal);
//            mChildView.setScaleX(scaleXReal);
//            mChildView.setAlpha(scaleXReal);
        }

        /**
         * 当拖拽到状态改变时回调
         *
         * @params 新的状态
         */
        @Override
        public void onViewDragStateChanged(int state) {
            switch (state) {
                // 正在被拖动
                case ViewDragHelper.STATE_DRAGGING:
                    if (mBeginDrag == false && mDragStateListener != null) {
                        mDragStateListener.onBeginDrag();
                    }
                    break;
                // view没有被拖拽或者 正在进行fling/snap
                case ViewDragHelper.STATE_IDLE:
                    break;
                // fling完毕后被放置到一个位置
                case ViewDragHelper.STATE_SETTLING:
                    mBeginDrag = false;
                    break;
            }
            super.onViewDragStateChanged(state);
        }
    }

    public void setDragListener(DragHelperCallback listener){
        mListener = listener;
    }
}
