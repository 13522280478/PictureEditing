package com.biotech.drawlessons.photoedit.utils;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import com.biotech.drawlessons.BaseApplication;
import com.biotech.drawlessons.photoedit.draws.BaseBrush;
import com.biotech.drawlessons.photoedit.draws.BaseDraw;
import com.biotech.drawlessons.photoedit.draws.BaseSticker;
import com.biotech.drawlessons.photoedit.draws.BitmapSticker;
import com.biotech.drawlessons.photoedit.draws.IDraw;
import com.biotech.drawlessons.photoedit.draws.LightLineBrush;
import com.biotech.drawlessons.photoedit.draws.PhotoFrameDraw;
import com.biotech.drawlessons.photoedit.draws.StickerBrush;
import com.biotech.drawlessons.photoedit.draws.TextSticker;
import com.biotech.drawlessons.photoedit.utilsmodel.IColorPicker;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by xintu on 2017/12/4.
 */

public class DrawInvoker {
    public static final int MAX_BITMAP_STICKER_COUNT = 20;
    public static final int MAX_LIGHT_LINE_BRUSH_COUNT = 30;
    public static final int MAX_STICKER_BRUSH_COUNT = 30;
    private LinkedList<BaseBrush> mBrushList = new LinkedList<>();
    private LinkedList<BaseSticker> mStickerList = new LinkedList<>();
    // photoFrame本质上也是一个brush，把他放入brush分支，可以更简单的处理排序的问题
    // 副作用就是不容易理解
    private PhotoFrameDraw mPhotoFrame;
    private BitmapsManager mBitmapManager;
    private boolean mCanUndo;
    private int mTextStickerTotalLength;
    private int mBitmapStickerCount;
    private int mLightLineCount;
    private int mStickerBrushCount;
    private IColorPicker mIColorPicker;

    public DrawInvoker(BitmapsManager manager) {
        mBitmapManager = manager;
    }

    public void addBrushAndOrder(BaseBrush drawPath) {
        if (drawPath == null) {
            return;
        }
        addBrush(drawPath);
        orderBrushes();
    }

    public void addBrush(BaseBrush baseBrush) {
        if (baseBrush == null) {
            return;
        }
        mBrushList.add(baseBrush);
        baseBrush.setIndexInList(mPhotoFrame == null ? mBrushList.size() : mBrushList.size() - 1);
        baseBrush.setTouchUp();

        if (baseBrush instanceof LightLineBrush) {
            mLightLineCount++;
        } else if (baseBrush instanceof StickerBrush) {
            mStickerBrushCount++;
        }
        // 如果添加的是相框的数据，不能回退
        else if (baseBrush instanceof PhotoFrameDraw) {
            return;
        }

        if (!mCanUndo) {
            mCanUndo = true;
            if (mIColorPicker != null) {
                mIColorPicker.onUndoStateChange(true);
            }
        }
    }

    public void setPhotoFrame(PhotoFrameDraw photoFrame) {
        if (photoFrame == null) {
            return;
        }
        if (mPhotoFrame != null) {
            mBrushList.remove(mPhotoFrame);
        }
        mPhotoFrame = photoFrame;
        addBrushAndOrder(mPhotoFrame);
    }

    public boolean hasPhotoFrame() {
        return mPhotoFrame != null;
    }

    public void setIColorPicker(IColorPicker iColorPicker) {
        mIColorPicker = iColorPicker;
    }

    public boolean hasSticker() {
        return mStickerList.size() > 0;
    }

    public boolean hasBrush() {
        return mBrushList.size() > 0;
    }

    public LinkedList<BaseBrush> getBrushList() {
        return mBrushList;
    }

    public LinkedList<BaseSticker> getStickerList() {
        return mStickerList;
    }

    public void removeAllDraws() {
        mBrushList.clear();
        mStickerList.clear();
    }

    public void drawBrush(Canvas canvas) {
        Iterator<BaseBrush> iterator = mBrushList.iterator();
        while (iterator.hasNext()) {
            IDraw draw = iterator.next();
            draw.draw(canvas);
        }
    }

