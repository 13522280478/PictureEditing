//package com.biotech.drawlessons.photoedit.utilsmodel;
//
//import android.animation.Animator;
//import android.animation.AnimatorListenerAdapter;
//import android.animation.ValueAnimator;
//import android.text.TextUtils;
//import android.view.View;
//
//import com.biotech.drawlessons.photoedit.resourcepicker.DragMediaResourcePickerView;
//import com.biotech.drawlessons.photoedit.resourcepicker.MediaResourcePickerView;
//import com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.model.BaseData;
//import com.biotech.drawlessons.photoedit.views.AbstractDragLayout;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.concurrent.Callable;
//
///**
// * Created by xintu on 2018/3/14.
// */
//
//public class PhotoEditMaterialModel implements AbstractDragLayout.OnDragStateChangeListener {
//    private static final int STICKER_MODE = 1;
//    private static final int BEAUTY_MODE = 2;
//    private static final String RECENT_STICKER_GROUP_BURY_CODE = "recent_sticker";
//    private static final int MAX_STICKER_RECENT_COUNT = 28;
//    private static final int MAX_BEAUTY_RECENT_COUNT = 12;
//    private boolean mInited;
//    // 服务器取到的相册数据
//    private MaterialPhotoFramesBean mPhotoEditAlbumData;
//    // 由服务器获取到的数据经过转换的数据，为了方便 RecyclerView 使用
//    private ArrayList<PhotoFrameData> mPhotoFrameList;
//    // 服务器获取到的贴纸数据
//    private MaterialStickerBean mMaterialStickerData;
//    private MaterialPhotoFramesBean mMaterialPhotoFrameData;
//    // 由服务器获取到的数据经过转换的数据，方便 recyclerView 使用
//    private ArrayList<PhotoFrameData> mStickerList;
//    private ArrayList<PageAdapter.TabData> mStickerTabList = new ArrayList<>();
//    private ArrayList<PageAdapter.PageContentData> mStickerContentList = new ArrayList<>();
//    private ArrayList<PageAdapter.TabData> mBeautyTabList = new ArrayList<>();
//    private ArrayList<PageAdapter.PageContentData> mBeautyContentList = new ArrayList<>();
//    private PageAdapter.PageContentData mRecentStickerContent;
//    private PageAdapter.PageContentData mRecentBeautyContent;
//    private PageAdapter.PageContentData mAlbumContent;
//    private volatile boolean mStickerRequesting;
//    private volatile boolean mAlbumRequesting;
//    private DragMediaResourcePickerView mResourcePickerView;
//    private boolean mAnimating;
//    private boolean mResourcePickerShown;
//
//    private boolean mStickerFromNetInit;
//    private boolean mStickerFromCacheInit;
//    private boolean mStickerRequestOK;
//
//    private boolean mBeautyFromNetInit;
//    private boolean mBeautyFromCacheInit;
//    private boolean mBeautyRequestOk;
//    // 这个时间戳是由服务器传递的正确的时间戳，在请求出现时间戳错误的时候，这个值会被重置
//    private String mRealTimeStamps;
//    private int mRetryRequestTime;
//    private int mMode;
//    private String mCacheStickerPageBuryCode;
//    private String mCacheBeautyPageBuryCode;
//    private MaterialCloseListener mDismissCallback;
//    private int mStickerTabIndex;
//    private int mBeautyTabIndex;
//
//    public PhotoEditMaterialModel(DragMediaResourcePickerView resourcePickerView) {
//        mResourcePickerView = resourcePickerView;
//        initListener();
//    }
//
//    private void initListener() {
//        mResourcePickerView.setOnDragStateChangeListener(this);
//    }
//
//    public void setOnResourceItemClickListener(MediaResourcePickerView.OnResourceItemClickListener listener) {
//        mResourcePickerView.setOnResourceItemClickListener(listener);
//    }
//
//    public void finish() {
//        if (mStickerContentList != null && mStickerContentList.size() > 0 && mStickerFromCacheInit) {
//            cacheStickerDatas();
//        }
//
//        if (mBeautyContentList != null && mBeautyContentList.size() >= 0 && mBeautyFromCacheInit) {
//            cacheBeautyDatas();
//        }
//    }
//
//    /**
//     * 把 sticker 数据存到 sp 中
//     */
//    private void cacheStickerDatas() {
//        // 存储 sticker 的 tab 数据
//        Repo.getInstance().getMaterialHistorySp(Global.getUserId()).updateStickerTabs(mStickerTabList);
//        // 存储 sticker 的 content 数据
//        Repo.getInstance().getMaterialHistorySp(Global.getUserId()).updateStickerPageContents(mStickerContentList);
//        // 通过当前的 pageIndex 获取到 groupBuryCode，并保存
//        int curPageIndex = mResourcePickerView.getCurrentPageIndex();
//        String pageBuryCode = "";
//        if (curPageIndex >= 0 && curPageIndex < mStickerContentList.size()) {
//            if (mStickerContentList.get(curPageIndex) != null) {
//                pageBuryCode = mStickerContentList.get(curPageIndex).mBuryCode;
//            }
//        }
//        Repo.getInstance().getMaterialHistorySp(Global.getUserId()).updateStickerPageBuryCode(pageBuryCode);
//    }
//
//    /**
//     * 把美化数据存到 sp 中
//     */
//    private void cacheBeautyDatas() {
//        // 存储 sticker 的 tab 数据
//        Repo.getInstance().getMaterialHistorySp(Global.getUserId()).updateBeautyTabs(mBeautyTabList);
//        // 存储 sticker 的 content 数据
//        Repo.getInstance().getMaterialHistorySp(Global.getUserId()).updateBeautyPageContents(mBeautyContentList);
//        // 通过当前的 pageIndex 获取到 groupBuryCode，并保存
//        int curPageIndex = mResourcePickerView.getCurrentPageIndex();
//        String pageBuryCode = "";
//        if (curPageIndex >= 0 && curPageIndex < mBeautyContentList.size()) {
//            if (mBeautyContentList.get(curPageIndex) != null) {
//                pageBuryCode = mBeautyContentList.get(curPageIndex).mBuryCode;
//            }
//        }
//        Repo.getInstance().getMaterialHistorySp(Global.getUserId()).updateBeautyPageBuryCode(pageBuryCode);
//    }
//
//    public void setOnDismissListener(MaterialCloseListener listener) {
//        mDismissCallback = listener;
//    }
//
//    public boolean isResourcePickerShown() {
//        return mResourcePickerShown;
//    }
//
//    public void beautyPickerClick() {
//        boolean modeChange = mMode != BEAUTY_MODE;
//        mMode = BEAUTY_MODE;
//        if (modeChange) {
//            if (mBeautyFromCacheInit) {
//                mResourcePickerView.setDatas(getBeautyTabList(), getBeautyContentList(),
//                        MediaResourcePickerView.BEAUTY_MODE);
//                mResourcePickerView.setCurItem(mBeautyTabIndex);
//            }
//        }
//        getAlbumDatas();
//        showResourcePicker();
//    }
//
//    public void getAlbumDataFromNet(final Callback<Boolean> callback) {
//        // 如果正在请求，直接return
//        if (mAlbumRequesting || mStickerRequesting || !NetworkUtils.isConnect(SnsApplication.getInstance())) return;
//        mAlbumRequesting = true;
//        Task.call(new Callable<MaterialPhotoFramesBean>() {
//            @Override
//            public MaterialPhotoFramesBean call() throws Exception {
//                // 如果 mRealTimeStamps被赋值，说明之前一次传值，时间戳错误，服务器返回了一个正确的时间戳，
//                // 这时候用正确的时间戳去请求
//                String timestamp = !TextUtils.isEmpty(mRealTimeStamps) ? mRealTimeStamps : String.valueOf(System.currentTimeMillis());
//                MaterialPhotoFramesBean response = null;
//                mAlbumRequesting = false;
//                try {
//                    response = ApiRequest.getPhotoEditAlbums(timestamp);
//                } catch (Exception e) {
//                    // 获取出错
//                    e.printStackTrace();
//                }
//                updatePhotoFrameByResponse(response);
//
//                return response;
//            }
//        }, Task.BACKGROUND_EXECUTOR).continueWith(new Continuation<MaterialPhotoFramesBean, Object>() {
//            @Override
//            public MaterialPhotoFramesBean then(Task<MaterialPhotoFramesBean> task) throws Exception {
//                MaterialPhotoFramesBean response = task.getResult();
//                // response 的状态码是对的
//                if (response != null && response.isStatusOk()) {
//                    if (callback != null) {
//                        callback.onCallback(true);
//                    }
//                } else if (response != null) {
//                    // response 状态码不对，根据状态码，做相应的处理
//                    int statusCode = response.status;
//                    // 传递的时间戳错了，说明手机的系统时间被人为的修改过，这个时候，获取一个正确的时间戳，
//                    // 再请求一次，重试的请求只做一次，防止服务器出bug，导致我们这边死循环
//                    if (statusCode == MaterialStickerBean.TIME_STAMPS_ERROR && mRetryRequestTime < 1) {
//                        mRealTimeStamps = response.timestamp;
//                        updateBeautyDataFromNet();
//                        mRetryRequestTime++;
//                    }
//                    // 请求失败，弹相应的toast
//                    ToastModel.showRed(SnsApplication.getInstance(), response.msg);
//                    if (callback != null) {
//                        callback.onCallback(false);
//                    }
//                } else {
//                    // 请求失败，弹相应的toast
//                    ToastModel.showRed(SnsApplication.getInstance(), R.string.tip_request_response_data_parser_error);
//                    if (callback != null) {
//                        callback.onCallback(false);
//                    }
//                }
//                return null;
//            }
//        }, Task.UI_THREAD_EXECUTOR);
//    }
//
//    private void getAlbumDatas() {
//        // 如果已经从缓存中获取到了数据，且已经更新UI，这个时候不在做处理
//        if (!mBeautyFromCacheInit) {
//            getBeautyDataFromCache(new Callback<Object>() {
//                @Override
//                public void onCallback(Object pValue) {
//                    // 获取到缓存数据之后，更新UI，同时获取缓存数据
//                    // 如果弹窗已经完全显示，这个时候显示数据
//                    if (mResourcePickerShown) {
//                        mBeautyFromCacheInit = true;
//                        mResourcePickerView.setDatas(getBeautyTabList(), getBeautyContentList(), MediaResourcePickerView.BEAUTY_MODE);
//                    }
//                    // 开始请求网络数据
//                    updateBeautyDataFromNet();
//                }
//            });
//        }
//        // 这种情况是取到了缓存数据，但是没有取到网络数据，尝试再去取一次
//        else if (!mBeautyFromNetInit) {
//            // 开始请求网络数据
//            updateBeautyDataFromNet();
//        }
//    }
//
//    private void getBeautyDataFromCache(final Callback<Object> callback) {
//        Task.call(new Callable<Object>() {
//            @Override
//            public Object call() throws Exception {
//                mBeautyTabList.clear();
//                // 好像不需要从数据库中取数据了，所有的tab都是手动构建的
////                ArrayList<PageAdapter.TabData> tabData
////                        = Repo.getInstance().getMaterialHistorySp(Global.getUserId()).getBeautyTabs();
//                // 第一次进入，没有缓存，这个时候主动构建一个“最近”的tab
//                mBeautyTabList.add(FakeData.buildRecentTabData(true));
//                // 构建一个“滤镜”tab
//                mBeautyTabList.add(FakeData.buildFilterTabData(true));
//                // 构建一个“相框”tab
//                mBeautyTabList.add(FakeData.buildPhotoFrameTabData(true));
//
//                // 从缓存中获取 content 数据
//                mBeautyContentList.clear();
//                ArrayList<PageAdapter.PageContentData> contentData
//                        = Repo.getInstance().getMaterialHistorySp(Global.getUserId()).getBeautyPagesContents();
//
//                if (contentData == null || contentData.size() == 0) {
//                    // 第一次进入，没有缓存，这个时候主动添加一个空的“最近”content
//                    mRecentBeautyContent = new PageAdapter.PageContentData();
//                    mRecentBeautyContent.mContentList = new ArrayList<>();
//                    // 同时需要添加一个空的data进去，因为 gson 在解析数据的时候，如果碰到list数据为空，
//                    // 他是无法转换类型的。
//                    PhotoFrameData recentData = new PhotoFrameData();
//                    mRecentBeautyContent.mContentList.add(recentData);
//                    // 添加“最近”list
//                    mBeautyContentList.add(mRecentBeautyContent);
//
//                    // 构建“滤镜” 的list
//                    PageAdapter.PageContentData filterContent = FakeData.buildFilterContentData();
//                    mBeautyContentList.add(filterContent);
//
//                    // 构建“相框”的list
//                    PhotoFrameData photoFrameData = new PhotoFrameData();
//                    mAlbumContent = new PageAdapter.PageContentData();
//                    mAlbumContent.mContentList = new ArrayList<>();
//                    mAlbumContent.mContentList.add(photoFrameData);
//                    mBeautyContentList.add(mAlbumContent);
//                } else {
//                    // 第一个content肯定是“最近”
//                    mRecentBeautyContent = contentData.get(0);
//                    if (contentData.size() > 2) {
//                        mAlbumContent = contentData.get(2);
//                    }
//                    mBeautyContentList.addAll(contentData);
//                }
//
//                mCacheBeautyPageBuryCode = Repo.getInstance().getMaterialHistorySp(Global.getUserId()).getBeautyPageBuryCode();
//
//                int index = getBeautyPageIndex();
//                mBeautyTabIndex = index == -1 ? 1 : index;
//                return null;
//            }
//        }, Task.BACKGROUND_EXECUTOR).continueWith(new Continuation<Object, Object>() {
//            @Override
//            public Object then(Task<Object> task) throws Exception {
//                // 获取完之后回调主线程
//                if (callback != null) {
//                    callback.onCallback(null);
//                }
//                return null;
//            }
//        }, Task.UI_THREAD_EXECUTOR);
//    }
//
//    private void updateBeautyDataFromNet() {
//        // 如果已经从网络取到了数据且已经更新了，不在做处理
//        if (mBeautyFromNetInit) return;
//        getAlbumDataFromNet(new Callback<Boolean>() {
//            @Override
//            public void onCallback(Boolean pValue) {
//                // 如果网络请求回来，发现 resourcePicker 已经完全弹起，这个时候，可以直接更新数据
//                if (mResourcePickerShown && pValue) {
//                    mBeautyFromNetInit = true;
//                    mResourcePickerView.setDatas(getBeautyTabList(), getBeautyContentList(), MediaResourcePickerView.BEAUTY_MODE);
//                    mResourcePickerView.setCurItem(mBeautyTabIndex);
//                }
//            }
//        });
//    }
//
//    /**
//     * 根据获取到的bean来更新UI，因为涉及到bean的二次解析，所以这里最好放在子线程做
//     */
//    private void updatePhotoFrameByResponse(MaterialPhotoFramesBean response) {
//        if (response != null && response.isStatusOk()) {
//            mBeautyRequestOk = true;
//            mMaterialPhotoFrameData = response;
//
//            mAlbumContent.mContentList.clear();
//            // 把网络数据转换为content数据，并且添加到list中
//            mAlbumContent.mContentList.addAll(mMaterialPhotoFrameData.convertToPhotoFrameList());
//
//            int index = getBeautyPageIndex();
//            mBeautyTabIndex = index == -1 ? 1 : index;
//
////            // 过滤掉已经下架的 sticker
////            filterTimeOutSticker();
//        }
//    }
//
//    /**
//     * ===========================这里是获取sticker弹窗的一些方法====================================
//     */
//    public void stickerPickerClick() {
//        boolean modeChange = mMode != STICKER_MODE;
//        mMode = STICKER_MODE;
//        if (modeChange) {
//            if (mStickerFromNetInit) {
//                mResourcePickerView.setDatas(getStickerTabList(), getStickerContentList(), MediaResourcePickerView.STICKER_MODE);
//                mResourcePickerView.setCurItem(mStickerTabIndex);
//            }
//            updateStickerDatas();
//        }
//        showResourcePicker();
//    }
//
//    public void getStickerDatasFromCache(final Callback<Object> callback) {
//        Task.call(new Callable<Object>() {
//            @Override
//            public Object call() throws Exception {
//                mStickerTabList.clear();
//                ArrayList<PageAdapter.TabData> tabData
//                        = Repo.getInstance().getMaterialHistorySp(Global.getUserId()).getStickerTabs();
//                if (tabData == null || tabData.size() == 0) {
//                    // 第一次进入，没有缓存，这个时候主动构建一个“最近”的tab
//                    // 注意！这里sticker上面的tab不需要显示文字
//                    mStickerTabList.add(FakeData.buildRecentTabData(false));
//                } else {
//                    mStickerTabList.addAll(tabData);
//                    // 因为拿到的resId在每一次构建的时候，都会变动，所以这里重新再set一次
//                    mStickerTabList.remove(0);
//                    mStickerTabList.add(0, FakeData.buildRecentTabData(false));
//                }
//
//                // 从缓存中获取 content 数据
//                mStickerContentList.clear();
//                ArrayList<PageAdapter.PageContentData> contentData
//                        = Repo.getInstance().getMaterialHistorySp(Global.getUserId()).getStickersPagesContents();
//                if (contentData == null || contentData.size() == 0) {
//                    // 第一次进入，没有缓存，这个时候主动添加一个空的“最近”content
//                    mRecentStickerContent = new PageAdapter.PageContentData();
//                    mRecentStickerContent.mContentList = new ArrayList<>();
//                    // 同时需要添加一个空的data进去，因为 gson 在解析数据的时候，如果碰到list数据为空，
//                    // 他是无法转换类型的。
//                    StickerData data = new StickerData();
//                    // 同时使用默认的bury_code
//                    data.mGroupBuryCode = RECENT_STICKER_GROUP_BURY_CODE;
//                    mRecentStickerContent.mContentList.add(data);
//                    mRecentStickerContent.mBuryCode = RECENT_STICKER_GROUP_BURY_CODE;
//
//                    mStickerContentList.add(mRecentStickerContent);
//                } else {
//                    // 第一个content肯定是“最近”
//                    mRecentStickerContent = contentData.get(0);
//                    mRecentStickerContent.mBuryCode = RECENT_STICKER_GROUP_BURY_CODE;
//                    mStickerContentList.addAll(contentData);
//                }
//
//                mCacheStickerPageBuryCode = Repo.getInstance().getMaterialHistorySp(Global.getUserId()).getStickerPageBuryCode();
//                int index = getStickerPageIndex();
//                mStickerTabIndex = index == -1 ? 1 : index;
//
//                return null;
//            }
//        }, Task.BACKGROUND_EXECUTOR).continueWith(new Continuation<Object, Object>() {
//            @Override
//            public Object then(Task<Object> task) throws Exception {
//                // 获取完之后回调主线程
//                if (callback != null) {
//                    callback.onCallback(null);
//                }
//                return null;
//            }
//        }, Task.UI_THREAD_EXECUTOR);
//    }
//
//    public void getStickerDatasFromNet(final Callback<Boolean> callback) {
//        // 如果正在请求，直接return
//        if (mAlbumRequesting || mStickerRequesting || !NetworkUtils.isConnect(SnsApplication.getInstance())) return;
//        mStickerRequesting = true;
//        Task.call(new Callable<MaterialStickerBean>() {
//            @Override
//            public MaterialStickerBean call() throws Exception {
//                // 如果 mRealTimeStamps被赋值，说明之前一次传值，时间戳错误，服务器返回了一个正确的时间戳，
//                // 这时候用正确的时间戳去请求
//                String timestamp = !TextUtils.isEmpty(mRealTimeStamps) ? mRealTimeStamps : String.valueOf(System.currentTimeMillis());
//                MaterialStickerBean response = null;
//                mStickerRequesting = false;
//                try {
//                    response = ApiRequest.getPhotoEditStickers(timestamp);
//                } catch (Exception e) {
//                    // 获取出错
//                    e.printStackTrace();
//                }
//                updateStickerByResponse(response);
//
//                return response;
//            }
//        }, Task.BACKGROUND_EXECUTOR).continueWith(new Continuation<MaterialStickerBean, Object>() {
//            @Override
//            public MaterialStickerBean then(Task<MaterialStickerBean> task) throws Exception {
//                MaterialStickerBean response = task.getResult();
//                // response 的状态码是对的
//                if (response != null && response.isStatusOk()) {
//                    if (callback != null) {
//                        callback.onCallback(true);
//                    }
//                } else if (response != null) {
//                    // response 状态码不对，根据状态码，做相应的处理
//                    int statusCode = response.status;
//                    // 传递的时间戳错了，说明手机的系统时间被人为的修改过，这个时候，获取一个正确的时间戳，
//                    // 再请求一次，重试的请求只做一次，防止服务器出bug，导致我们这边死循环
//                    if (statusCode == MaterialStickerBean.TIME_STAMPS_ERROR && mRetryRequestTime < 1) {
//                        mRealTimeStamps = response.timestamp;
//                        updateStickerDataFromNet();
//                        mRetryRequestTime++;
//                    }
//                    // 请求失败，弹相应的toast
//                    ToastModel.showRed(SnsApplication.getInstance(), response.msg);
//                    if (callback != null) {
//                        callback.onCallback(false);
//                    }
//                } else {
//                    // 请求失败，弹相应的toast
//                    ToastModel.showRed(SnsApplication.getInstance(), R.string.tip_request_response_data_parser_error);
//                    if (callback != null) {
//                        callback.onCallback(false);
//                    }
//                }
//                return null;
//            }
//        }, Task.UI_THREAD_EXECUTOR);
//    }
//
//    /**
//     * 根据获取到的bean来更新UI，因为涉及到bean的二次解析，所以这里最好放在子线程做
//     */
//    private void updateStickerByResponse(MaterialStickerBean response) {
//        if (response != null && response.isStatusOk()) {
//            mMaterialStickerData = response;
//            mStickerRequestOK = true;
//
//            // 清理掉除了“最近” 之外的所有 tab 数据
//            Iterator<PageAdapter.TabData> tabIt = mStickerTabList.iterator();
//            tabIt.next();
//            while (tabIt.hasNext()) {
//                tabIt.next();
//                tabIt.remove();
//            }
//            // 把网络数据转换为tab数据，并且添加到list中
//            mStickerTabList.addAll(mMaterialStickerData.convertToStickerTabList());
//
//            // 清理除了“最近” 之外的所有 content 数据
//            Iterator<PageAdapter.PageContentData> contentIt = getStickerContentList().iterator();
//            contentIt.next();
//            while (contentIt.hasNext()) {
//                contentIt.next();
//                contentIt.remove();
//            }
//            // 把网络数据转换为content数据，并且添加到list中
//            ArrayList<PageAdapter.PageContentData> stickerData = mMaterialStickerData.convertToStickerList();
//            getStickerContentList().addAll(stickerData);
//
//            // 过滤掉已经下架的 sticker
//            filterTimeOutSticker();
//
//            int index = getStickerPageIndex();
//            mStickerTabIndex = index == -1 ? 1 : index;
//        }
//    }
//
//    // 过滤掉那些已经下架的sticker，过滤的依据就是在 mRecentSticker 列队里的 sticker 的 mGroupBuryCode 在
//    // mMaterialSticker里面已经找不到了
//    private void filterTimeOutSticker() {
//        if (mRecentStickerContent == null || mMaterialStickerData == null) return;
//        // 先把所有的 sticker 的 needFilter 标志位置为 false
//        for (BaseData data : mRecentStickerContent.mContentList) {
//            if (data != null && data instanceof StickerData) {
//                ((StickerData) data).setNeedFilter(true);
//            }
//        }
//
//        for (int i = 0; i < mMaterialStickerData.data.size(); i++) {
//            String buryCode = mMaterialStickerData.data.get(i).bury_code;
//            for (BaseData data : mRecentStickerContent.mContentList) {
//                // 如果 mGroupBuryCode 能在 materialStickerData 里面找到，说明这个sticker没有被下架
//                // 这个时候，把needFilter的标志位置为false
//                if (data != null && data instanceof StickerData) {
//                    if (!TextUtils.isEmpty(buryCode) && buryCode.equals(((StickerData) data).mGroupBuryCode)) {
//                        ((StickerData) data).setNeedFilter(false);
//                    }
//                }
//            }
//        }
//
//        Iterator<BaseData> it = mRecentStickerContent.mContentList.iterator();
//        // 重新遍历一遍 mRecentSticker，发现如果有需要过滤的 data，就直接过滤掉
//        while (it.hasNext()) {
//            BaseData data = it.next();
//            if (data != null && data instanceof StickerData) {
//                if (((StickerData) data).getNeedFilter()) {
//                    // 如果sticker已经被下架，这个时候把相关的资源移除掉。
//                    String localPath = data.getLocalPath();
//                    if (!TextUtils.isEmpty(localPath)) {
//                        File file = new File(localPath);
//                        try {
//                            if (file.exists()) {
//                                file.delete();
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    it.remove();
//                }
//            }
//        }
//    }
//
//    public void showResourcePicker() {
//        if (mAnimating) return;
//        mResourcePickerView.setVisibility(View.VISIBLE);
//        ValueAnimator animator = mResourcePickerView.getShowAnimator();
//        animator.start();
//        animator.removeAllListeners();
//        animator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                mAnimating = false;
//                mResourcePickerShown = true;
//                // 如果弹窗先起来，先判断 sticker 请求是否结束，如果没有结束，说明之后会走到
//                // updateStickerDataFromNet 里面的方法来更新数据。
//                // 如果请求已经结束，发现 resourcePicker 还没有被初始化，这个时候，直接更新数据
//                if (!mStickerRequesting || !mAlbumRequesting) {
//                    if (mMode == STICKER_MODE) {
//                        showStickerDatasWhenPickerShown();
//                    } else if (mMode == BEAUTY_MODE) {
//                        showBeautyDatasWhenPickerShown();
//                    }
//                }
//            }
//
//            @Override
//            public void onAnimationStart(Animator animation) {
//                mAnimating = true;
//            }
//        });
//        animator.start();
//    }
//
//    private void showStickerDatasWhenPickerShown() {
//        // 如果网络数据获取到了，就更新网络数据
//        if (!mStickerFromNetInit && mStickerRequestOK) {
//            mStickerFromNetInit = true;
//            mStickerFromCacheInit = true;
//            mResourcePickerView.setDatas(getStickerTabList(), getStickerContentList(), MediaResourcePickerView.STICKER_MODE);
//            mResourcePickerView.setCurItem(mStickerTabIndex);
//            return;
//        }
//
//        // 如果缓存数据已经获取到了，就更新缓存的数据
//        if (!mStickerFromCacheInit) {
//            mStickerFromCacheInit = true;
//            mResourcePickerView.setDatas(getStickerTabList(), getStickerContentList(), MediaResourcePickerView.STICKER_MODE);
//
//            mResourcePickerView.setCurItem(mStickerTabIndex);
//        }
//    }
//
//    private void showBeautyDatasWhenPickerShown() {
//        // 如果网络数据获取到了，就更新网络数据
//        if (!mBeautyFromNetInit && mBeautyRequestOk) {
//            mBeautyFromNetInit = true;
//            mBeautyFromCacheInit = true;
//            mResourcePickerView.setDatas(getBeautyTabList(), getBeautyContentList(), MediaResourcePickerView.BEAUTY_MODE);
//            mResourcePickerView.setCurItem(mBeautyTabIndex);
//            // 如果已经获取到了网络数据，就没有必要先更新cache，再更新网络数据了
//            return;
//        }
//
//        // 如果缓存数据已经获取到了，就更新缓存的数据
//        if (!mBeautyFromCacheInit) {
//            mBeautyFromCacheInit = true;
//            mResourcePickerView.setDatas(getBeautyTabList(), getBeautyContentList(), MediaResourcePickerView.BEAUTY_MODE);
//            mResourcePickerView.setCurItem(mBeautyTabIndex);
//        }
//    }
//
//    private int getBeautyPageIndex() {
//        return getPageIndex(getBeautyContentList(), mCacheBeautyPageBuryCode);
//    }
//
//    private int getStickerPageIndex() {
//        return getPageIndex(getStickerContentList(), mCacheStickerPageBuryCode);
//    }
//
//    private int getPageIndex(ArrayList<PageAdapter.PageContentData> list, String buryCode) {
//        // size小于1，就返回0
//        if (list.size() < 1) {
//            return 0;
//        }
//        // 如果得到的buryCode是空，默认返回0
//        if (TextUtils.isEmpty(buryCode)) {
//            return 1;
//        } else {
//            // 对比一下当前的contentList的所有栏目，如果栏目的buryCode 有和 cache 得到的 buryCode 一样的话，
//            // 返回栏目的 index，否则，返回 1
//            for (int i = 0; i < list.size(); i++) {
//                if (buryCode.equals(list.get(i).mBuryCode)) {
//                    return i;
//                }
//            }
//        }
//        return -1;
//    }
//
//    private void updateStickerDatas() {
//        // 如果已经从缓存中获取到了数据，且已经更新UI，这个时候不在做处理
//        if (!mStickerFromNetInit) {
//            getStickerDatasFromCache(new Callback<Object>() {
//                @Override
//                public void onCallback(Object pValue) {
//                    // 获取到缓存数据之后，更新UI，同时获取缓存数据
//                    // 如果弹窗已经完全显示，这个时候显示数据
//                    if (mResourcePickerShown) {
//                        mStickerFromCacheInit = true;
//                        mResourcePickerView.setDatas(getStickerTabList(), getStickerContentList(), MediaResourcePickerView.STICKER_MODE);
//                    }
//                    // 开始请求网络数据
//                    updateStickerDataFromNet();
//                }
//            });
//        }
//        // 这种情况是取到了缓存数据，但是没有取到网络数据，尝试再去取一次
//        else if (!mBeautyFromNetInit) {
//            // 开始请求网络数据
//            updateStickerDataFromNet();
//        }
//    }
//
//    private void updateStickerDataFromNet() {
//        // 如果已经从网络取到了数据且已经更新了，不在做处理
//        if (mStickerFromNetInit) return;
//        getStickerDatasFromNet(new Callback<Boolean>() {
//            @Override
//            public void onCallback(Boolean pValue) {
//                // 如果网络请求回来，发现 resourcePicker 已经完全弹起，这个时候，可以直接更新数据
//                if (mResourcePickerShown && pValue) {
//                    mStickerFromNetInit = true;
//                    mResourcePickerView.setDatas(getStickerTabList(), getStickerContentList(), MediaResourcePickerView.STICKER_MODE);
//                    mResourcePickerView.setCurItem(mStickerTabIndex);
//                }
//            }
//        });
//
//    }
//
//    public void dismissResourcePicker(int closeTopValue) {
//        if (mMode == STICKER_MODE) {
//            mStickerTabIndex = mResourcePickerView.getCurrentPageIndex();
//        } else if (mMode == BEAUTY_MODE) {
//            mBeautyTabIndex = mResourcePickerView.getCurrentPageIndex();
//        }
//        mResourcePickerShown = false;
//        mResourcePickerView.setVisibility(View.VISIBLE);
//        ValueAnimator animator = mResourcePickerView.getDismissAnimator(closeTopValue);
//
//        animator.removeAllListeners();
//        animator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                mAnimating = false;
//                mResourcePickerView.setVisibility(View.GONE);
//                if (mDismissCallback != null) {
//                    mDismissCallback.onMaterialPickerClose();
//                }
//            }
//
//            @Override
//            public void onAnimationStart(Animator animation) {
//                mAnimating = true;
//            }
//        });
//        animator.start();
//    }
//
//    public void addStickerToRecentContent(StickerData stickerData) {
//        if (stickerData == null) return;
//        // 这里由于第一次构建的时候，新建了一个空的sticker，这里添加sticker到“最近”里面的时候，把这个自己
//        // 构建的假数据清除掉
//        if (mRecentStickerContent != null
//                && mRecentStickerContent.mContentList != null
//                && mRecentStickerContent.mContentList.size() > 0
//                && mRecentStickerContent.mContentList.get(0) != null
//                && TextUtils.isEmpty(mRecentStickerContent.mContentList.get(0).getDownloadUrl())) {
//            mRecentStickerContent.mContentList.remove(0);
//        }
//        int index = positionOnStickerRecent(stickerData);
//        if (index == -1) {
//            mRecentStickerContent.mContentList.add(0, stickerData);
//            // 最多添加28个sticker，如果这个sticker插入到队列中，发现数量已经大于28，把最后一个sticker移除
//            if (mRecentStickerContent.mContentList.size() > MAX_STICKER_RECENT_COUNT) {
//                mRecentStickerContent.mContentList.remove(MAX_STICKER_RECENT_COUNT - 1);
//            }
//            mResourcePickerView.notifyDataSetChange();
//        } else {
//            // 把这个 data remove 掉
//            mRecentStickerContent.mContentList.remove(index);
//            // 在把这个 data 添加到第 0 个位置
//            mRecentStickerContent.mContentList.add(0, stickerData);
//            mResourcePickerView.notifyDataSetChange();
//        }
//    }
//
//    public void addBeautyContentToRecentContent(BaseData data) {
//        if (data == null) return;
//        int index = positionOnBeautyRecent(data);
//        if (index == -1) {
//            mRecentBeautyContent.mContentList.add(0, data);
//            if (mRecentBeautyContent.mContentList.size() > MAX_BEAUTY_RECENT_COUNT) {
//                mRecentBeautyContent.mContentList.remove(MAX_BEAUTY_RECENT_COUNT - 1);
//            }
//            mResourcePickerView.notifyDataSetChange();
//        } else {
//            // 把这个 data remove 掉
//            mRecentBeautyContent.mContentList.remove(index);
//            // 在把这个 data 添加到第 0 个位置
//            mRecentBeautyContent.mContentList.add(0, data);
//            mResourcePickerView.notifyDataSetChange();
//        }
//    }
//
//    /**
//     * 判断StickerData在最近使用队列中的具体位置
//     *
//     * @return -1 表示stickerData在最近使用中不存在
//     */
//    public int positionOnStickerRecent(StickerData data) {
//        if (data == null || mStickerContentList == null || mRecentStickerContent == null
//                || mRecentStickerContent.mContentList == null
//                || mRecentStickerContent.mContentList.size() == 0) return -1;
//
//        for (int i = 0; i < mRecentStickerContent.mContentList.size(); i++) {
//            BaseData tempData = mRecentStickerContent.mContentList.get(i);
//            if (tempData instanceof StickerData && ((StickerData) tempData).bury_code.equals(data.bury_code)) {
//                return i;
//            }
//        }
//        return -1;
//    }
//
//    public int positionOnBeautyRecent(BaseData data) {
//        if (data == null || mBeautyContentList == null || mRecentBeautyContent == null
//                || mRecentBeautyContent.mContentList == null
//                || mRecentBeautyContent.mContentList.size() == 0) return -1;
//
//        for (int i = 0; i < mRecentBeautyContent.mContentList.size(); i++) {
//            BaseData tempData = mRecentBeautyContent.mContentList.get(i);
//            if (tempData instanceof PhotoFrameData && data instanceof PhotoFrameData) {
//                if (!TextUtils.isEmpty(((PhotoFrameData) data).bury_code)
//                        && ((PhotoFrameData) data).bury_code.equals(((PhotoFrameData) tempData).bury_code))
//                return i;
//            } else if (tempData instanceof FilterData && data instanceof FilterData) {
//                if (!TextUtils.isEmpty(((FilterData) data).filterType)
//                        && ((FilterData) tempData).filterType.equals(((FilterData) data).filterType)) {
//                    return i;
//                }
//            }
//        }
//        return -1;
//    }
//
//    public synchronized ArrayList<PageAdapter.TabData> getBeautyTabList() {
//        return mBeautyTabList;
//    }
//
//    public synchronized ArrayList<PageAdapter.PageContentData> getBeautyContentList() {
//        return mBeautyContentList;
//    }
//
//    public synchronized ArrayList<PageAdapter.TabData> getStickerTabList() {
//        return mStickerTabList;
//    }
//
//    public synchronized ArrayList<PageAdapter.PageContentData> getStickerContentList() {
//        return mStickerContentList;
//    }
//
//    public boolean isInited() {
//        return mInited;
//    }
//
//    public ArrayList<PhotoFrameData> getPhotoFrameList() {
//        return mPhotoFrameList;
//    }
//
//    @Override
//    public void onClose(int top) {
//        mResourcePickerView.resetData();
//        dismissResourcePicker(top);
//    }
//
//    @Override
//    public void onViewPositionChange(int top, float mDragOffset) {
//
//    }
//
//    @Override
//    public void onBeginDrag() {
//
//    }
//
//    public void onDetachFromWindow() {
//        mResourcePickerView.destroyDrawingCache();
//    }
//
//    public interface MaterialCloseListener {
//        void onMaterialPickerClose();
//    }
//}
