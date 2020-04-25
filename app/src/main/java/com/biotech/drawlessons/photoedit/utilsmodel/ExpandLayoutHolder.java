package com.biotech.drawlessons.photoedit.utilsmodel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.biotech.drawlessons.R;
import com.biotech.drawlessons.UtilsKt;
import com.biotech.drawlessons.photoedit.tools.FilterAdapter;
import com.biotech.drawlessons.photoedit.tools.HorizontalItemDecoration;
import com.biotech.drawlessons.photoedit.views.IconTextView;


/**
 * Created by xintu on 2018/3/27.
 */

public class ExpandLayoutHolder implements View.OnClickListener {
    private View mParentView;
    private LinearLayout mLlExpandUtils;
    private LinearLayout mLlCropUtils;
    private IconTextView mTvRestore;
    private IconTextView mTvRotate;
    private ImageView mIvCancel, mIvDone;
    private RecyclerView mFilterView;
    private IExpandLayout mIExpand;
    private int mLlConfirmHeight, mLlCropUtilHeight, mFilterViewHeight;
    private FilterAdapter mAdapter;

    public ExpandLayoutHolder(View rootView, IExpandLayout iExpand) {
        mParentView = rootView;
        mIExpand = iExpand;
        findViews();
        initListener();
        initHeight();
        initDatas();
    }

    private void findViews() {
        mLlExpandUtils = mParentView.findViewById(R.id.ll_expand_utils);
        mLlCropUtils = mParentView.findViewById(R.id.ll_crop_utils);

        mIvCancel = mParentView.findViewById(R.id.iv_cancel);
        mIvDone = mParentView.findViewById(R.id.iv_done);
        mTvRestore = mParentView.findViewById(R.id.tv_restore);
        mTvRestore.setDrawable(IconTextView.LEFT, R.drawable.ic_clear_light, R.drawable.ic_clear_light);
        mTvRestore.setAlpha(0.3f);
        mTvRotate = mParentView.findViewById(R.id.tv_rotate);
        mTvRotate.setDrawable(IconTextView.LEFT, R.drawable.ic_rotation_light, R.drawable.ic_rotation_light);

        mFilterView = mParentView.findViewById(R.id.rv_filter_tools);
    }

    private void initListener() {
        mIvCancel.setOnClickListener(this);
        mIvDone.setOnClickListener(this);
        mTvRestore.setOnClickListener(this);
        mTvRotate.setOnClickListener(this);
    }

    private void initHeight() {
        mLlCropUtilHeight = (int) mParentView.getResources().getDimension(R.dimen.ll_crop_rotate_height);
        mFilterViewHeight = (int) mParentView.getResources().getDimension(R.dimen.filter_list_view_height);
        mLlConfirmHeight = (int) (mParentView.getResources().getDimension(R.dimen.rl_crop_confirm_height) + 1); // 1px 是分割线
    }

    private void initDatas() {
        mAdapter = new FilterAdapter(mParentView.getContext());
        mFilterView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(mIExpand);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mParentView.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mFilterView.setLayoutManager(linearLayoutManager);
        mFilterView.setItemAnimator(null);
        mFilterView.addItemDecoration(
                new HorizontalItemDecoration((int)UtilsKt.dp2px(12),
                        (int)UtilsKt.dp2px(14), true));
    }

    public ObjectAnimator getDismissAnim() {
        int viewHeight = mLlConfirmHeight;
        if (mFilterView.getVisibility() == View.VISIBLE) {
            viewHeight += mFilterViewHeight;
        } else if (mLlCropUtils.getVisibility() == View.VISIBLE) {
            viewHeight += mLlCropUtilHeight;
        }

        ObjectAnimator animator = ObjectAnimator
                .ofFloat(mLlExpandUtils, "translationY", 0, viewHeight)
                .setDuration(PhotoEditToolsHelper.ANIM_DURATION);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLlExpandUtils.setVisibility(View.GONE);
            }
        });

        return animator;
    }

    public ObjectAnimator getShowCropUtilsAnim() {
        ObjectAnimator animator = ObjectAnimator
                .ofFloat(mLlExpandUtils, "translationY",
                        mLlConfirmHeight + mLlCropUtilHeight, 0)
                .setDuration(PhotoEditToolsHelper.ANIM_DURATION);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mLlExpandUtils.setVisibility(View.VISIBLE);
                mLlCropUtils.setVisibility(View.VISIBLE);
                mFilterView.setVisibility(View.GONE);
            }
        });

        return animator;
    }

    public ObjectAnimator getShowFilterAnim() {
        ObjectAnimator animator = ObjectAnimator
                .ofFloat(mLlExpandUtils, "translationY",
                        mLlConfirmHeight + mFilterViewHeight, 0)
                .setDuration(PhotoEditToolsHelper.ANIM_DURATION);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mLlExpandUtils.setVisibility(View.VISIBLE);
                mLlCropUtils.setVisibility(View.GONE);
                mFilterView.setVisibility(View.VISIBLE);
            }
        });

        return animator;
    }

    public void setCropStateChange(boolean cropBorderChange) {
        if (cropBorderChange) {
            setRestoreEnable(true);
            setDoneEnable(true);
        } else {
            setRestoreEnable(false);
//            mIvDone.setAlpha(0.3f);
//            mIvDone.setEnabled(false);
        }
    }

    public void setDoneEnable(boolean enable) {
        mIvDone.setAlpha(enable ? 1f : 0.3f);
        mIvDone.setEnabled(enable);
    }

    public void setRestoreEnable(boolean enable) {
        mTvRestore.setAlpha(enable ? 1f : 0.3f);
        mTvRestore.setEnabled(enable);
    }

    public boolean isShown() {
        return mLlExpandUtils.getVisibility() == View.VISIBLE;
    }

    public boolean isCropUtilsShown() {
        return mLlCropUtils.getVisibility() == View.VISIBLE;
    }

    public boolean isFilterViewShown() {
        return mFilterView.getVisibility() == View.VISIBLE;
    }

    public void updateSelectedFilter(String filterType) {
        mAdapter.selectFilter(filterType);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cancel:
                mIExpand.onCancelIconClick();
                break;

            case R.id.iv_done:
                mIExpand.onDoneIconClick();
                break;

            case R.id.tv_restore:
                mIExpand.onRestoreIconClick();
                break;

            case R.id.tv_rotate:
                mIExpand.onRotateIconClick();
                break;
        }
    }
}