    public void drawLastBrushOnSecondBitmap() {
        mBitmapManager.ensureSecondTempBitmap();
        Canvas canvas = mBitmapManager.getSecondTempCanvas();

        if (mBrushList.size() > 0) {
            BaseBrush draw = mBrushList.get(mBrushList.size() - 1);
            draw.draw(canvas);
            draw.setDrawOnWhere(BaseDraw.DrawOnWhere.DRAW_ON_SECOND_TEMP_BITMAP);
        }
    }

    public void drawBrush(Canvas canvas, BaseBrush iBrush) {
        if (iBrush != null && canvas != null) {
            iBrush.draw(canvas);
        }
    }

    public void undo() {
        Iterator<BaseBrush> iterator = mBrushList.listIterator();
        BaseDraw draw = null;
        int lastIndex = mPhotoFrame == null ? mBrushList.size() : mBrushList.size() - 1;

        while (iterator.hasNext()) {
            draw = iterator.next();
            if ((draw.getIndexInList() == lastIndex) && !(draw instanceof PhotoFrameDraw)){
                iterator.remove();
                break;
            }
        }
        if (draw != null && draw.getDrawOnWhere() == BaseDraw.DrawOnWhere.DRAW_ON_INTERNAL_BITMAP) {
            mBitmapManager.erasInternalBitmap();
            redrawAllBrushOnInternalBitmap();
        } else if (draw != null && draw.getDrawOnWhere() == BaseDraw.DrawOnWhere.DRAW_ON_SECOND_TEMP_BITMAP) {
            mBitmapManager.erasSecondTempBitmap();
            redrawAllBrushOnSecondBitmap();
        }

        if (draw instanceof LightLineBrush) {
            mLightLineCount--;
        } else if (draw instanceof StickerBrush) {
            mStickerBrushCount--;
        }

        // 如果 brushList 不包含数据，或者只包含一条 photoFrame 的数据，这个时候，就可以 undo
        if ((mBrushList.size() == 1 && mBrushList.get(0) instanceof PhotoFrameDraw) || mBrushList.size() == 0) {
            mCanUndo = false;
            if (mIColorPicker != null) {
                mIColorPicker.onUndoStateChange(false);
            }
        }
    }

    public void drawLastBrushOnInternalBitmap() {
        drawBrushOnInternalBitmap(getLastBrush());
    }

    public void drawBrushOnInternalBitmap(BaseBrush brush) {
        drawBrush(mBitmapManager.getInternalCanvas(), brush);
    }

    public void drawBrushOnSecondTempBitmap(BaseBrush brush) {
        drawBrush(mBitmapManager.getSecondTempCanvas(), brush);
    }

    // todo:所有的 drawAll 方法全部需要重写
    public void drawAllMosaicsOnSecondTempBitmap() {
        Canvas canvas = mBitmapManager.getSecondTempCanvas();
        Iterator<BaseBrush> iterator = mBrushList.iterator();
        while (iterator.hasNext()) {
            BaseBrush baseBrush = iterator.next();
            if (baseBrush.getOrder() == IPhotoEditType.Order.MOSAICS) {
                baseBrush.draw(canvas);
                baseBrush.setDrawOnWhere(BaseDraw.DrawOnWhere.DRAW_ON_SECOND_TEMP_BITMAP);
            } else {
                return;
            }
        }
    }

    public void drawAllMosaicsOnBitmap(Canvas canvas) {
        for (BaseBrush brush : mBrushList) {
            if (brush.getOrder() == IPhotoEditType.Order.MOSAICS) {
                brush.draw(canvas);
            } else {
                return;
            }
        }
    }

    public void drawBrushWithoutMosaicsOnInternalBitmap() {
        Canvas canvas = mBitmapManager.getInternalCanvas();
        for (BaseBrush brush : mBrushList) {
            if (brush.getOrder() != IPhotoEditType.Order.MOSAICS) {
                brush.draw(canvas);
            }
        }
    }

