//package com.biotech.drawlessons.photoedit.test;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//
//import com.sohu.kan.R;
//import com.sohu.kan.common.utils.LogUtils;
//import com.sohu.kan.common.utils.glide.GlideUtils;
//import com.sohu.kan.controllers.BaseActivity;
//import com.sohu.kan.controllers.photoedit.resourcepicker.MediaResourcePickActivity;
//import com.sohu.kan.controllers.photoedit.resourcepicker.MediaResourcePickerView;
//import com.sohu.kan.controllers.photoedit.resourcepicker.PopMediaBeautyDialog;
//import com.sohu.kan.controllers.photoedit.resourcepicker.custom.recyclerview.model.Data;
//import com.sohu.kan.controllers.photoedit.resourcepicker.custom.recyclerview.model.FilterData;
//import com.sohu.kan.controllers.photoedit.resourcepicker.custom.recyclerview.model.ResourceData;
//
//public class MediaResourceTestActivity extends BaseActivity {
//
//    private final static boolean DEBUG = true;
//
//    private final static String TAG = MediaResourcePickerView.PREFIX_TAG + "MainActivity";
//
//    private Button mBtnPicker;
//    private Button mBtnDialog;
//
//    private ImageView mImgPreview;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        LogUtils.d(TAG, "onCreate");
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_media_resource_test);
//
//        mBtnPicker = findViewById(R.id.btn_picker);
//        mBtnDialog = findViewById(R.id.btn_dialog);
//
//        mImgPreview = findViewById(R.id.img_preview);
//
//        mBtnPicker.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MediaResourceTestActivity.this, MediaResourcePickActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        mBtnDialog.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDelDialog();
//            }
//        });
//
//    }
//
//    @Override
//    protected void onStart() {
//        LogUtils.d(TAG, "onStart");
//        super.onStart();
//    }
//
//    @Override
//    protected void onRestart() {
//        LogUtils.d(TAG, "onRestart");
//        super.onRestart();
//    }
//
//    @Override
//    protected void onResume() {
//        LogUtils.d(TAG, "onResume");
//        super.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        LogUtils.d(TAG, "onPause");
//        super.onPause();
//    }
//
//    @Override
//    protected void onStop() {
//        LogUtils.d(TAG, "onStop");
//        super.onStop();
//    }
//
//    @Override
//    protected void onDestroy() {
//        LogUtils.d(TAG, "onDestroy");
//        super.onDestroy();
//    }
//
//    private void showDelDialog() {
//        PopMediaBeautyDialog dialog = new PopMediaBeautyDialog(mContext);
//        dialog.setOnResourceItemClickListener(new MediaResourcePickerView.OnResourceItemClickListener() {
//            @Override
//            public void onResourceClick(Data data) {
//                // 有本地路径类型的资源 如贴纸 相框等
//                if (data != null && data instanceof ResourceData) {
//                    String localPath = ((ResourceData) data).getLocalPath();
//                    LogUtils.d(DEBUG, TAG, "local path: " + localPath);
//                    GlideUtils.setImageBmp(localPath, mImgPreview);
//                }
//                // 滤镜类型
//                else if(data != null && data instanceof FilterData) {
//
//                }
//            }
//
//            @Override
//            public void onClearResourceClick() {
//
//            }
//        });
//        dialog.build();
//        dialog.show();
//    }
//}
