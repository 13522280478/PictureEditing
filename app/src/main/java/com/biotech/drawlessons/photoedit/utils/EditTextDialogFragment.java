package com.biotech.drawlessons.photoedit.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.biotech.drawlessons.BaseApplication;
import com.biotech.drawlessons.R;
import com.biotech.drawlessons.UtilsKt;
import com.biotech.drawlessons.photoedit.draws.TextSticker;
import com.biotech.drawlessons.photoedit.tools.ColorPickerAdapter;
import com.biotech.drawlessons.photoedit.tools.ColorPickerBean;
import com.biotech.drawlessons.photoedit.tools.ColorPickerView;
import com.biotech.drawlessons.photoedit.tools.HorizontalItemDecoration;
import com.biotech.drawlessons.photoedit.utilsmodel.PhotoEditToolsHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xintu on 2018/3/25.
 */

@SuppressLint("ValidFragment")
public class EditTextDialogFragment extends DialogFragment implements View.OnClickListener, ColorPickerView.OnColorPickerListener {
    private Context mContext;
    private EditText mEditText;
    private RelativeLayout mRootView;
    private RecyclerView mRecyclerView;
    private TextView mTvCancel;
    private TextView mTvDone;
    private ColorPickerAdapter mAdapter;
    private List<ColorPickerBean> mData;
    private OnEditDoneCallback mDoneCallback;
    private int mSelectedColor, mOldColor;
    private TextSticker mTextSticker;
    private DrawInvoker mInvoker;
    private TextWatcher watcher = new TextWatcher() {
        String strBefore;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            strBefore = s.toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            int textLength = getTotalTextLength(s.toString());
            if (!isTextLengthValidate(textLength)) {
                //todo::: toast
//                ToastModel.showRed(mContext, R.string.total_text_length_invalidate);
                mEditText.setText(strBefore);
                int selection = mEditText.getText().length() - 1;
                if (selection < 0) {
                    return;
                }
                mEditText.setSelection(selection);
            }
        }
    };

    public EditTextDialogFragment(Context context, DrawInvoker drawInvoker, TextSticker sticker) {
        mContext = context;
        mInvoker = drawInvoker;
        mTextSticker = sticker;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.dialog_full_screen_loading);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
        View viewRoot = inflater.inflate(R.layout.layout_edit_text_window, container, false);
        findViews(viewRoot);
        initData();
        initListener();
        if (mTextSticker != null) {
            mEditText.setText(mTextSticker.getText());
            mOldColor = mTextSticker.getColor();
            mSelectedColor = mOldColor;
            mAdapter.setColorSelected(mSelectedColor);
            mEditText.setTextColor(mSelectedColor);
        } else {
            mEditText.setText("");
            mAdapter.setColorSelected(mSelectedColor);
        }
        setRootViewGlobalListener();
        return viewRoot;
    }

    private void setRootViewGlobalListener() {
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                boolean prepared = (mRootView.getHeight() > 0)
                        && (getResources().getDisplayMetrics().widthPixels - mRootView.getHeight() > getResources().getDisplayMetrics().heightPixels * 0.15);
                if (!prepared) {
                    return;
                }

                startShowAnim();

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mRootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    mRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                window.setBackgroundDrawable(BaseApplication.getInstance().getResources().getDrawable(R.drawable.transparent));
                // 插件导致直接使用xml动画失效，没有办法，这里直接写一个view的动画
                window.setWindowAnimations(R.style.edit_window_anim);
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                        | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }

            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                        startDismissAnim();
                        return true;
                    }

                    return false;
                }
            });
        }
    }

    private void findViews(View viewRoot) {
        mRootView = viewRoot.findViewById(R.id.root_view);
        mEditText = viewRoot.findViewById(R.id.edit_text);
        mEditText.setHorizontallyScrolling(false);
        mEditText.setMaxLines(Integer.MAX_VALUE);
        mRecyclerView = viewRoot.findViewById(R.id.rv_colors);
        mTvCancel = viewRoot.findViewById(R.id.tv_cancel);
        mTvDone = viewRoot.findViewById(R.id.tv_finish);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.addItemDecoration(
                new HorizontalItemDecoration((int) UtilsKt.dp2px(10),
                        (int)UtilsKt.dp2px(11), true)
        );
    }

    private void initData() {
        int[] mColorList = new int[]{
                R.color.white,
                R.color.Blk_1,
                R.color.color_picker_red,
                R.color.color_picker_orange,
                R.color.color_picker_yellow,
                R.color.color_picker_green,
                R.color.color_picker_blue,
                R.color.color_picker_violet};

        mData = new ArrayList<ColorPickerBean>();
        for (int color : mColorList) {
            mData.add(new ColorPickerBean(IPhotoEditType.BRUSH_NORMAL_COLOR, false, color));
        }
        mData.get(0).setClick(true);
        mAdapter = new ColorPickerAdapter(mContext, mData);
        mRecyclerView.setAdapter(mAdapter);

        mSelectedColor = Color.WHITE;
        mOldColor = Color.WHITE;
        mEditText.setTextColor(mSelectedColor);
    }

    private void initListener() {
        mAdapter.setOnItemClickListener(this);
        mRootView.setOnClickListener(this);
        mTvDone.setOnClickListener(this);
        mTvCancel.setOnClickListener(this);
        mEditText.addTextChangedListener(watcher);
    }

    @Override
    public void onDestroyView() {
        if (mEditText != null) {
            mEditText.addTextChangedListener(null);
        }
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.root_view:
                mEditText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
                try {
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mEditText, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.tv_cancel:
                startDismissAnim();
                break;

            case R.id.tv_finish:
                startDismissAnim();
                break;
        }
    }

    public void setOnEditDoneCallback(OnEditDoneCallback callback) {
        mDoneCallback = callback;
    }

    @Override
    public void dismiss() {
        prepareFinish();
        super.dismiss();
    }

    private void prepareFinish() {
        if (mContext != null && mContext instanceof Activity && !((Activity) mContext).isFinishing()) {
            if (mDoneCallback != null) {
                String text = mEditText.getText().toString();
                if (mTextSticker != null) {
                    String oldText = mTextSticker.getText();
                    mDoneCallback.onEditDone(oldText, text, mTextSticker, mSelectedColor, mOldColor);
                } else {
                    mDoneCallback.onEditDone(text, text, null, mSelectedColor, mOldColor);
                }
            }
            mEditText.setText("");
            mEditText.setInputType(InputType.TYPE_NULL);
        }
    }

    private void startDismissAnim() {
        hideKeyboard();
        ObjectAnimator animator = ObjectAnimator.ofFloat(mRootView, "translationY", 0, mRootView.getHeight());
        animator.setDuration(PhotoEditToolsHelper.ANIM_DURATION);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                dismiss();
            }
        });
        animator.start();
    }

    private void startShowAnim() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mRootView, "translationY", mRootView.getHeight(), 0);
        animator.setDuration(PhotoEditToolsHelper.ANIM_DURATION);
        animator.start();
    }

    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0, null);
        } catch (Exception e) {
            if (mEditText != null) {
                mEditText.setInputType(InputType.TYPE_NULL);
            }
            e.printStackTrace();
        }
    }