    public void drawAllBrushSecondBitmap() {
        Canvas canvas = mBitmapManager.getSecondTempCanvas();
        for (BaseBrush brush : mBrushList) {
            brush.draw(canvas);
        }
    }

    public void setAllMosaicBrushDrawOnInternal() {
        Iterator<BaseBrush> iterator = mBrushList.iterator();
        while (iterator.hasNext()) {
            BaseBrush baseBrush = iterator.next();
            if (baseBrush.getType() == IPhotoEditType.BRUSH_BLOCK_MOSAICS
                    || baseBrush.getType() == IPhotoEditType.BRUSH_MOSAICS) {
                baseBrush.setDrawOnWhere(BaseDraw.DrawOnWhere.DRAW_ON_INTERNAL_BITMAP);
            } else if (baseBrush.getOrder() != IPhotoEditType.Order.MOSAICS) {
                return;
            }
        }
    }

    private void redrawAllBrushOnInternalBitmap() {
        Iterator<BaseBrush> iterator = mBrushList.iterator();
        Canvas canvas = mBitmapManager.getInternalCanvas();
        while (iterator.hasNext()) {
            BaseBrush baseBrush = iterator.next();
            if (baseBrush.getDrawOnWhere() == BaseDraw.DrawOnWhere.DRAW_ON_INTERNAL_BITMAP) {
                baseBrush.draw(canvas);
            }
        }
    }

    private void redrawAllBrushOnSecondBitmap() {
        Iterator<BaseBrush> iterator = mBrushList.iterator();
        Canvas canvas = mBitmapManager.getSecondTempCanvas();
        while (iterator.hasNext()) {
            BaseBrush baseBrush = iterator.next();
            if (baseBrush.getDrawOnWhere() == BaseDraw.DrawOnWhere.DRAW_ON_SECOND_TEMP_BITMAP) {
                baseBrush.draw(canvas);
            }
        }
    }

    public void removeUselessStickerBitmap(String stickerText, int color) {
        Iterator<BaseSticker> iterator = mStickerList.iterator();
        boolean hasStickerUse = false;
        while (iterator.hasNext()) {
            BaseSticker sticker = iterator.next();
            if (sticker instanceof TextSticker) {
                if (((TextSticker) sticker).getText().equals(stickerText)) {
                    hasStickerUse = true;
                }
            }
        }
        if (!hasStickerUse) {
            mBitmapManager.removeBitmap(BitmapsManager.generateTextStickerUri(stickerText, color));
        }
    }


    public BaseBrush getLastBrush() {
        if (mBrushList == null || mBrushList.size() == 0) {
            return null;
        }

        return mBrushList.get(mBrushList.size() - 1);
    }

    public void orderBrushes() {
        if (mBrushList.size() < 2) {
            return;
        }
        Collections.sort(mBrushList, new Comparator<BaseBrush>() {
            @Override
            public int compare(BaseBrush o1, BaseBrush o2) {
                if (o1.getOrderNumber() == o2.getOrderNumber()) {
                    return o1.getIndexInList() - o2.getIndexInList();
                }

                // 如果是同一个 type，order 大的优先级高
                return o1.getOrderNumber() - o2.getOrderNumber();
            }
        });
    }

    public void drawPhotoFrame() {
        if (mPhotoFrame == null) return;
        mBitmapManager.ensureSecondTempBitmap();
        mBitmapManager.erasSecondTempBitmap();
        mBitmapManager.erasInternalBitmap();
        // 1.把比 photoFrame 优先级低的 draws 绘制在 secondBitmap 上
        drawUnderOrderBrushOnSecondBitmap(IPhotoEditType.Order.PHOTO_FRAME, true);
        // 2.把比 photoFrame 优先级高的 draws 绘制在 internalBitmap 上
        drawUpperOrderBrushOnInternalBitmap(IPhotoEditType.Order.PHOTO_FRAME, false);
    }

