package com.biotech.drawlessons.photoedit.utilsmodel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Handler;
import android.view.View;


import com.biotech.drawlessons.R;
import com.biotech.drawlessons.photoedit.tools.ColorPickerBean;
import com.biotech.drawlessons.photoedit.tools.ColorPickerView;
import com.biotech.drawlessons.photoedit.tools.IStrokeWidthChange;
import com.biotech.drawlessons.photoedit.tools.StrokeUtilView;
import com.biotech.drawlessons.photoedit.utils.IPhotoEditType;

import java.lang.ref.WeakReference;

/**
 * Created by xintu on 2018/3/28.
 */

public class ColorPickerHolder implements ColorPickerView.OnColorPickerListener, IStrokeWidthChange {
    private ColorPickerView mColorPickerView;
    private StrokeUtilView mStrokeUtilsView;
    private int mColorPickerHeight;
    private View mRootView;
    private IColorPicker mIColorPicker;
    private ShowColorPickerRunnable mShowRunnable;
    private DismissColorPickerRunnable mDismissRunnable;

    public ColorPickerHolder(View rootView, IColorPicker iColorPicker) {
        mRootView = rootView;
        mIColorPicker = iColorPicker;
        findViews();
        initListener();
        mColorPickerHeight = (int) rootView.getResources().getDimension(R.dimen.color_pick_height);
    }

    private void findViews() {
        mColorPickerView = mRootView.findViewById(R.id.color_picker);
        mStrokeUtilsView = mRootView.findViewById(R.id.stroke_util_view);
    }

    private void initListener() {
        mColorPickerView.setOnItemClickListener(this);
        mStrokeUtilsView.setIDrawToolChangeListener(this);
        mShowRunnable = new ShowColorPickerRunnable(mColorPickerView, mStrokeUtilsView);
        mDismissRunnable = new DismissColorPickerRunnable(mColorPickerView, mStrokeUtilsView);
    }

    public void dismissWithoutAnim() {
        mColorPickerView.setVisibility(View.GONE);
        mStrokeUtilsView.setVisibility(View.GONE);
        Handler handler = mColorPickerView.getHandler();
        if (handler == null) {
            mColorPickerView.removeCallbacks(mShowRunnable);
        } else {
            handler.removeCallbacks(mShowRunnable);
        }
    }

    public ObjectAnimator getTranslateDismissAnim() {
        return getTranslateDismissAnim(mColorPickerHeight);
    }

