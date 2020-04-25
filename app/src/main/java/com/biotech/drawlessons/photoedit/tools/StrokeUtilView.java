package com.biotech.drawlessons.photoedit.tools;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.biotech.drawlessons.R;
import com.biotech.drawlessons.UtilsKt;


/**
 * Created by xintu on 2018/2/26.
 */

public class StrokeUtilView extends RelativeLayout implements View.OnClickListener {
    private Context mContext;
    private StrokePointsView mPointsView;
    private ImageView mIvChoose;
    private int mIvChooseSize;
    private int mIvChooseColor;
    private int mPointsViewHeight;
    private ValueAnimator mPointsAnim;
    private boolean mPointsShow;
    private int dp_14;
    private boolean mAnimating;

    public StrokeUtilView(Context context) {
        this(context, null);
    }

    public StrokeUtilView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StrokeUtilView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_stroke_width_util, this);

        mIvChooseSize = (int) mContext.getResources().getDimension(R.dimen.stroke_width_util_width);
        mIvChooseColor = mContext.getResources().getColor(R.color.Blk_1_alpha_50);
        mPointsViewHeight = (int) mContext.getResources().getDimension(R.dimen.layout_stroke_points_height);
        dp_14 = (int) UtilsKt.dp2px( 14);

        findViews(view);
        initListener();
        initAnimator();
    }

    private void findViews(View view) {
        mPointsView = view.findViewById(R.id.stroke_points_view);
        mIvChoose = view.findViewById(R.id.iv_choose);

        GradientDrawable chooseDrawable = new GradientDrawable();
        chooseDrawable.setShape(GradientDrawable.OVAL);
        chooseDrawable.setColor(mIvChooseColor);
        chooseDrawable.setSize(mIvChooseSize, mIvChooseSize);
        mIvChoose.setBackgroundDrawable(chooseDrawable);
    }

    private void initListener() {
        mIvChoose.setOnClickListener(this);
        mPointsView.setOnClickListener(this);
    }

    private void initAnimator() {
        mPointsAnim = ValueAnimator.ofInt(mPointsViewHeight, 0).setDuration(275);
        mPointsAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimating = false;

            }

            @Override
            public void onAnimationStart(Animator animation) {
                mAnimating = true;
            }
        });
    }

    public void setIDrawToolChangeListener(IStrokeWidthChange listener) {
        mPointsView.setOnStrokeChangeListener(listener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_choose:
                if (!mPointsShow) {
                    showPoints();
                } else {
                    dismissPoints();
                }
                break;
        }
    }

    private void showPoints() {
        if (mAnimating) {
            return;
        }

        mPointsShow = true;
        mPointsView.setVisibility(VISIBLE);
        mPointsAnim.setIntValues(dp_14 + mPointsViewHeight, 0);
        mPointsAnim.removeAllUpdateListeners();
        mPointsAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                int value = (int) animation.getAnimatedValue();
                mPointsView.setAlpha(fraction);
                ((LayoutParams) mPointsView.getLayoutParams()).topMargin = value;
                mPointsView.requestLayout();
            }
        });

        mPointsAnim.setDuration(275);
        mPointsAnim.start();
    }

    private void dismissPoints() {
        if (mAnimating) {
            return;
        }
        mPointsView.setVisibility(VISIBLE);
        mPointsAnim.setIntValues(0, mPointsViewHeight + dp_14);
        mPointsAnim.removeAllUpdateListeners();
        mPointsShow = false;
        mPointsAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                int value = (int) animation.getAnimatedValue();
                mPointsView.setAlpha(1 - fraction);
                ((LayoutParams) mPointsView.getLayoutParams()).topMargin = value;
                mPointsView.requestLayout();
            }
        });
        mPointsAnim.setDuration(275);
        mPointsAnim.start();
    }

}