    // 清除图片上的photoFrame，感觉这些方法步骤应该直接写成方法的...
    public void removePhotoFrame() {
        if (mPhotoFrame == null) {
            return;
        }
        mBrushList.remove(mPhotoFrame);
        mBitmapManager.ensureSecondTempBitmap();
        mBitmapManager.erasSecondTempBitmap();
        mBitmapManager.erasInternalBitmap();
        // 1.把比 photoFrame 优先级低的 draws 绘制在 secondBitmap 上
        drawUnderOrderBrushOnSecondBitmap(IPhotoEditType.Order.PHOTO_FRAME, false);
        // 2.把比 photoFrame 优先级高的 draws 绘制在 internalBitmap 上
        drawUpperOrderBrushOnInternalBitmap(IPhotoEditType.Order.PHOTO_FRAME, false);
    }

    public void drawUnderOrderBrushOnSecondBitmap(IPhotoEditType.Order order, boolean drawCurOrder) {
        mBitmapManager.ensureSecondTempBitmap();
        Canvas canvas = mBitmapManager.getSecondTempCanvas();
        drawUnderOrderBrush(canvas, order, drawCurOrder, BaseDraw.DrawOnWhere.DRAW_ON_SECOND_TEMP_BITMAP);
    }

    public void drawUpperOrderBrushOnInternalBitmap(IPhotoEditType.Order order, boolean drawCurOrder) {
        Canvas canvas = mBitmapManager.getInternalCanvas();
        drawUpperOrderBrush(canvas, order, drawCurOrder, BaseDraw.DrawOnWhere.DRAW_ON_INTERNAL_BITMAP);
    }

    /**
     * 把某一特定order的之下的所有笔刷绘制在secondBitmap上
     *
     * @param order        特定的order
     * @param drawCurOrder 是否绘制当前 order 类型的笔刷
     */
    private void drawUnderOrderBrush(Canvas canvas, IPhotoEditType.Order order, boolean drawCurOrder,
                                    BaseDraw.DrawOnWhere drawOnWhere) {
        int dstOrderNumber = order.getOrder();
        int mCurOrderNumber;

        for (BaseDraw baseDraw : mBrushList) {
            mCurOrderNumber = baseDraw.getOrderNumber();
            if (mCurOrderNumber < dstOrderNumber) {
                baseDraw.draw(canvas);
                baseDraw.setDrawOnWhere(drawOnWhere);

            } else if (mCurOrderNumber == dstOrderNumber) {
                if (drawCurOrder) {
                    baseDraw.draw(canvas);
                    baseDraw.setDrawOnWhere(drawOnWhere);

                } else {
                    break;
                }
            } else if (mCurOrderNumber > dstOrderNumber) {
                break;
            }
        }
    }

