package com.biotech.drawlessons.photoedit.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.biotech.drawlessons.UtilsKt;

/**
 * Created by xintu on 2017/8/29.
 */

public class ActivityDragLayout extends AbstractDragLayout {
    private boolean mEnableDrag;

    public ActivityDragLayout(Context context) {
        super(context);
        initListener();
    }

    public ActivityDragLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initListener();
    }

    public ActivityDragLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initListener();
    }

    @Override
    public int closeDistance(Context context) {
        return(int) UtilsKt.dp2px(44);
    }

    public void setEnableDrag(boolean enableDrag){
        mEnableDrag = enableDrag;
    }

    private void initListener(){
        setDragListener(listener);

//        setLimitAngles(45,135, AbstractDragLayout.ORIENTATION_VERTICAL);
    }

    DragHelperCallback listener = new DragHelperCallback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return mEnableDrag;
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {

        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            int leftBound = getPaddingLeft();
            int rightBound = getWidth() - mChildView.getWidth();
            return Math.min(Math.max(left, leftBound), rightBound);
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            int topBound = 0;
            if (child != null && child.getLayoutParams() != null
                    && child.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                topBound = ((RelativeLayout.LayoutParams) child.getLayoutParams()).topMargin;
            }
            return Math.max(top, topBound);
        }
    };

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mIsFirstLayout) {
            mIsFirstLayout = false;
            mTop = 0;
        }
        super.onLayout(changed, l, t, r, b);

        if (mNeedClose) {
            mChildView.layout(
                    0,
                    mTop,
                    r,
                    mTop + mChildView.getMeasuredHeight());
        }
    }
}
