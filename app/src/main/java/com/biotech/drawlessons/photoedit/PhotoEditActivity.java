//package com.biotech.drawlessons.photoedit;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.biotech.drawlessons.photoedit.utils.BitmapsManager;
//import com.biotech.drawlessons.photoedit.utils.DrawInvoker;
//import com.biotech.drawlessons.photoedit.utilsmodel.PhotoEditToolsHelper;
//import com.biotech.drawlessons.photoedit.views.DrawingBoardView;
//
//import java.util.concurrent.Callable;
//
///**
// * Created by xintu on 2018/2/20.
// */
//
//public class PhotoEditActivity extends Activity implements DrawingBoardView.InitFinishCallback {
//    public static final int REQUEST_CODE_EDIT_PHOTO = 200;
//    private DrawingBoardView mBord;
//    private RelativeLayout mRootView;
//    private DrawInvoker mInvoker;
//    private BitmapsManager mBitmapManager;
//    private String mEditPhotoUri;
//    private TextView mTvCancel, mTvFinish;
//    private LoadingViewSns mLoadingView;
//    private MediaFileBean mMediaFileBean;
//    private boolean mSaved;
//    private boolean mSaving;
//    private PhotoEditToolsHelper mToolsHelper;
//
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_photo_edit);
//        closeSlide = true;
//        initUI();
//        initData();
//        initListener();
//    }
//
//    private void initData() {
//        mMediaFileBean = (MediaFileBean) getIntent().getSerializableExtra(Constant.KEY_MEDIA_FILE_BEAN);
//        mEditPhotoUri = mMediaFileBean.getUri();
//        // 如果是已经修改过的路径，就拿原始路径
//        if (DrawDataManager.getInstance().isEditedUrl(mEditPhotoUri)) {
//            mEditPhotoUri = DrawDataManager.getInstance().getOriginalUrl(mEditPhotoUri);
//        }
//        //TODO:如果路径不对，需要有处理
//        mBitmapManager = new BitmapsManager();
//        mInvoker = new DrawInvoker(mBitmapManager);
//        mBord.init(mInvoker, mBitmapManager, mEditPhotoUri);
//        mToolsHelper = new PhotoEditToolsHelper(this, mRootView, mBord, mBitmapManager, mInvoker);
//    }
//
//    private void initUI() {
//        mRootView = findViewById(R.id.root_view);
//        mBord = findViewById(R.id.drawing_board);
//        mTvCancel = findViewById(R.id.tv_cancel);
//        mTvFinish = findViewById(R.id.tv_finish);
//        mLoadingView = findViewById(R.id.loading_view);
//    }
//
//    private void initListener() {
//        mBord.setInitFinishCallback(this);
//        mTvFinish.setOnClickListener(this);
//        mTvCancel.setOnClickListener(this);
//    }
//
//    @Override
//    public void finish() {
//        mBitmapManager.removeAllBitmap();
//        mToolsHelper.onFinish();
//        super.finish();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.tv_cancel:
//                goBack();
//                break;
//            case R.id.tv_finish:
//                if (mBord.isPhotoEdited()) {
//                    mLoadingView.showLoadingView();
//                    mBitmapManager.drawSecondTempBitmapUnderInternalBitmap();
//                    mBitmapManager.erasSecondTempBitmap();
//                    mBord.switchToIdleLayer();
//                    mSaving = true;
//                    Task.call(new Callable<RestoreBean>() {
//                        @Override
//                        public RestoreBean call() throws Exception {
//                            return mBord.saveDrawDatas(false);
//                        }
//                    }, Task.BACKGROUND_EXECUTOR).continueWith(new Continuation<RestoreBean, Object>() {
//                        @Override
//                        public Object then(Task<RestoreBean> task) throws Exception {
//                            mSaving = false;
//                            RestoreBean bean = task.getResult();
//                            if (bean != null) {
//                                mSaved = true;
//                                DrawDataManager.getInstance().updateRestoreBean(mEditPhotoUri, bean);
//                                mMediaFileBean.setUri(BitmapsManager.getSaveEditedUri(mEditPhotoUri));
//                            }
//                            mLoadingView.dismissLoadingView();
//                            setSavedResult();
//                            finish();
//                            return null;
//                        }
//                    }, Task.UI_THREAD_EXECUTOR);
//                } else {
//                    finish();
//                }
//                break;
//        }
//    }
//
//    @Override
//    public void goBack() {
//        if (mToolsHelper.dealBackEvent()) return;
//        if (mSaving) return;
//        if (mSaved) {
//            setSavedResult();
//        }
//        super.goBack();
//    }
//
//    private void setSavedResult() {
//        Intent intent = new Intent();
//        intent.putExtra(Constant.KEY_MEDIA_FILE_BEAN, mMediaFileBean);
//        intent.putExtra(Constant.KEY_PHOTO_EDITED, true);
//        setResult(RESULT_OK, intent);
//    }
//
//    @Override
//    public void onInitFinish() {
//        mLoadingView.dismissLoadingView();
//        mToolsHelper.updateSelectedFilter(mBord.getCurFilterType());
//    }
//}