    private void drawUpperOrderBrush(Canvas canvas, IPhotoEditType.Order order, boolean drawCurOrder,
                                    BaseDraw.DrawOnWhere drawOnWhere) {
        int dstOrderNumber = order.getOrder();
        int mCurOrderNumber;

        for (BaseDraw baseDraw : mBrushList) {
            mCurOrderNumber = baseDraw.getOrderNumber();
            if (mCurOrderNumber == dstOrderNumber) {
                if (drawCurOrder) {
                    baseDraw.draw(canvas);
                    baseDraw.setDrawOnWhere(drawOnWhere);
                }
            } else if (mCurOrderNumber > dstOrderNumber) {
                baseDraw.draw(canvas);
                baseDraw.setDrawOnWhere(drawOnWhere);
            }
        }
    }

//    /**
//     * 把比自己优先级低（orderNumber比较小）的线段（不包括自己）绘制在bitmap上。
//     * 首先进行排序，优先级低（type比较大）的肯定排在前面。
//     * 当遍历到和自己优先级相同的时候，也应该绘制在 bitmap 上，因为的画笔肯定比前面画笔的顺序靠后。
//     */
//    private void drawUnderBrushOnBitmap(Canvas canvas, BaseBrush draw) {
//        int dstOrderNumber = draw.getOrderNumber();
//        int mCurOrderNumber;
//
//        for (BaseDraw baseDraw : mBrushList) {
//            mCurOrderNumber = baseDraw.getOrderNumber();
//            if (mCurOrderNumber < dstOrderNumber) {
//                baseDraw.draw(canvas);
//            } else if (mCurOrderNumber == dstOrderNumber) {
//                // 如果传过来的是一个brush，这个时候遍历到该brush的时候，就直接break，
//                // 防止把这个brush绘制在canvas上
//                if (baseDraw == draw) {
//                    break;
//                }
//                baseDraw.draw(canvas);
//            }
//            else if (mCurOrderNumber > dstOrderNumber) {
//                break;
//            }
//        }
//    }
//
//    /**
//     * 把比自己优先级高（type比较小）的线段绘制在屏幕上。
//     * 首先进行排序，优先级高（type比较小）的肯定排在后面。
//     * 遍历到 type 比自己小的直接 draw 在 canvas 上。
//     */
//    public void drawUpperBrushOnBitmap(Canvas canvas, BaseBrush brush) {
//        if (brush == null) return;
//
//        int dstOrderNumber = brush.getOrderNumber();
//        int mCurOrderNumber;
//        for (BaseDraw baseDraw : mBrushList) {
//            mCurOrderNumber = baseDraw.getOrderNumber();
//            if (mCurOrderNumber == dstOrderNumber) {
//                if (baseDraw.getIndexInList() > brush.getIndexInList()) {
//                    baseDraw.draw(canvas);
//                }
//            }
//            else if (mCurOrderNumber > dstOrderNumber) {
//                baseDraw.draw(canvas);
//            }
//        }
//    }

    public void drawAllItemsOnSecondBitmap() {
        // 保存当前图片的步骤：
        // 1.把internalBitmap清空
        Canvas canvas = mBitmapManager.getSecondTempCanvas();
        canvas.drawColor(Color.TRANSPARENT);
        // 2.把original的图片绘制到internalBitmap中
        canvas.drawBitmap(mBitmapManager.getOriginalBitmap(), 0, 0, null);
        // 3.把所有的brush和sticker绘制到internalBitmap中
        drawAllBrushSecondBitmap();
        drawAllStickerOnSecondBitmap();
    }

    //============================ sticker 相关的方法==================================
    public void addSticker(BaseSticker sticker) {
        if (sticker == null) {
            return;
        }
        mStickerList.add(sticker);
        sticker.setIndexInList(mStickerList.size());
        clearAllStickerTouched();
        orderStickers();
        if (sticker instanceof TextSticker) {
            mTextStickerTotalLength += ((TextSticker) sticker).getTextLength();
        } else if (sticker instanceof BitmapSticker) {
            mBitmapStickerCount++;
        }
    }

    public void deleteSticker(BaseSticker sticker) {
        if (sticker == null) {
            return;
        }
        mStickerList.remove(sticker);
        if (sticker instanceof TextSticker) {
            mTextStickerTotalLength -= ((TextSticker) sticker).getTextLength();
        } else if (sticker instanceof BitmapSticker) {
            mBitmapStickerCount--;
        }
    }

    public int getBitmapStickerCounts() {
        return mBitmapStickerCount;
    }

    public boolean isLightLineCountValidate() {
        return mLightLineCount < MAX_LIGHT_LINE_BRUSH_COUNT;
    }

    public boolean isStickerBrushCountValidate() {
        return mStickerBrushCount < MAX_STICKER_BRUSH_COUNT;
    }

    public boolean isBitmapStickerCountValidate() {
        return mBitmapStickerCount < MAX_BITMAP_STICKER_COUNT;
    }

    public void updateTextSticker(String oldText, String newText, TextSticker sticker, int newColor, int oldColor) {
        if (sticker == null) {
            return;
        }
        if (((oldText == null && newText == null) || (oldText != null && oldText.equals(newText)))
                && newColor == oldColor) {
            return;
        }

        String textUri = BitmapsManager.generateTextStickerUri(newText, newColor);
        Bitmap bitmap = BitmapUtils.createBitmapFromString(BaseApplication.getInstance(), newText, newColor);
        mBitmapManager.saveBitmap(textUri, bitmap);
        sticker.updateTextStickerStatus(newText, newColor, textUri);
        if (oldText != null && newText != null && !oldText.equals(newText)) {
            removeUselessStickerBitmap(oldText, newColor);
        }
        int newLength = StringUtil.getAdjustTextLength(newText);
        int oldLength = StringUtil.getAdjustTextLength(oldText);
        mTextStickerTotalLength += (newLength - oldLength);
    }

