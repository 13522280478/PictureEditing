package com.biotech.drawlessons.photoedit.utilsmodel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;


import com.biotech.drawlessons.BaseApplication;
import com.biotech.drawlessons.R;
import com.biotech.drawlessons.photoedit.draws.BaseSticker;
import com.biotech.drawlessons.photoedit.draws.StickerData;
import com.biotech.drawlessons.photoedit.draws.TextSticker;
import com.biotech.drawlessons.photoedit.layers.StickerLayer;
import com.biotech.drawlessons.photoedit.resourcepicker.DragMediaResourcePickerView;
import com.biotech.drawlessons.photoedit.resourcepicker.MediaResourcePickerView;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.model.Data;
import com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.model.FilterData;
import com.biotech.drawlessons.photoedit.test.FakeData;
import com.biotech.drawlessons.photoedit.tools.ColorPickerBean;
import com.biotech.drawlessons.photoedit.utils.AnimFinishCallback;
import com.biotech.drawlessons.photoedit.utils.BitmapUtils;
import com.biotech.drawlessons.photoedit.utils.BitmapsManager;
import com.biotech.drawlessons.photoedit.utils.DrawInvoker;
import com.biotech.drawlessons.photoedit.utils.EditTextDialogFragment;
import com.biotech.drawlessons.photoedit.utils.IPhotoEditType;
import com.biotech.drawlessons.photoedit.views.DrawingBoardView;

import java.util.ArrayList;


/**
 * Created by xintu on 2018/3/27.
 */

public abstract class AbsToolsHelper implements IColorPicker, EditTextDialogFragment.OnEditDoneCallback, StickerLayer.StickerStateListener, IBaseTools {
    public static final int ANIM_DURATION = 200;
    protected View mRootView;
    protected Activity mActivity;
    protected ImageView mIvDraft;

    protected ColorPickerHolder mColorPickerHolder;
    protected DragMediaResourcePickerView mDragResourceView;
//    protected PhotoEditMaterialModel mMaterialModel;

    protected DrawingBoardView mDrawingBoard;
    protected BitmapsManager mBitmapManager;
    protected DrawInvoker mInvoker;

    protected int mCurColor;

    public AbsToolsHelper(Activity activity, View rootView, DrawingBoardView boardView,
                          BitmapsManager bitmapsManager, DrawInvoker invoker) {
        mActivity = activity;
        mRootView = rootView;
        mDrawingBoard = boardView;
        mBitmapManager = bitmapsManager;
        mInvoker = invoker;
        findViews();
        initHolders();
        initBitmaps();
        initListeners();
    }

    abstract void startShrinkToolsAnim(AnimFinishCallback<Boolean> callback);

    abstract void startExpandTools();

    abstract boolean dealBackEvent();

    private void findViews() {
        mDragResourceView = mRootView.findViewById(R.id.drag_resource_picker_view);
        mIvDraft = mRootView.findViewById(R.id.iv_draft);
    }

    private void initHolders() {
        mColorPickerHolder = new ColorPickerHolder(mRootView, this);
//        mMaterialModel = new PhotoEditMaterialModel(mDragResourceView);
    }

    private void initBitmaps() {
        // 初始化马赛克笔刷的图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_brush_mosaics, options);
        mBitmapManager.saveBitmap(BitmapsManager.KEY_BRUSH_MOSAICS, bitmap);

        // 初始化 brushList 图片
        ArrayList<Bitmap> mStickerList = new ArrayList<>();
        Bitmap mBitmapSticker1 = BitmapFactory.decodeResource(getResources(), R.drawable.ic_second_sticker1);
        Bitmap mBitmapSticker2 = BitmapFactory.decodeResource(getResources(), R.drawable.ic_second_sticker2);
        Bitmap mBitmapSticker3 = BitmapFactory.decodeResource(getResources(), R.drawable.ic_second_sticker3);
        mStickerList.add(mBitmapSticker1);
        mStickerList.add(mBitmapSticker2);
        mStickerList.add(mBitmapSticker3);
        mBitmapManager.saveStickerList(BitmapsManager.KEY_STICKER_LIST, mStickerList);

        // 初始化 backgroundBrush 图片
        Bitmap backgroundBrush1 = BitmapFactory.decodeResource(getResources(), R.drawable.bg_first_background_brush);
        Bitmap backgroundBrush2 = BitmapFactory.decodeResource(getResources(), R.drawable.bg_second_background_brush);
        mBitmapManager.saveBitmap(BitmapsManager.KEY_FIRST_BACKGROUND_BRUSH, backgroundBrush1);
        mBitmapManager.saveBitmap(BitmapsManager.KEY_SECOND_BACKGROUND_BRUSH, backgroundBrush2);
    }

