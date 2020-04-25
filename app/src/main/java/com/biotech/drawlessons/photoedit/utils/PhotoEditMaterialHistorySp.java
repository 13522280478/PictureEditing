//package com.biotech.drawlessons.photoedit.utils;
//
//import android.content.Context;
//
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.sohu.kan.bussinessmodels.database.sharedpreferences.SharedPreferenceUtility;
//import com.sohu.kan.common.utils.GsonUtils;
//import com.sohu.kan.common.utils.MD5Builder;
//import com.sohu.kan.controllers.photoedit.resourcepicker.custom.PageAdapter;
//import com.sohu.kan.controllers.photoedit.resourcepicker.custom.recyclerview.model.BaseData;
//import com.sohu.kan.controllers.photoedit.resourcepicker.custom.recyclerview.model.Data;
//import com.sohu.kan.controllers.photoedit.resourcepicker.custom.recyclerview.model.FilterData;
//import com.sohu.kan.controllers.photoedit.resourcepicker.custom.recyclerview.model.PhotoFrameData;
//import com.sohu.kan.controllers.photoedit.resourcepicker.custom.recyclerview.model.StickerData;
//
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//
//
//public class PhotoEditMaterialHistorySp extends SharedPreferenceUtility {
//
//    // 出现在历史记录中的最低使用频率
//    public static final int FREQUENCY_MIN_HISTORY = 1;
//    public static final int FREQUENCY_ALL_HISTORY = 0;
//    /**
//     * 需要的表情类型-所有表情
//     */
//    public static final int HISTORY_TYPE_ALL = 10;
//    /**
//     * 需要的表情类型-仅系统表情
//     */
//    public static final int HISTORY_TYPE_ONLY_SYSTEM = 11;
//    // 存储美化按钮的最近使用key
//    private static final String KEY_BEAUTY_HISTORY = "key_beauty_history";
//    private static final String KEY_STICKER_PAGES_CONTENTS = "key_stickers_pages_contents";
//    private static final String KEY_STICKER_TABS = "key_sticker_tabs";
//    private static final String KEY_STICKER_PAGE_BURY_CODE = "key_sticker_page_bury_code";
//    private static final String KEY_BEAUTY_PAGE_BURY_CODE = "key_beauty_page_bury_code";
//    private static final String KEY_BEAUTY_TABS = "key_beauty_tabs";
//    private static final String KEY_BEAUTY_PAGES_CONTENTS = "key_beauty_pages_content";
//    private static final String ENDTIME = "ENDTIME";
//    private static final String DEFAULT_VALUE = "";
//    // 当用户第一次进来默认显示第1页，即Emoji的第1页(不是收藏夹)
//    private static final int DEFAULT_INDEX = 1;
//    // 最多可以取出满足条件的最大数量
//    private static final int MAX_HISTORY_COUNT = 23;
//    private String mUserId;
//
//
//    public PhotoEditMaterialHistorySp(Context context, String userId) {
//        super(context, MD5Builder.getMD5("PhotoEditMaterialHistorySp_" + userId));
//        this.mUserId = userId;
//    }
//
//    public String getUserId() {
//        return mUserId;
//    }
//
//    /**
//     * 获取使用过的美化的历史记录
//     */
//    public PageAdapter.PageContentData getBeautyHistory() {
//        return getContents(KEY_BEAUTY_HISTORY);
//    }
//
//    private PageAdapter.PageContentData getContents(String key) {
//        String value = getValue(key, DEFAULT_VALUE);
//        PageAdapter.PageContentData res = new PageAdapter.PageContentData();
//        res.mContentList = new ArrayList<>();
//
//        try {
//            // JSON串转为ArrayList<MaterialSerialBean>
//            Type listType = new TypeToken<ArrayList<MaterialSerialBean>>() {
//            }.getType();
//            ArrayList<MaterialSerialBean> datas = new Gson().fromJson(value, listType);
//            if (datas != null && datas.size() > 0) {
//                for (MaterialSerialBean history : datas) {
//                    res.mContentList.add(history.convertJsToContent());
//                }
//            }
//            return res;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return res;
//    }
//
//
//    /**
//     * 将当前点击的Emoji加入到历史记录中
//     * 注意，当关键词存在时则移动到最前面 && 最大数量限制
//     * 1.表情进入收藏夹的唯一条件是用户在使用一个表情大于等于1次时，无时间限制
//     * 2.表情一旦进入收藏夹，移出的方式只有满足最大数量后被挤出
//     */
//    private void updateStickerContents(PageAdapter.PageContentData historyList, String key) {
//        ArrayList<MaterialSerialBean> histories = new ArrayList<>();
//        buildMaterialSerialBeanFromPageContent(historyList, histories);
//
//        if (histories.size() > 0) {
//            String js = GsonUtils.objToStr(histories);
//            setValue(key, js);
//        }
//    }
//
//    /**
//     * 根据 PageContent 构建
//     */
//    private void buildMaterialSerialBeanFromPageContent(PageAdapter.PageContentData pageContentData,
//                                                        ArrayList<MaterialSerialBean> dstArrayList) {
//        if (dstArrayList == null) dstArrayList = new ArrayList<>();
//        for (BaseData data : pageContentData.mContentList) {
//            switch (data.getViewItemType()) {
//                // 类别分割线和相框实际上是同一个数据
//                case Data.CATEGORY_LINE:
//                case Data.PHOTO_FRAME_TYPE:
//                    MaterialSerialBean history = MaterialSerialBean.buildMaterialSerialBean(((PhotoFrameData) data));
//                    if (history != null) {
//                        dstArrayList.add(history);
//                    }
//                    break;
//
//                case Data.STICKER_TYPE:
//                    history = MaterialSerialBean.buildMaterialSerialBean((StickerData) data);
//                    if (history != null) {
//                        dstArrayList.add(history);
//                    }
//                    break;
//
//                case Data.IMAGE_FILTER_TYPE:
//                    history = MaterialSerialBean.buildMaterialSerialBean((FilterData) data);
//                    if (history != null) {
//                        dstArrayList.add(history);
//                    }
//                    break;
//            }
//        }
//    }
//
//    public void updateBeautyHistories(PageAdapter.PageContentData historyList) {
//        updateStickerContents(historyList, KEY_BEAUTY_HISTORY);
//    }
//
//    /**
//     * 更新美化tab的数据
//     */
//    public void updateBeautyTabs(ArrayList<PageAdapter.TabData> tabData) {
//        updateTabs(tabData, KEY_BEAUTY_TABS);
//    }
//
//    /**
//     * 更新 sticker 的 tab 数据
//     */
//    public void updateStickerTabs(ArrayList<PageAdapter.TabData> tabData) {
//        updateTabs(tabData, KEY_STICKER_TABS);
//    }
//
//    private void updateTabs(ArrayList<PageAdapter.TabData> tabData, String key) {
//        if (tabData == null) return;
//        String js = GsonUtils.objToStr(tabData);
//        setValue(key, js);
//    }
//
//    /**
//     * 获取 sticker 的 tab数据
//     */
//    public ArrayList<PageAdapter.TabData> getStickerTabs() {
//        return getTabs(KEY_STICKER_TABS);
//    }
//
//    /**
//     * 获取美化的 tab 数据
//     */
//    public ArrayList<PageAdapter.TabData> getBeautyTabs() {
//        return getTabs(KEY_BEAUTY_TABS);
//    }
//
//    private ArrayList<PageAdapter.TabData> getTabs(String key) {
//        String value = getValue(key, DEFAULT_VALUE);
//        ArrayList<PageAdapter.TabData> res = null;
//        try {
//            // JSON串转为 ArrayList<ArrayList<MaterialSerialBean>>
//            Type listType = new TypeToken<ArrayList<PageAdapter.TabData>>() {
//            }.getType();
//            res = new Gson().fromJson(value, listType);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return res;
//    }
//
//    public void updateStickerPageBuryCode(String buryCode) {
//        setValue(KEY_STICKER_PAGE_BURY_CODE, buryCode);
//    }
//
//    public String getStickerPageBuryCode() {
//        return getValue(KEY_STICKER_PAGE_BURY_CODE, DEFAULT_VALUE);
//    }
//
//    public void updateBeautyPageBuryCode(String buryCode) {
//        setValue(KEY_BEAUTY_PAGE_BURY_CODE, buryCode);
//    }
//
//    public String getBeautyPageBuryCode() {
//        return getValue(KEY_BEAUTY_PAGE_BURY_CODE, DEFAULT_VALUE);
//    }
//
//    /**
//     * 更新sticker的content数据
//     */
//    public void updateStickerPageContents(ArrayList<PageAdapter.PageContentData> stickerCache) {
//        updatePageContents(stickerCache, KEY_STICKER_PAGES_CONTENTS);
//    }
//
//    /**
//     * 更新美化的 content 数据
//     */
//    public void updateBeautyPageContents(ArrayList<PageAdapter.PageContentData> stickerCache) {
//        updatePageContents(stickerCache, KEY_BEAUTY_PAGES_CONTENTS);
//    }
//
//    private void updatePageContents(ArrayList<PageAdapter.PageContentData> stickerCache, String key) {
//        if (stickerCache == null) return;
//        ArrayList<ArrayList<MaterialSerialBean>> contentList = new ArrayList<>();
//
//        for (PageAdapter.PageContentData contentData : stickerCache) {
//            ArrayList<MaterialSerialBean> pageDatas = new ArrayList<>();
//            // 构建page的serial数据
//            buildMaterialSerialBeanFromPageContent(contentData, pageDatas);
//            contentList.add(pageDatas);
//        }
//
//        if (contentList.size() > 0) {
//            String js = GsonUtils.objToStr(contentList);
//            setValue(key, js);
//        }
//    }
//
//    /**
//     * 获取sticker的content数据
//     */
//    public ArrayList<PageAdapter.PageContentData> getStickersPagesContents() {
//        return getPagesContents(KEY_STICKER_PAGES_CONTENTS);
//    }
//
//    public ArrayList<PageAdapter.PageContentData> getBeautyPagesContents() {
//        return getPagesContents(KEY_BEAUTY_PAGES_CONTENTS);
//    }
//
//    private ArrayList<PageAdapter.PageContentData> getPagesContents(String key) {
//        String value = getValue(key, DEFAULT_VALUE);
//        ArrayList<PageAdapter.PageContentData> res = new ArrayList<>();
//        try {
//            // JSON串转为 ArrayList<ArrayList<MaterialSerialBean>>
//            Type listType = new TypeToken<ArrayList<ArrayList<MaterialSerialBean>>>() {
//            }.getType();
//            ArrayList<ArrayList<MaterialSerialBean>> datas = new Gson().fromJson(value, listType);
//
//            // 得到了 ArrayList<ArrayList<MaterialSerialBean>>之后，需要把每一个 ArrayList<MaterialSerialBean>
//            // 转换为 对应的 pageContentData，再把 pageContentData 添加到 res 队列中
//
//            if (datas != null && datas.size() > 0) {
//                for (ArrayList<MaterialSerialBean> pageContents : datas) {
//                    // 新建一个pageContent的数据
//                    PageAdapter.PageContentData pageContentData = new PageAdapter.PageContentData();
//                    // 新建一个list
//                    pageContentData.mContentList = new ArrayList<>();
//
//                    for (MaterialSerialBean bean : pageContents) {
//                        // 把获取到的js串转换为data数据
//                        BaseData data = bean.convertJsToContent();
//                        if (data != null) {
//                            // 把data数据添加到队列中
//                            pageContentData.mContentList.add(data);
//                        }
//                    }
//                    // 把 pageContentData 数据添加到 res 的队列中
//                    res.add(pageContentData);
//                }
//            }
//            return res;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return res;
//    }
//}
//
//
//
//
//
//
