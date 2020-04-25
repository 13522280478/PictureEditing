//package com.biotech.drawlessons.photoedit.resourcepicker;
//
//import android.content.Context;
//import android.view.View;
//
//
//import java.util.ArrayList;
//import java.util.List;
//
///***************************************************************************************************
// * 描述：
// *
// * 作者：champion
// *
// * 时间：17/12/7
// **************************************************************************************************/
//
//
//public class PopMediaBeautyDialog extends PopDialog implements MediaResourcePickerView.OnResourceItemClickListener {
//
//    private final static boolean DEBUG = true;
//
//    private final static String TAG = MediaResourcePickerView.PREFIX_TAG + "PopMediaBeautyDialog";
//    private View mRootView;
//    private View mViewClose;
//
//    private MediaResourcePickerView mPickerView;
//
//    private MediaResourcePickerView.OnResourceItemClickListener mItemClickListener;
//
//    public PopMediaBeautyDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
//        super(context, cancelable, cancelListener);
//    }
//
//    public PopMediaBeautyDialog(Context context, int theme) {
//        super(context, theme);
//    }
//
//    public PopMediaBeautyDialog(Context context) {
//        super(context);
//    }
//
//    public void build() {
//        // 加载View
//        findViews();
//        initViews();
//        initDatas();
//        initListeners();
//
//        // 添加View
//        build(mRootView);
//    }
//
//    public void setOnResourceItemClickListener(MediaResourcePickerView.OnResourceItemClickListener listener) {
//        this.mItemClickListener = listener;
//    }
//
//    private void initListeners() {
//        mViewClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });
//
//        mPickerView.setOnResourceItemClickListener(this);
//    }
//
//    private void findViews() {
//        mRootView = mInflater.inflate(R.layout.layout_dialog_media_beauty_picker, null);
//        mPickerView = mRootView.findViewById(R.id.v_media_resource_picker);
//        mViewClose = mRootView.findViewById(R.id.v_top_bar);
//    }
//
//    private void initViews() {
//        mBgView.setBackgroundColor(0x00000000);
//        mContainerLayout.setBackgroundColor(0x00000000);
//    }
//
//    private void initDatas() {
//
//        List<PageAdapter.TabData> datas = new ArrayList<>();
//
//        datas.add(new PageAdapter.TabData(
//                R.drawable.icon_media_edit_beauty_recent_use_selected_light,
//                R.drawable.icon_media_edit_beauty_recent_use_selected_night,
//                R.drawable.icon_media_edit_beauty_recent_use_unselected_light,
//                R.drawable.icon_media_edit_beauty_recent_use_unselected_night, false,
//                "最近"));
//        datas.add(new PageAdapter.TabData(
//                R.drawable.icon_filter_selected_light,
//                R.drawable.icon_filter_selected_night,
//                R.drawable.icon_filter_unselected_light,
//                R.drawable.icon_filter_unselected_night, false, "滤镜"));
//        datas.add(new PageAdapter.TabData(
//                R.drawable.icon_picture_frame_selected_light,
//                R.drawable.icon_picture_frame_selected_night,
//                R.drawable.icon_picture_frame_unselected_light,
//                R.drawable.icon_picture_frame_unselected_night, false, "相框"));
//        // datas.add(new PageAdapter.TabData(
//        //         R.drawable.icon_special_effect_selected_light,
//        //         R.drawable.icon_special_effect_selected_night,
//        //         R.drawable.icon_special_effect_unselected_light,
//        //         R.drawable.icon_special_effect_unselected_night,
//        //         "特效"));
//
//        PageAdapter.PageContentData data1 = new PageAdapter.PageContentData();
//        data1.mContentList = FakeData.buildRecendDatas();
//
//        PageAdapter.PageContentData data2 = new PageAdapter.PageContentData();
//        data2.mContentList = new ArrayList<>();
//        data2.mContentList.addAll(FakeData.buildFilterDatas());
//
//        PageAdapter.PageContentData data3 = new PageAdapter.PageContentData();
//        data3.mContentList = new ArrayList<>();
//        data3.mContentList.addAll(FakeData.buildPhotoFrameDatas());
//
//        List<PageAdapter.PageContentData> contentDatas = new ArrayList<>();
//        contentDatas.add(data1);
//        contentDatas.add(data2);
//        contentDatas.add(data3);
//
//
//        mPickerView.setDatas(datas, contentDatas, MediaResourcePickerView.BEAUTY_MODE);
//        mPickerView.setCurItem(1);
//
//    }
//
//
//    @Override
//    public void onResourceClick(Data data) {
//        LogUtils.d(DEBUG, TAG, "onResourceClick");
//        if (mItemClickListener != null) {
//            mItemClickListener.onResourceClick(data);
//        }
//    }
//
//    @Override
//    public void onClearResourceClick() {
//
//    }
//}
