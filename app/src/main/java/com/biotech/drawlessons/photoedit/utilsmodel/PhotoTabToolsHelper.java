//package com.biotech.drawlessons.photoedit.utilsmodel;
//
//import android.animation.Animator;
//import android.animation.AnimatorListenerAdapter;
//import android.animation.AnimatorSet;
//import android.animation.ObjectAnimator;
//import android.app.Activity;
//import android.provider.Settings;
//import android.view.View;
//import android.widget.RelativeLayout;
//
//import com.biotech.drawlessons.photoedit.utils.BitmapsManager;
//import com.biotech.drawlessons.photoedit.utils.DrawInvoker;
//import com.biotech.drawlessons.photoedit.views.DrawingBoardView;
//
//
///**
// * Created by xintu on 2018/3/29.
// */
//
//public class PhotoTabToolsHelper extends AbsToolsHelper implements IBeautyTools, View.OnClickListener{
//    private BeautyRightBar mRightBar;
//    private int mBeautyPickerMarginTop;
//
//    public PhotoTabToolsHelper(Activity activity, View rootView, DrawingBoardView boardView,
//                               BitmapsManager bitmapsManager, DrawInvoker invoker, BeautyRightBar rightBar) {
//        super(activity, rootView, boardView, bitmapsManager, invoker);
//        mRightBar = rightBar;
//        initListener();
//
//        mBeautyPickerMarginTop = Settings.Global.screenHeight - ScreenUtils.dp2Px(SnsApplication.getInstance(), 287);
//    }
//
//    private void initListener() {
//        mRightBar.getImgBeauty().setOnClickListener(this);
//        mRightBar.getImgFont().setOnClickListener(this);
//        mRightBar.getImgGraffiti().setOnClickListener(this);
//        mRightBar.getImgSticker().setOnClickListener(this);
//    }
//
//    @Override
//    void startShrinkToolsAnim(final AnimFinishCallback<Boolean> callback) {
//        ObjectAnimator dismissColorPickerAnim = null;
//
//        if (mColorPickerHolder.isColorPickerShown()) {
//            dismissColorPickerAnim = mColorPickerHolder.getTranslateDismissAnim();
//        }
//
//        ObjectAnimator dismissToolsAnim = mRightBar.getShrinkToolsAnim();
//        if (dismissColorPickerAnim != null) {
//            AnimatorSet set = new AnimatorSet();
//            set.playTogether(dismissColorPickerAnim, dismissToolsAnim);
//            set.addListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    if (callback != null) {
//                        callback.onAnimFinish(true);
//                    }
//                }
//            });
//            set.start();
//        } else {
//            dismissToolsAnim.addListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    if (callback != null) {
//                        callback.onAnimFinish(true);
//                    }
//                }
//            });
//            dismissToolsAnim.start();
//        }
//    }
//
//    @Override
//    void startExpandTools() {
//        ObjectAnimator animator = mRightBar.getExpandToolsAnim();
//        animator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                mDrawingBoard.switchToIdleLayer();
//            }
//        });
//        animator.start();
//    }
//
//    @Override
//    public boolean dealBackEvent() {
//        return dealShrinkColorPicker() || dealMaterialPickerClose();
//    }
//
//    private boolean dealShrinkColorPicker() {
//        if (mColorPickerHolder.isColorPickerShown()) {
//            onBrushIconClick();
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public void onMaterialPickerClose() {
//        startExpandTools();
//    }
//
//    @Override
//    public void onBeautyIconClick() {
//        startShrinkToolsAnim(new AnimFinishCallback<Boolean>() {
//            @Override
//            public void onAnimFinish(Boolean object) {
//                mMaterialModel.beautyPickerClick();
//            }
//        });
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.iv_sticker:
//                ((RelativeLayout.LayoutParams)mDragResourceView.getLayoutParams()).topMargin = 0;
//                onStickerIconClick();
//                break;
//
//            case R.id.iv_font:
//                onTextIconClick();
//                break;
//
//            case R.id.iv_beauty:
//                ((RelativeLayout.LayoutParams)mDragResourceView.getLayoutParams()).topMargin = mBeautyPickerMarginTop;
//                onBeautyIconClick();
//                break;
//
//            case R.id.iv_graffiti:
//                onBrushIconClick();
//                break;
//        }
//    }
//}
