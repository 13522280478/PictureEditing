package com.biotech.drawlessons.photoedit.utilsmodel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.biotech.drawlessons.R;


/**
 * Created by xintu on 2018/3/27.
 */

public class ToolsLayoutHolder implements View.OnClickListener {
    private View mParentView;
    private LinearLayout mLlTools;
    private RelativeLayout mRlCrop;
    private RelativeLayout mRlFilter;
    private RelativeLayout mRlSticker;
    private RelativeLayout mRlText;
    private RelativeLayout mRlBrush;
    private IPhotoEditActivityTools mITools;
    private TextView mTvBrush;
    private int mLlToolHeight;

    public ToolsLayoutHolder(View parent, IPhotoEditActivityTools iTools) {
        mParentView = parent;
        mITools = iTools;
        findViews();
        initListener();
        mLlToolHeight = (int) parent.getResources().getDimension(R.dimen.edit_tool_height);
    }

    private void findViews() {
        mLlTools = mParentView.findViewById(R.id.ll_edit_tools);
        mRlCrop = mParentView.findViewById(R.id.rl_crop);
        mRlFilter = mParentView.findViewById(R.id.rl_filter);
        mRlSticker = mParentView.findViewById(R.id.rl_sticker);
        mRlText = mParentView.findViewById(R.id.rl_text);
        mRlBrush = mParentView.findViewById(R.id.rl_brush);
        mTvBrush = mParentView.findViewById(R.id.tv_brush);
    }

    private void initListener() {
        mRlCrop.setOnClickListener(this);
        mRlFilter.setOnClickListener(this);
        mRlSticker.setOnClickListener(this);
        mRlText.setOnClickListener(this);
        mRlBrush.setOnClickListener(this);
    }

    public int getLlToolHeight() {
        return mLlToolHeight;
    }

    public ObjectAnimator getDismissAnim() {
        return getDismissAnim(mLlToolHeight);
    }

    public ObjectAnimator getDismissAnim(float distance) {
        ObjectAnimator animator = ObjectAnimator
                .ofFloat(mLlTools, "translationY", 0, distance)
                .setDuration(PhotoEditToolsHelper.ANIM_DURATION);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLlTools.setVisibility(View.GONE);
            }
        });
        return animator;
    }

    public ObjectAnimator getShowAnim() {
        ObjectAnimator animator = ObjectAnimator
                .ofFloat(mLlTools, "translationY", mLlToolHeight, 0)
                .setDuration(PhotoEditToolsHelper.ANIM_DURATION);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mLlTools.setVisibility(View.VISIBLE);
            }
        });
        return animator;
    }

    public void setBrushClickState() {
        mTvBrush.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_brush_selected_light, 0, 0);
    }

    public void setBrushUnClickState() {
        mTvBrush.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_brush_un_selected_light, 0, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_crop:
                mITools.onCropIconClick();
                break;

            case R.id.rl_filter:
                mITools.onFilterIconClick();
                break;

            case R.id.rl_sticker:
                mITools.onStickerIconClick();
                break;

            case R.id.rl_text:
                mITools.onTextIconClick();
                break;

            case R.id.rl_brush:
                mITools.onBrushIconClick();
                break;
        }
    }

    public LinearLayout getLlTools() {
        return mLlTools;
    }

    public RelativeLayout getRlCrop() {
        return mRlCrop;
    }

    public RelativeLayout getRlFilter() {
        return mRlFilter;
    }

    public RelativeLayout getRlSticker() {
        return mRlSticker;
    }

    public RelativeLayout getRlText() {
        return mRlText;
    }

    public RelativeLayout getRlBrush() {
        return mRlBrush;
    }
}