    public void drawAllStickerOnScreen(Canvas canvas) {
        if (mStickerList.size() == 0) {
            return;
        }
        Iterator<BaseSticker> iterator = mStickerList.iterator();
        while (iterator.hasNext()) {
            BaseSticker sticker = iterator.next();
            sticker.setDrawOnWhere(BaseDraw.DrawOnWhere.DRAW_ON_SCREEN);
            sticker.draw(canvas);
        }
    }

    public void drawAllStickerOnSecondBitmap() {
        if (mStickerList.size() == 0) {
            return;
        }
        Canvas canvas = mBitmapManager.getSecondTempCanvas();
        Iterator<BaseSticker> iterator = mStickerList.iterator();
        while (iterator.hasNext()) {
            BaseSticker sticker = iterator.next();
            sticker.setDrawOnWhere(BaseDraw.DrawOnWhere.DRAW_ON_SECOND_TEMP_BITMAP);
            sticker.draw(canvas);
        }
    }

    public BaseSticker getTouchedSticker(float x, float y) {
        if (mStickerList.size() == 0) {
            return null;
        }
        ListIterator<BaseSticker> iterator = mStickerList.listIterator();
        // 先正序遍历，让 iterator 达到队列的尾部
        while (iterator.hasNext()) {
            iterator.next();
        }

        // 倒序遍历，在队列中靠后的 sticker 先响应 touch 事件。
        while (iterator.hasPrevious()) {
            BaseSticker sticker = iterator.previous();
            if (sticker.isTouched(x, y)) {
                return sticker;
            }
        }
        return null;
    }

    public void drawSticker(Canvas canvas, BaseSticker sticker) {
        if (sticker == null || canvas == null) {
            return;
        }
        sticker.draw(canvas);
    }

    public int getTextStickersTotalLength() {
        return mTextStickerTotalLength;
    }

    /**
     * 重置队列中所有 sticker 的 touched 状态。
     * 设置参数中的 sticker.touched = true
     * 队列中其他的 sticker.touched = false
     * 同时重新设置 order，目的是让现在的顺序保持不变
     */
    public void setStickerTouched(BaseSticker sticker) {
        if (!mStickerList.contains(sticker)) {
            return;
        }
        Iterator<BaseSticker> iterator = mStickerList.iterator();
        int index = 1;
        while (iterator.hasNext()) {
            BaseSticker temp = iterator.next();
            temp.setTouched(false);
            temp.setIndexInList(index);
            index++;
        }
        sticker.setTouched(true);
    }

    /**
     * 清空所有 sticker 的 touched 状态，同时需要重新设置order
     * 同时重新设置 order，目的是让现在的顺序保持不变
     */
    public void clearAllStickerTouched() {
        Iterator<BaseSticker> iterator = mStickerList.iterator();
        int index = 1;
        while (iterator.hasNext()) {
            BaseSticker sticker = iterator.next();
            sticker.setTouched(false);
            sticker.setIndexInList(index);
            index++;
        }
    }

    public void orderStickers() {
        if (mStickerList.size() < 2) {
            return;
        }
        Collections.sort(mStickerList, new Comparator<BaseSticker>() {
            @Override
            public int compare(BaseSticker o1, BaseSticker o2) {
                if (o1.getType() - o2.getType() != 0) {
                    return o2.getType() - o1.getType();
                }

                if (o1.getTouched()) {
                    // 如果是 touched 状态，放在队列最前面
                    return 1;
                }
                if (o2.getTouched()) {
                    return -1;
                }

                return o1.getOrderNumber() - o2.getOrderNumber();
            }
        });
    }
}