    private void initListeners() {
        // 感觉应该把这些逻辑放到model层里面，在里面做具体的处理，涉及到activity的方法调用，用接口的方式来实现
        // 目前改不动了，就先这样吧
//        mMaterialModel.setOnResourceItemClickListener(new MediaResourcePickerView.OnResourceItemClickListener() {
//            @Override
//            public void onResourceClick(Data data) {
//                if (data instanceof StickerData) {
//                    onStickerDataClick((StickerData) data);
//                } else if (data instanceof PhotoFrameData) {
//                    onPhotoFrameDataClick((PhotoFrameData) data);
//                } else if (data instanceof FilterData) {
//                    onFilterDataClick((FilterData) data);
//                }
//            }
//
//            @Override
//            public void onClearResourceClick() {
//                clearPhotoFrameAndFilter();
//            }
//        });
//
//        mMaterialModel.setOnDismissListener(this);

        mDrawingBoard.setIColorPicker(this);
        mInvoker.setIColorPicker(this);
        mDrawingBoard.setStickerStateChangeListener(this);
    }

    protected void onStickerDataClick(StickerData data) {
        if (!mInvoker.isBitmapStickerCountValidate()) {
//            ToastModel.showRed(SnsApplication.getInstance(), R.string.material_count_invalidate);
            return;
        }
//        String localPath = data.getLocalPath();
//        mDrawingBoard.switchToIdleLayer();
//        Bitmap bitmap = mBitmapManager.getBitmap(localPath);
//        if (bitmap == null) {
//            bitmap = BitmapFactory.decodeFile(localPath);
//            mBitmapManager.saveBitmap(localPath, bitmap);
//        }
//        mDrawingBoard.addBitmapStick(localPath);
//        mMaterialModel.addStickerToRecentContent(data);
//        mMaterialModel.dismissResourcePicker(0);
    }

//    protected void onPhotoFrameDataClick(PhotoFrameData data) {
//        if (data == null) return;
//        mMaterialModel.addBeautyContentToRecentContent(data);
//        String localPath = data.getLocalPath();
//        mDrawingBoard.switchToIdleLayer();
//        mDrawingBoard.setPhotoFrame(localPath);
//    }
//
//    protected void onFilterDataClick(FilterData data) {
//        if (data == null) return;
//        mMaterialModel.addBeautyContentToRecentContent(data);
//        mDrawingBoard.setFilter(data.filterType);
//    }

    private void clearPhotoFrameAndFilter() {
        mDrawingBoard.clearPhotoFrame();
        mDrawingBoard.setFilter(FakeData.ORIGINAL_FILTER_TYPE);
    }

    @Override
    public void onStickerIconClick() {
        startShrinkToolsAnim(new AnimFinishCallback<Boolean>() {
            @Override
            public void onAnimFinish(Boolean object) {
//                mMaterialModel.stickerPickerClick();
            }
        });
    }

    @Override
    public void onTextIconClick() {
        startShrinkToolsAnim(new AnimFinishCallback<Boolean>() {
            @Override
            public void onAnimFinish(Boolean object) {
                showEditTextFragment(null);
            }
        });
    }

