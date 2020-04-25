package com.biotech.drawlessons.photoedit.utilsmodel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.biotech.drawlessons.R;
import com.biotech.drawlessons.photoedit.layers.CropLayer;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.model.FilterData;
import com.biotech.drawlessons.photoedit.utils.AnimFinishCallback;
import com.biotech.drawlessons.photoedit.utils.BitmapsManager;
import com.biotech.drawlessons.photoedit.utils.DrawInvoker;
import com.biotech.drawlessons.photoedit.views.DrawingBoardView;


/**
 * Created by xintu on 2018/3/27.
 */

public class PhotoEditToolsHelper extends AbsToolsHelper implements IPhotoEditActivityTools, IExpandLayout, CropLayer.OnCropStateChangeListener {
    private ToolsLayoutHolder mToolsLayoutHolder;
    private ExpandLayoutHolder mExpandLayoutHolder;
    private TextView mTvFinish;
    private TextView mTvCancel;

    public PhotoEditToolsHelper(Activity activity, View rootView, DrawingBoardView boardView,
                                BitmapsManager bitmapsManager, DrawInvoker invoker) {
        super(activity, rootView, boardView, bitmapsManager, invoker);
        findViews();
        initHolders();
    }

    private void initHolders() {
        mToolsLayoutHolder = new ToolsLayoutHolder(mRootView, this);
        mExpandLayoutHolder = new ExpandLayoutHolder(mRootView, this);
    }

    private void findViews() {
        mTvFinish = mRootView.findViewById(R.id.tv_finish);
        mTvCancel = mRootView.findViewById(R.id.tv_cancel);
    }