//    public void dismissAndHideKeyboard(final boolean done) {
//        try {
//            int rootViewHeight = mRootView.getHeight();
//            if (Global.screenHeight - rootViewHeight > Global.screenHeight * 0.15) {
//                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0, null);
//            }
//
//            if (mContext != null && mContext instanceof Activity && !((Activity) mContext).isFinishing()) {
//                if (done && mDoneCallback != null) {
//                    String text = mEditText.getText().toString();
//                    if (mTextSticker != null) {
//                        String oldText = mTextSticker.getText();
//                        mDoneCallback.onEditDone(oldText, text, mTextSticker, mSelectedColor, mOldColor);
//                    } else {
//                        mDoneCallback.onEditDone(text, text, null, mSelectedColor, mOldColor);
//                    }
//                }
//
//                mTextSticker = null;
//                mEditText.setText("");
//                mEditText.setInputType(InputType.TYPE_NULL);
//                dismiss();
//            }
//        } catch (Exception e) {
//            mTextSticker = null;
//            mEditText.setText("");
//            mEditText.setInputType(InputType.TYPE_NULL);
//            if (mContext != null && mContext instanceof Activity && !((Activity) mContext).isFinishing()) {
//                dismiss();
//            }
//        }
//    }

    private boolean isTextLengthValidate(String text) {
        if (text == null) return false;
        // 前面的计算是按照一个中文字符算2的长度，一个英文字符算1，所以在最后计算的时候，限制70个字，
        // 就应该对应的是140个字符长度
        return isTextLengthValidate(getTotalTextLength(text));
    }

    private boolean isTextLengthValidate(int textLength) {
        return textLength <= 70 * 2;
    }

    private int getTotalTextLength(String addString) {
        int totalLength = mInvoker.getTextStickersTotalLength();
        int lengthIncrease;
        if (mTextSticker != null) {
            int newLength = StringUtil.getEmojiAdjustSize(addString);
            int oldLength = StringUtil.getEmojiAdjustSize(mTextSticker.getText());
            lengthIncrease = newLength - oldLength;
        } else {
            lengthIncrease = StringUtil.getEmojiAdjustSize(addString);
        }
        totalLength += lengthIncrease;
        return totalLength;
    }

    @Override
    public void onColorItemClick(int position, ColorPickerBean bean) {
        if (bean != null) {
            mSelectedColor = mContext.getResources().getColor(bean.getColorRes());
            mEditText.setTextColor(mSelectedColor);
        }
    }

    @Override
    public void onColorPickerBackIconClick() {

    }

    public interface OnEditDoneCallback {
        void onEditDone(String oldText, String newText, TextSticker textSticker, int newColor, int oldColor);
    }
}