    @Override
    public void onBrushIconClick() {
        ObjectAnimator animator;
        if (mColorPickerHolder.isColorPickerShown()) {
            animator = mColorPickerHolder.getTranslateDismissAnim();
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mDrawingBoard.switchToIdleLayer();
                }
            });
        } else {
            animator = mColorPickerHolder.getTranslateShowAnim();
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ColorPickerBean bean = mColorPickerHolder.getColorPickerView().getSelectedBean();
                    onColorPickerBeanSelect(bean);
                }
            });
        }

        animator.start();
    }

    @Override
    public void onStrokeWidthChange(int strokeLevel) {
        mDrawingBoard.onStrokeWidthChange(strokeLevel);
    }

    @Override
    public void onColorItemClick(int position, ColorPickerBean bean) {
        onColorPickerBeanSelect(bean);
    }

    @Override
    public void onColorPickerBackIconClick() {
        mInvoker.undo();
        mDrawingBoard.requestInvalidate();
    }

    protected boolean dealMaterialPickerClose() {
//        if (mMaterialModel.isResourcePickerShown()) {
//            mMaterialModel.onClose(0);
//            return true;
//        }

        return false;
    }

    /**
     * 颜色选择器的icon被点击之后具体的处理逻辑
     */
    protected void onColorPickerBeanSelect(ColorPickerBean bean) {
        if (bean == null) {
            return;
        }
        mColorPickerHolder.getStrokeUtilsView().setVisibility(View.GONE);
        switch (bean.getType()) {
            case IPhotoEditType.BRUSH_NORMAL_COLOR:
                if ((mCurColor != getResources().getColor(bean.getColorRes()))) {
                    mCurColor = getResources().getColor(bean.getColorRes());
                }
                // 在brushNormal的时候，显示strokeUtil，其他情况都为gone
                mColorPickerHolder.getStrokeUtilsView().setVisibility(View.VISIBLE);
                mDrawingBoard.setNormalBrushMode(mCurColor);
                break;

            case IPhotoEditType.BRUSH_LIGHT_COLOR:
                mDrawingBoard.setLightBrushMode();
                break;

            case IPhotoEditType.BRUSH_BLOCK_MOSAICS:
                mDrawingBoard.setBlockMosaicsMode();
                break;

            case IPhotoEditType.BRUSH_MOSAICS:
                mDrawingBoard.setBrushMosaicsMode(BitmapsManager.KEY_BRUSH_MOSAICS);
                break;

            case IPhotoEditType.BRUSH_STICKERS:
                mDrawingBoard.setStickerListMode(BitmapsManager.KEY_STICKER_LIST);
                break;

            case IPhotoEditType.BRUSH_BACKGROUND:
                mDrawingBoard.setBackgroundBrushMode(bean.getBitmapType());
                break;

        }
    }

    private Resources getResources() {
        return BaseApplication.getInstance().getResources();
    }

    /**
     * 展示编辑页面
     */
    protected void showEditTextFragment(TextSticker textSticker) {
        EditTextDialogFragment fragment = new EditTextDialogFragment(mActivity, mInvoker, textSticker);
        fragment.setOnEditDoneCallback(this);
        FragmentTransaction ft = mActivity.getFragmentManager().beginTransaction();
        Fragment prev = mActivity.getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        fragment.show(ft, "dialog");
    }

    public void onFinish() {
//        mMaterialModel.finish();
    }

    public void onMaterialPickerClose() {
        startExpandTools();
    }

    @Override
    public void onEditDone(String oldText, String newText, TextSticker textSticker, int newColor, int oldColor) {
        if (textSticker != null) {
            // 这个时候是点击sticker进入的，sticker已经存在
            textSticker.setIsEditing(false);
            mInvoker.updateTextSticker(oldText, newText, textSticker, newColor, oldColor);
            mDrawingBoard.requestInvalidate();
            mDrawingBoard.switchToIdleLayer();
        } else {
            // 添加图片到 bitmapsManager
            Bitmap bitmap = BitmapUtils.createBitmapFromString(mActivity, newText, newColor);
            mBitmapManager.saveBitmap(BitmapsManager.generateTextStickerUri(newText, newColor), bitmap);
            // sticker为空说明需要添加一个新的sticker，同时添加一张bitmap
            mDrawingBoard.addTextSticker(oldText, BitmapsManager.generateTextStickerUri(oldText, newColor), newColor);
            mDrawingBoard.switchToIdleLayer();
        }
        startExpandTools();
    }

    public void dismissColorPickerWithoutAnim() {
        mColorPickerHolder.dismissWithoutAnim();
    }

    @Override
    public void showColorPicker(int delay) {
        mColorPickerHolder.showDelay(delay);
    }

    @Override
    public void dismissColorPicker(int delay) {
        mColorPickerHolder.dismissDelay(delay);
    }

    @Override
    public void onUndoStateChange(boolean canUndo) {
        mColorPickerHolder.onUndoStateChange(canUndo);
    }

    @Override
    public void onStickerClick(final BaseSticker sticker) {
        // 只有 textSticker 可以接受点击事件
        if (!(sticker instanceof TextSticker)) {
            return;
        }
        if (mColorPickerHolder.isColorPickerShown()) {
            startShrinkToolsAnim(new AnimFinishCallback<Boolean>() {
                @Override
                public void onAnimFinish(Boolean object) {
                    ((TextSticker) sticker).setIsEditing(true);
                    showEditTextFragment((TextSticker) sticker);
                }
            });
        } else {
            ((TextSticker) sticker).setIsEditing(true);
            showEditTextFragment((TextSticker) sticker);
        }
    }

    @Override
    public void onStickerPressStateChange(BaseSticker sticker, boolean pressed) {
        if (pressed) {
            mIvDraft.setVisibility(View.VISIBLE);
        } else {
            mIvDraft.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStickerDeleteStateChange(BaseSticker sticker, boolean isDeletable) {
        if (isDeletable) {
            mIvDraft.setBackgroundResource(R.color.Red_1);
        } else {
            mIvDraft.setBackgroundResource(R.color.white);
        }
    }
}
