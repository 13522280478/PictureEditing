//package com.biotech.drawlessons.photoedit.utils;
//
//import android.text.TextUtils;
//
//
//import java.io.Serializable;
//import java.lang.reflect.Type;
//
///**
// * Created by xintu on 2018/3/15.
// * 根据素材的bean（MaterialStickerBean等）转换的用于存储的bean
// */
//
//public class MaterialSerialBean implements Serializable {
////    private Class clazz;
//
//    private String contentJs;
//    private int type;
////    public Class getClazz() {
////        return clazz;
////    }
//
//    public static MaterialSerialBean buildMaterialSerialBean(PhotoFrameData data) {
//        if (data == null) return null;
//        MaterialSerialBean history = new MaterialSerialBean();
//        history.contentJs = GsonUtils.objToStr(data);
//        history.type = Data.PHOTO_FRAME_TYPE;
//
//        return history;
//    }
//
//    public static MaterialSerialBean buildMaterialSerialBean(StickerData data) {
//        if (data == null) return null;
//        MaterialSerialBean history = new MaterialSerialBean();
//        history.contentJs = GsonUtils.objToStr(data);
//        history.type = Data.STICKER_TYPE;
//        return history;
//    }
//
//    public static MaterialSerialBean buildMaterialSerialBean(FilterData data) {
//        if (data == null) return null;
//        MaterialSerialBean history = new MaterialSerialBean();
//        history.contentJs = GsonUtils.objToStr(data);
//        history.type = Data.IMAGE_FILTER_TYPE;
//        return history;
//    }
//
//    public void setMatrialTyep(int type) {
//        this.type = type;
//    }
//
//    public int getType() {
//        return type;
//    }
//
//    public BaseData convertJsToContent() {
//        if (!TextUtils.isEmpty(contentJs)) {
//            switch (type) {
//                case Data.CATEGORY_LINE:
//                case Data.PHOTO_FRAME_TYPE:
//                    // JSON串转为ArrayList<PageAdapter.PageContentData>
//                    try {
//                        Type listType = new TypeToken<PhotoFrameData>() {
//                        }.getType();
//                        return new Gson().fromJson(contentJs, listType);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    break;
//
//                case Data.STICKER_TYPE:
//                    try {
//                        Type listType = new TypeToken<StickerData>() {
//                        }.getType();
//                        return new Gson().fromJson(contentJs, listType);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                case Data.IMAGE_FILTER_TYPE:
//                    try {
//                        Type listType = new TypeToken<FilterData>() {
//                        }.getType();
//                        return new Gson().fromJson(contentJs, listType);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    break;
//            }
//        }
//
//        return null;
//    }
//}
