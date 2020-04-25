package com.biotech.drawlessons.photoedit.tools;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.biotech.drawlessons.R;
import com.biotech.drawlessons.UtilsKt;
import com.biotech.drawlessons.photoedit.utils.BitmapsManager;
import com.biotech.drawlessons.photoedit.utils.IPhotoEditType;

import java.util.ArrayList;
import java.util.List;

public class ColorPickerView extends RelativeLayout implements View.OnClickListener {

    //当前笔触是否有大小设置
    private RecyclerView mRecyclerView;
    private ColorPickerAdapter mAdapter;
    private List<String> mBitmapUrl;

    private List<ColorPickerBean> mData;
    //Paint开始绘图的消息
    private Context mContext;
    private int mViewWidth, mViewHeight;
    private ImageView mIconBack;
    private OnColorPickerListener mListener;

    public ColorPickerView(Context context) {
        this(context, null);
    }

    public ColorPickerView(Context context, AttributeSet set) {
        this(context, set, 0);
    }

    public ColorPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initData();
        initView();
        initListener();
    }

    void initData() {
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
        ColorPickerBean bean = new ColorPickerBean(IPhotoEditType.BRUSH_LIGHT_COLOR, false, 0);
        ColorPickerBean bean1 = new ColorPickerBean(IPhotoEditType.BRUSH_BLOCK_MOSAICS, false, 0);
        ColorPickerBean bean2 = new ColorPickerBean(IPhotoEditType.BRUSH_MOSAICS, false, 0);
        ColorPickerBean bean3 = new ColorPickerBean(IPhotoEditType.BRUSH_STICKERS, false, 0);
        ColorPickerBean bean4 = new ColorPickerBean(IPhotoEditType.BRUSH_BACKGROUND, false, 0);
        bean4.setBitmapType(BitmapsManager.KEY_FIRST_BACKGROUND_BRUSH);

        ColorPickerBean bean5 = new ColorPickerBean(IPhotoEditType.BRUSH_BACKGROUND, false, 0);
        bean5.setBitmapType(BitmapsManager.KEY_SECOND_BACKGROUND_BRUSH);

        mData.get(0).setClick(true);
        mData.add(bean);
        mData.add(bean1);
        mData.add(bean2);
        mData.add(bean3);
        mData.add(bean4);
        mData.add(bean5);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
    }

    public void initView() {
        View mRootView = LayoutInflater.from(mContext).inflate(R.layout.layout_color_picker, this);
        mRecyclerView = mRootView.findViewById(R.id.id_recyclerview_horizontal);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.addItemDecoration(
                new HorizontalItemDecoration((int)UtilsKt.dp2px(10),
                        (int)UtilsKt.dp2px(11), true)
        );
        mAdapter = new ColorPickerAdapter(mContext, mData);
        mRecyclerView.setAdapter(mAdapter);

        mIconBack = findViewById(R.id.iv_back);
        mIconBack.setAlpha(0.3f);
        mIconBack.setEnabled(false);
    }

    private void initListener() {
        mIconBack.setOnClickListener(this);
    }

    public void setOnItemClickListener(OnColorPickerListener listener) {
        mListener = listener;
        mAdapter.setOnItemClickListener(mListener);
    }

    public ImageView getIconBack() {
        return mIconBack;
    }

    public ColorPickerBean getSelectedBean() {
        for (ColorPickerBean bean : mData) {
            if (bean.getClick()) {
                return bean;
            }
        }

        return null;
    }

    public boolean isNormalColorSelected() {
        ColorPickerBean bean = getSelectedBean();
        return bean != null && bean.getType() == IPhotoEditType.BRUSH_NORMAL_COLOR;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                if (mListener != null) {
                    mListener.onColorPickerBackIconClick();
                }
                break;
        }
    }

    public interface OnColorPickerListener {
        void onColorItemClick(int position, ColorPickerBean bean);

        void onColorPickerBackIconClick();
    }
}
