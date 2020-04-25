//package com.biotech.drawlessons.photoedit.resourcepicker;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.widget.ImageView;
//
//
//import com.biotech.drawlessons.R;
//import com.biotech.drawlessons.photoedit.resourcepicker.custom.PageAdapter;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MediaResourcePickActivity extends Activity {
//
//
//    private final static boolean DEBUG = true;
//
//    private final static String TAG = MediaResourcePickerView.PREFIX_TAG + "MediaResourcePickActivity";
//
//    private MediaResourcePickerView mPickerView;
//
//    // 测试用 待删
//    private ImageView mImgPreview;
//
//    // public static void launch(Context context) {
//    //     Intent intent = new Intent(context, MediaResourcePickActivity.class);
//    //     startActivity(intent);
//    // }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_media_resource_pick);
//
//        closeSlide = true;
//
//        mPickerView = findViewById(R.id.v_media_resource_picker);
//        mImgPreview = findViewById(R.id.img_preview);
//
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
//
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
//        // List<MediaResourcePickerView.TabDataItem> datas = new ArrayList<>();
//        //
//        // datas.add(new MediaResourcePickerView.TabDataItem(
//        //         R.drawable.icon_media_edit_beauty_recent_use_selected_light,
//        //         R.drawable.icon_media_edit_beauty_recent_use_selected_night,
//        //         R.drawable.icon_media_edit_beauty_recent_use_unselected_light,
//        //         R.drawable.icon_media_edit_beauty_recent_use_unselected_night,
//        //         "最近"));
//        // datas.add(new MediaResourcePickerView.TabDataItem(
//        //         R.drawable.icon_filter_selected_light,
//        //         R.drawable.icon_filter_selected_night,
//        //         R.drawable.icon_filter_unselected_light,
//        //         R.drawable.icon_filter_unselected_night, "滤镜"));
//        // datas.add(new MediaResourcePickerView.TabDataItem(
//        //         R.drawable.icon_picture_frame_selected_light,
//        //         R.drawable.icon_picture_frame_selected_night,
//        //         R.drawable.icon_picture_frame_unselected_light,
//        //         R.drawable.icon_picture_frame_unselected_night, "相框"));
//        // datas.add(new MediaResourcePickerView.TabDataItem(
//        //         R.drawable.icon_special_effect_selected_light,
//        //         R.drawable.icon_special_effect_selected_night,
//        //         R.drawable.icon_special_effect_unselected_light,
//        //         R.drawable.icon_special_effect_unselected_night,
//        //         "特效"));
//        //
//        //
//        // mPickerView.setTabDatas(datas);
//
//        initListeners();
//    }
//
//    private void initListeners() {
//        mPickerView.setOnResourceItemClickListener(new MediaResourcePickerView.OnResourceItemClickListener() {
//            @Override
//            public void onResourceClick(Data data) {
//                LogUtils.d(DEBUG, TAG, "onResourceClick");
//                if (data != null && data instanceof ResourceData) {
//                    String localPath = ((ResourceData) data).getLocalPath();
//                    LogUtils.d(DEBUG, TAG, "local path: " + localPath);
//                    GlideUtils.setImageBmp(localPath, mImgPreview);
//                }
//            }
//
//            @Override
//            public void onClearResourceClick() {
//
//            }
//        });
//    }
//
//
//}