    @Override
    void startShrinkToolsAnim(final AnimFinishCallback<Boolean> callback) {
        ObjectAnimator dismissColorPickerAnim = null;
        int translationY;

        if (mColorPickerHolder.isColorPickerShown()) {
            translationY = mColorPickerHolder.getColorPickerHeight() + mToolsLayoutHolder.getLlToolHeight();
            dismissColorPickerAnim = mColorPickerHolder.getTranslateDismissAnim(translationY);
        } else {
            translationY = mToolsLayoutHolder.getLlToolHeight();
        }

        ObjectAnimator dismissToolsAnim = mToolsLayoutHolder.getDismissAnim(translationY);
        if (dismissColorPickerAnim != null) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(dismissColorPickerAnim, dismissToolsAnim);
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (callback != null) {
                        callback.onAnimFinish(true);
                    }
                }
            });
            set.start();
        } else {
            dismissToolsAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (callback != null) {
                        callback.onAnimFinish(true);
                    }
                }
            });
            dismissToolsAnim.start();
        }
    }

    @Override
    void startExpandTools() {
        ObjectAnimator animator = mToolsLayoutHolder.getShowAnim();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mDrawingBoard.switchToIdleLayer();
                mTvFinish.setVisibility(View.VISIBLE);
                mTvCancel.setVisibility(View.VISIBLE);
            }
        });
        animator.start();
    }

    @Override
    public boolean dealBackEvent() {
        return dealMaterialPickerClose() || dealShrinkColorPicker() || dealShrinkExpandLayout();
    }

    @Override
    public void onBrushIconClick() {
        if (mColorPickerHolder.isColorPickerShown()) {
            mToolsLayoutHolder.setBrushUnClickState();
        } else {
            mToolsLayoutHolder.setBrushClickState();
        }
        super.onBrushIconClick();
    }

    private boolean dealShrinkColorPicker() {
        if (mColorPickerHolder.isColorPickerShown()) {
            onBrushIconClick();
            return true;
        }

        return false;
    }

    private boolean dealShrinkExpandLayout() {
        if (mExpandLayoutHolder.isShown()) {
            onCancelIconClick();
            return true;
        }
        return false;
    }

    @Override
    public void onMaterialPickerClose() {
        if (mExpandLayoutHolder != null) {
            startExpandTools();
            showCancelAndFinish();
        }
    }

    @Override
    public void onCropIconClick() {
        mExpandLayoutHolder.setDoneEnable(false);
        hideCancelAndFinish();
        startShrinkToolsAnim(new AnimFinishCallback<Boolean>() {
            @Override
            public void onAnimFinish(Boolean object) {
                Animator animator2 = mExpandLayoutHolder.getShowCropUtilsAnim();
                animator2.start();
                mDrawingBoard.startCrop(PhotoEditToolsHelper.this);
            }
        });
    }

    @Override
    public void onFilterIconClick() {
        mExpandLayoutHolder.setDoneEnable(false);
        hideCancelAndFinish();
        startShrinkToolsAnim(new AnimFinishCallback<Boolean>() {
            @Override
            public void onAnimFinish(Boolean object) {
                Animator animator2 = mExpandLayoutHolder.getShowFilterAnim();
                animator2.start();
            }
        });
    }

    @Override
    public void onStickerIconClick() {
        hideCancelAndFinish();
        super.onStickerIconClick();
    }

    @Override
    public void onTextIconClick() {
        hideCancelAndFinish();
        super.onTextIconClick();
    }

    @Override
    public void onCancelIconClick() {
        ObjectAnimator animator = mExpandLayoutHolder.getDismissAnim();
        if (mExpandLayoutHolder.isCropUtilsShown()) {
            mDrawingBoard.cancelCropMode();
        } else if (mExpandLayoutHolder.isFilterViewShown()) {
            mDrawingBoard.cancelSetFilter();
        }

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startExpandTools();
                showCancelAndFinish();
            }
        });
        animator.start();
    }

    @Override
    public void onDoneIconClick() {
        if (mExpandLayoutHolder.isCropUtilsShown()) {
            mDrawingBoard.doneCropMode();
        } else if (mExpandLayoutHolder.isFilterViewShown()) {
            mDrawingBoard.doneSetFilter();
        }
        ObjectAnimator animator = mExpandLayoutHolder.getDismissAnim();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                showCancelAndFinish();
                startExpandTools();
            }
        });
        animator.start();
    }

    @Override
    public void onRestoreIconClick() {
        mDrawingBoard.restoreCrop();
    }

    @Override
    public void onRotateIconClick() {
        mDrawingBoard.rotateCrop();
    }

    @Override
    public void onFilterItemClick(View view, int position, FilterData data) {
        if (data == null) {
            return;
        }
        if (!mDrawingBoard.getMCurFilterType().equals(data.filterType)) {
            mExpandLayoutHolder.setDoneEnable(true);
            mDrawingBoard.setFilter(data.filterType);
        }
    }

    private void hideCancelAndFinish() {
        mTvCancel.setVisibility(View.GONE);
        mTvFinish.setVisibility(View.GONE);
    }

    private void showCancelAndFinish() {
        mTvCancel.setVisibility(View.VISIBLE);
        mTvFinish.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCropStateChange(boolean cropBorderChange) {
        mExpandLayoutHolder.setCropStateChange(cropBorderChange);
    }

    public void updateSelectedFilter(String filterType) {
        mExpandLayoutHolder.updateSelectedFilter(filterType);
    }

//    public static final int ANIM_DURATION = 200;
//    private View mRootView;
//    private ColorPickerHolder mColorPickerHolder;
//    private ExpandLayoutHolder mExpandLayoutHolder;
//    private ToolsLayoutHolder mToolsLayoutHolder;
//    private DrawingBoardView mDrawingBoard;
//    private BitmapsManager mBitmapManager;
//    private DrawInvoker mInvoker;
//    private int mCurColor;
//    private DragMediaResourcePickerView mDragResourceView;
//    private PhotoEditMaterialModel mMaterialModel;
//    private Activity mActivity;
//    private ImageView mIvDraft;
//    private TextView mTvFinish;
//    private TextView mTvCancel;
//
//    public PhotoEditToolsHelper(Activity activity, View rootView, DrawingBoardView boardView,
//                                BitmapsManager bitmapsManager, DrawInvoker invoker) {
//        mActivity = activity;
//        mRootView = rootView;
//        mDrawingBoard = boardView;
//        mBitmapManager = bitmapsManager;
//        mInvoker = invoker;
//        findViews();
//        initHolders();
//        initBitmaps();
//        initListeners();
//    }
//
//    private void findViews() {
//        mDragResourceView = mRootView.findViewById(R.id.drag_resource_picker_view);
//        mIvDraft = mRootView.findViewById(R.id.iv_draft);
//        mTvFinish = mRootView.findViewById(R.id.tv_finish);
//        mTvCancel = mRootView.findViewById(R.id.tv_cancel);
//    }
//
//    private void initHolders() {
//        mColorPickerHolder = new ColorPickerHolder(mRootView, this);
//        mExpandLayoutHolder = new ExpandLayoutHolder(mRootView, this);
//        mToolsLayoutHolder = new ToolsLayoutHolder(mRootView, this);
//        mMaterialModel = new PhotoEditMaterialModel(mDragResourceView);
//    }
//
//    private void initBitmaps() {
//        // 初始化马赛克笔刷的图片
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.texture_test3, options);
//        mBitmapManager.saveBitmap(BitmapsManager.KEY_BRUSH_MOSAICS, bitmap);
//
//        // 初始化 brushList 图片
//        ArrayList<Bitmap> mStickerList = new ArrayList<>();
//        Bitmap mBitmapSticker1 = BitmapFactory.decodeResource(getResources(), R.drawable.ic_second_sticker1);
//        Bitmap mBitmapSticker2 = BitmapFactory.decodeResource(getResources(), R.drawable.ic_second_sticker2);
//        Bitmap mBitmapSticker3 = BitmapFactory.decodeResource(getResources(), R.drawable.ic_second_sticker3);
//        mStickerList.add(mBitmapSticker1);
//        mStickerList.add(mBitmapSticker2);
//        mStickerList.add(mBitmapSticker3);
//        mBitmapManager.saveStickerList(BitmapsManager.KEY_STICKER_LIST, mStickerList);
//    }
//
//    private void initListeners() {
//        // 感觉应该把这些逻辑放到model层里面，在里面做具体的处理，涉及到activity的方法调用，用接口的方式来实现
//        // 目前改不动了，就先这样吧
//        mMaterialModel.setOnResourceItemClickListener(new MediaResourcePickerView.OnResourceItemClickListener() {
//            @Override
//            public void onResourceClick(Data data) {
//                if (!mInvoker.isBitmapStickerCountValidate()) {
//                    ToastModel.showRed(SnsApplication.getInstance(), R.string.material_count_invalidate);
//                    return;
//                }
//                String localPath = ((ResourceData) data).getLocalPath();
//                mDrawingBoard.switchToIdleLayer();
//                Bitmap bitmap = mBitmapManager.getBitmap(localPath);
//                if (bitmap == null) {
//                    bitmap = BitmapFactory.decodeFile(localPath);
//                    mBitmapManager.saveBitmap(localPath, bitmap);
//                }
//                mDrawingBoard.addBitmapStick(localPath);
//                if (data instanceof StickerData) {
//                    mMaterialModel.addStickerToRecentContent((StickerData) data);
//                }
//                mMaterialModel.dismissResourcePicker(0);
//            }
//        });
//
//        mMaterialModel.setOnDismissListener(this);
//
//        mDrawingBoard.setIColorPicker(this);
//        mInvoker.setIColorPicker(this);
//        mDrawingBoard.setStickerStateChangeListener(this);
//    }
//
//    @Override
//    public void onCancelIconClick() {
//        ObjectAnimator animator = mExpandLayoutHolder.getDismissAnim();
//        if (mExpandLayoutHolder.isCropUtilsShown()) {
//            mDrawingBoard.cancelCropMode();
//        } else if (mExpandLayoutHolder.isFilterViewShown()){
//            mDrawingBoard.cancelSetFilter();
//        }
//
//        animator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                startExpandTools();
//                showCancelAndFinish();
//            }
//        });
//        animator.start();
//    }
//
//    @Override
//    public void onDoneIconClick() {
//        if (mExpandLayoutHolder.isCropUtilsShown()) {
//            mDrawingBoard.doneCropMode();
//        } else if (mExpandLayoutHolder.isFilterViewShown()) {
//            mDrawingBoard.doneSetFilter();
//        }
//        ObjectAnimator animator = mExpandLayoutHolder.getDismissAnim();
//        animator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                showCancelAndFinish();
//                startExpandTools();
//            }
//        });
//        animator.start();
//    }
//
//    @Override
//    public void onRestoreIconClick() {
//        mDrawingBoard.restoreCrop();
//    }
//
//    @Override
//    public void onRotateIconClick() {
//        mDrawingBoard.rotateCrop();
//    }
//
//    @Override
//    public void onStrokeWidthChange(int strokeLevel) {
//        mDrawingBoard.onStrokeWidthChange(strokeLevel);
//    }
//
//    @Override
//    public void onColorItemClick(int position, ColorPickerBean bean) {
//        onColorPickerBeanSelect(bean);
//    }
//
//    @Override
//    public void onColorPickerBackIconClick() {
//        mInvoker.undo();
//        mDrawingBoard.requestInvalidate();
//    }
//
//    @Override
//    public void onCropIconClick() {
//        mExpandLayoutHolder.setDoneEnable(false);
//        hideCancelAndFinish();
//        startShrinkToolsAnim(new AnimFinishCallback<Boolean>() {
//            @Override
//            public void onAnimFinish(Boolean object) {
//                Animator animator2 = mExpandLayoutHolder.getShowCropUtilsAnim();
//                animator2.start();
//                mDrawingBoard.startCrop(PhotoEditToolsHelper.this);
//            }
//        });
//    }
//
//    @Override
//    public void onFilterIconClick() {
//        mExpandLayoutHolder.setDoneEnable(false);
//        hideCancelAndFinish();
//        startShrinkToolsAnim(new AnimFinishCallback<Boolean>() {
//            @Override
//            public void onAnimFinish(Boolean object) {
//                Animator animator2 = mExpandLayoutHolder.getShowFilterAnim();
//                animator2.start();
//            }
//        });
//    }
//
//    @Override
//    public void onStickerIconClick() {
//        hideCancelAndFinish();
//        startShrinkToolsAnim(new AnimFinishCallback<Boolean>() {
//            @Override
//            public void onAnimFinish(Boolean object) {
//                mMaterialModel.stickerPickerClick();
//            }
//        });
//    }
//
//    @Override
//    public void onTextIconClick() {
//        hideCancelAndFinish();
//        startShrinkToolsAnim(new AnimFinishCallback<Boolean>() {
//            @Override
//            public void onAnimFinish(Boolean object) {
//                showEditTextFragment(null);
//            }
//        });
//    }
//
//    @Override
//    public void onBrushIconClick() {
//        ObjectAnimator animator;
//        if (mColorPickerHolder.isColorPickerShown()) {
//            animator = mColorPickerHolder.getTranslateDismissAnim();
//        } else {
//            animator = mColorPickerHolder.getTranslateShowAnim();
//            animator.addListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    ColorPickerBean bean = mColorPickerHolder.getColorPickerView().getSelectedBean();
//                    onColorPickerBeanSelect(bean);
//                }
//            });
//        }
//
//        animator.start();
//    }
//
//    /**
//     * 开始动画的方法，这里主要的问题是当colorPicker展示的时候，我们会需要把 colorPicker收起来，再开始别的
//     * 动画；而colorPicker不展示的时候，我们需要把 tools 先收起来。
//     * 这个方法就是执行收起来这一步，当收起来这一步结束之后，开始回调callback的onAnimFinish方法
//     * */
//    private void startShrinkToolsAnim(final AnimFinishCallback<Boolean> callback) {
//        ObjectAnimator dismissColorPickerAnim = null;
//        int translationY;
//
//        if (mColorPickerHolder.isColorPickerShown()) {
//            translationY = mColorPickerHolder.getColorPickerHeight() + mToolsLayoutHolder.getLlToolHeight();
//            dismissColorPickerAnim = mColorPickerHolder.getTranslateDismissAnim(translationY);
//        } else {
//            translationY = mToolsLayoutHolder.getLlToolHeight();
//        }
//
//        ObjectAnimator dismissToolsAnim = mToolsLayoutHolder.getDismissAnim(translationY);
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
//    /**
//     * 展开 tools 的动画
//     * */
//    public void startExpandTools() {
//        ObjectAnimator animator = mToolsLayoutHolder.getShowAnim();
//        animator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                mDrawingBoard.switchToIdleLayer();
//            }
//        });
//        animator.start();
//    }
//
//    /**
//     * 颜色选择器的icon被点击之后具体的处理逻辑
//     * */
//    private void onColorPickerBeanSelect(ColorPickerBean bean) {
//        if (bean == null) return;
//        mColorPickerHolder.getStrokeUtilsView().setVisibility(View.GONE);
//        switch (bean.getType()) {
//            case IPhotoEditType.BRUSH_NORMAL_COLOR:
//                if ((mCurColor != getResources().getColor(bean.getColorRes()))) {
//                    mCurColor = getResources().getColor(bean.getColorRes());
//                }
//                // 在brushNormal的时候，显示strokeUtil，其他情况都为gone
//                mColorPickerHolder.getStrokeUtilsView().setVisibility(View.VISIBLE);
//                mDrawingBoard.setNormalBrushMode(mCurColor);
//                break;
//
//            case IPhotoEditType.BRUSH_LIGHT_COLOR:
//                mDrawingBoard.setLightBrushMode();
//                break;
//
//            case IPhotoEditType.BRUSH_BLOCK_MOSAICS:
//                mDrawingBoard.setBlockMosaicsMode();
//                break;
//
//            case IPhotoEditType.BRUSH_MOSAICS:
//                mDrawingBoard.setBrushMosaicsMode(BitmapsManager.KEY_BRUSH_MOSAICS);
//                break;
//
//            case IPhotoEditType.BRUSH_STICKERS:
//                mDrawingBoard.setStickerListMode(BitmapsManager.KEY_STICKER_LIST);
//                break;
//
//        }
//    }
//
//    private Resources getResources() {
//        return SnsApplication.getInstance().getResources();
//    }
//
//    /**
//     * 展示编辑页面
//     * */
//    private void showEditTextFragment(TextSticker textSticker) {
//        EditTextDialogFragment fragment = new EditTextDialogFragment(mActivity, mInvoker, textSticker);
//        fragment.setOnEditDoneCallback(this);
//        FragmentTransaction ft = mActivity.getFragmentManager().beginTransaction();
//        Fragment prev = mActivity.getFragmentManager().findFragmentByTag("dialog");
//        if (prev != null) {
//            ft.remove(prev);
//        }
//        ft.addToBackStack(null);
//
//        // Create and show the dialog.
//        fragment.show(ft, "dialog");
//    }
//
//    public void onFinish() {
//        mMaterialModel.finish();
//    }
//
//    private void hideCancelAndFinish() {
//        mTvCancel.setVisibility(View.GONE);
//        mTvFinish.setVisibility(View.GONE);
//    }
//
//    private void showCancelAndFinish() {
//        mTvCancel.setVisibility(View.VISIBLE);
//        mTvFinish.setVisibility(View.VISIBLE);
//    }
//
//    public void updateSelectedFilter(String filterType) {
//        mExpandLayoutHolder.updateSelectedFilter(filterType);
//    }
//
//    @Override
//    public void onEditDone(String oldText, String newText, TextSticker textSticker, int newColor, int oldColor) {
//        if (textSticker != null) {
//            // 这个时候是点击sticker进入的，sticker已经存在
//            textSticker.setIsEditing(false);
//            mInvoker.updateTextSticker(oldText, newText, textSticker, newColor, oldColor);
//            mDrawingBoard.requestInvalidate();
//            mDrawingBoard.switchToIdleLayer();
//        } else {
//            // 添加图片到 bitmapsManager
//            Bitmap bitmap = BitmapUtils.createBitmapFromString(mActivity, newText, newColor);
//            mBitmapManager.saveBitmap(BitmapsManager.generateTextStickerUri(newText, newColor), bitmap);
//            // sticker为空说明需要添加一个新的sticker，同时添加一张bitmap
//            mDrawingBoard.addTextSticker(oldText, BitmapsManager.generateTextStickerUri(oldText, newColor), newColor);
//            mDrawingBoard.switchToIdleLayer();
//        }
//        startExpandTools();
//        showCancelAndFinish();
//    }
//
//    @Override
//    public void onCropStateChange(boolean cropBorderChange) {
//        if (mExpandLayoutHolder != null) {
//            mExpandLayoutHolder.setCropStateChange(cropBorderChange);
//        }
//    }
//
//    @Override
//    public void onFilterItemClick(View view, int position, FilterData data) {
//        if (data == null) return;
//        if (!mDrawingBoard.getCurFilterType().equals(data.filterType)){
//            if (mExpandLayoutHolder != null) {
//                mExpandLayoutHolder.setDoneEnable(true);
//            }
//            mDrawingBoard.setFilter(data.filterType);
//        }
//    }
//
//    @Override
//    public void showColorPicker(int delay) {
//        mColorPickerHolder.showDelay(delay);
//    }
//
//    @Override
//    public void dismissColorPicker(int delay) {
//        mColorPickerHolder.dismissDelay(delay);
//    }
//
//    @Override
//    public void onUndoStateChange(boolean canUndo) {
//        mColorPickerHolder.onUndoStateChange(canUndo);
//    }
//
//    @Override
//    public void onStickerClick(final BaseSticker sticker) {
//        // 只有 textSticker 可以接受点击事件
//        if (!(sticker instanceof TextSticker)) return;
//        if (mColorPickerHolder.isColorPickerShown()) {
//            startShrinkToolsAnim(new AnimFinishCallback<Boolean>() {
//                @Override
//                public void onAnimFinish(Boolean object) {
//                    ((TextSticker) sticker).setIsEditing(true);
//                    showEditTextFragment((TextSticker) sticker);
//                }
//            });
//        } else {
//            ((TextSticker) sticker).setIsEditing(true);
//            showEditTextFragment((TextSticker) sticker);
//        }
//    }
//
//    @Override
//    public void onStickerPressStateChange(BaseSticker sticker, boolean pressed) {
//        if (pressed) {
//            mIvDraft.setVisibility(View.VISIBLE);
//        } else {
//            mIvDraft.setVisibility(View.GONE);
//        }
//    }
//
//    @Override
//    public void onStickerDeleteStateChange(BaseSticker sticker, boolean isDeletable) {
//        if (isDeletable) {
//            mIvDraft.setBackgroundResource(R.color.Red_1);
//        } else {
//            mIvDraft.setBackgroundResource(R.color.white);
//        }
//    }
//
//    @Override
//    public void onMaterialPickerClose() {
//        if (mExpandLayoutHolder != null) {
//            startExpandTools();
//            showCancelAndFinish();
//        }
//    }
}
