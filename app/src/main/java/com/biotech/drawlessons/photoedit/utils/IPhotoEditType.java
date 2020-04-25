package com.biotech.drawlessons.photoedit.utils;

/**
 * Created by xintu on 2018/2/7.
 */

public interface IPhotoEditType {
    int BRUSH_STICKERS = 2;

    int BRUSH_LIGHT_COLOR = 3;

    int BRUSH_NORMAL_COLOR = 4;

    int BRUSH_BACKGROUND = 5;

    int PHOTO_FRAME_DRAW = 6;

    int BRUSH_MOSAICS = 7;

    int BRUSH_BLOCK_MOSAICS = 8;

    int STICKER_TEXT = 100;

    int STICKER_BITMAP = 101;

    int CROP = 102;

    int LAYER_IDL = 102;

    int LAYER_BASE_BITMAP = 103;

    int LAYER_MOSAICS = 104;

    int LAYER_SECOND_INTERNAL_BITMAP = 105;

    int LAYER_INTERNAL_BITMAP = 106;

    int LAYER_PHOTO_FRAME = 107;

    int LAYER_BRUSH = 108;

    int LAYER_LIGHT_LINE_BRUSH = 109;

    int LAYER_BACKGROUND_BRUSH = 110;

    int LAYER_STICKER = 111;

    int LAYER_CROP = 112;

    int FILTER_TYPE = 1000;


    int getType();

    int getOrderNumber();



    enum Order {
        MOSAICS(0), PHOTO_FRAME(1), BRUSH(2), STICKER(3);
        private int order;

        Order(int code) {
            this.order = code;
        }

        public int getOrder() {
            return order;
        }
    }
}