    public ObjectAnimator getTranslateDismissAnim(float height) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mColorPickerView, "translationY", 0, height).setDuration(PhotoEditToolsHelper.ANIM_DURATION);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mColorPickerView.setVisibility(View.GONE);
                mStrokeUtilsView.setVisibility(View.GONE);
            }
        });
        return animator;
    }

    public ObjectAnimator getTranslateShowAnim() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mColorPickerView, "translationY", mColorPickerHeight, 0).setDuration(PhotoEditToolsHelper.ANIM_DURATION);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mColorPickerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ColorPickerBean bean = mColorPickerView.getSelectedBean();
                if (bean != null && bean.getType() == IPhotoEditType.BRUSH_NORMAL_COLOR) {
                    mStrokeUtilsView.setVisibility(View.VISIBLE);
                }
            }
        });
        return animator;
    }

    public ObjectAnimator getAlphaDismissAnim() {
        ObjectAnimator animator = ObjectAnimator
                .ofFloat(mColorPickerView, "alpha", 1F, 0F)
                .setDuration(PhotoEditToolsHelper.ANIM_DURATION);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mStrokeUtilsView.setAlpha((Float) animation.getAnimatedValue());
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mColorPickerView.setVisibility(View.GONE);
                mStrokeUtilsView.setVisibility(View.GONE);
            }
        });
        return animator;
    }

    public ObjectAnimator getAlphaShowAnim() {
        ObjectAnimator animator = ObjectAnimator
                .ofFloat(mColorPickerView, "alpha", 0F, 1F)
                .setDuration(PhotoEditToolsHelper.ANIM_DURATION);

        ColorPickerBean bean = mColorPickerView.getSelectedBean();
        boolean showUtilsView = bean != null && bean.getType() == IPhotoEditType.BRUSH_NORMAL_COLOR;

        if (showUtilsView) {
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mStrokeUtilsView.setAlpha((Float) animation.getAnimatedValue());
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mStrokeUtilsView.setVisibility(View.VISIBLE);
                    mColorPickerView.setVisibility(View.VISIBLE);
                }
            });
        } else {
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mColorPickerView.setVisibility(View.VISIBLE);
                }
            });
        }

        return animator;
    }

    public void showDelay(int delay) {
        Handler handler = mColorPickerView.getHandler();
        if (handler == null) {
            mColorPickerView.removeCallbacks(mDismissRunnable);
            mColorPickerView.postDelayed(mShowRunnable, delay);
        } else {
            handler.removeCallbacks(mDismissRunnable);
            handler.postDelayed(mShowRunnable, delay);
        }
    }

    public void dismissDelay(int delay) {
        Handler handler = mColorPickerView.getHandler();
        if (handler == null) {
            mColorPickerView.removeCallbacks(mShowRunnable);
            mColorPickerView.postDelayed(mDismissRunnable, delay);
        } else {
            // 把 showRunnable remove 掉
            handler.removeCallbacks(mShowRunnable);
            handler.postDelayed(mDismissRunnable, delay);
        }
    }

    public boolean isColorPickerShown() {
        return mColorPickerView.getVisibility() == View.VISIBLE;
    }

    public ColorPickerView getColorPickerView() {
        return mColorPickerView;
    }

    public int getColorPickerHeight() {
        return mColorPickerHeight;
    }

    public StrokeUtilView getStrokeUtilsView() {
        return mStrokeUtilsView;
    }

    public void onUndoStateChange(boolean canUndo) {
        if (canUndo) {
            mColorPickerView.getIconBack().setAlpha(1f);
            mColorPickerView.getIconBack().setEnabled(true);
        } else {
            mColorPickerView.getIconBack().setAlpha(0.3f);
            mColorPickerView.getIconBack().setEnabled(false);
        }
    }

    @Override
    public void onColorItemClick(int position, ColorPickerBean bean) {
        mIColorPicker.onColorItemClick(position, bean);
    }

    @Override
    public void onColorPickerBackIconClick() {
        mIColorPicker.onColorPickerBackIconClick();
    }

    @Override
    public void onStrokeWidthChange(int strokeLevel) {
        mIColorPicker.onStrokeWidthChange(strokeLevel);
    }

    private static class ShowColorPickerRunnable implements Runnable {
        WeakReference<ColorPickerView> mWeakColorPicker;
        WeakReference<StrokeUtilView> mWeakStrokeUtils;
        ObjectAnimator mAnimator;

        ShowColorPickerRunnable(ColorPickerView colorPickerView, StrokeUtilView strokeUtilView) {
            mWeakColorPicker = new WeakReference<ColorPickerView>(colorPickerView);
            mWeakStrokeUtils = new WeakReference<StrokeUtilView>(strokeUtilView);
        }

        ObjectAnimator getAlphaShowAnim() {
            final ColorPickerView colorPickerView = mWeakColorPicker.get();
            final StrokeUtilView strokeUtilsView = mWeakStrokeUtils.get();
            if (colorPickerView == null || strokeUtilsView == null) {
                return null;
            }
            if (mAnimator != null && mAnimator.isRunning()) {
                return mAnimator;
            }

            ObjectAnimator animator = ObjectAnimator
                    .ofFloat(colorPickerView, "alpha", 0F, 1F)
                    .setDuration(PhotoEditToolsHelper.ANIM_DURATION);

            ColorPickerBean bean = colorPickerView.getSelectedBean();
            boolean showUtilsView = bean != null && bean.getType() == IPhotoEditType.BRUSH_NORMAL_COLOR;

            if (showUtilsView) {
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        strokeUtilsView.setAlpha((Float) animation.getAnimatedValue());
                    }
                });
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        strokeUtilsView.setVisibility(View.VISIBLE);
                        colorPickerView.setVisibility(View.VISIBLE);
                    }
                });
            } else {
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        colorPickerView.setVisibility(View.VISIBLE);
                    }
                });
            }

            return animator;
        }

        @Override
        public void run() {
            ObjectAnimator animator = getAlphaShowAnim();
            if (animator == null || animator.isRunning()) {
                return;
            }
            animator.start();
        }
    }

    private static class DismissColorPickerRunnable implements Runnable {
        WeakReference<ColorPickerView> mWeakColorPicker;
        WeakReference<StrokeUtilView> mWeakStrokeUtils;
        private ObjectAnimator mAnimator;

        DismissColorPickerRunnable(ColorPickerView colorPickerView, StrokeUtilView strokeUtilView) {
            mWeakColorPicker = new WeakReference<ColorPickerView>(colorPickerView);
            mWeakStrokeUtils = new WeakReference<StrokeUtilView>(strokeUtilView);
        }

        ObjectAnimator getAlphaDismissAnim() {
            final ColorPickerView colorPickerView = mWeakColorPicker.get();
            final StrokeUtilView strokeUtilsView = mWeakStrokeUtils.get();
            if (colorPickerView == null || strokeUtilsView == null) {
                return null;
            }
            // 如果animator已经开始run，就返回当前的animator，并且在外边判断正在running的话，不做别的处理
            if (mAnimator != null && mAnimator.isRunning()) {
                return mAnimator;
            }

            mAnimator = ObjectAnimator
                    .ofFloat(colorPickerView, "alpha", 1F, 0F)
                    .setDuration(PhotoEditToolsHelper.ANIM_DURATION);

            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    strokeUtilsView.setAlpha((Float) animation.getAnimatedValue());
                }
            });

            mAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    colorPickerView.setVisibility(View.GONE);
                    strokeUtilsView.setVisibility(View.GONE);
                    colorPickerView.setAlpha(1);
                    strokeUtilsView.setAlpha(1);
                }
            });
            return mAnimator;
        }

        @Override
        public void run() {
            ObjectAnimator animator = getAlphaDismissAnim();
            if (animator == null || animator.isRunning()) {
                return;
            }
            animator.start();
        }
    }
}
