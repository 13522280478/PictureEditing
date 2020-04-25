package com.biotech.drawlessons.photoedit.draws;

import android.graphics.Matrix;

import com.biotech.drawlessons.photoedit.utils.BitmapsManager;
import com.biotech.drawlessons.photoedit.utils.IPhotoEditType;

/**
 * Created by xintu on 2018/3/30.
 */

public class PhotoFrameDraw extends BasePhotoDraw {
    public PhotoFrameDraw(Matrix matrix, BitmapsManager manager) {
        super(IPhotoEditType.PHOTO_FRAME_DRAW, matrix, DrawOnWhere.DRAW_ON_SECOND_TEMP_BITMAP, manager);
    }

    public static PhotoFrameDraw createPhotoDraw(BaseDrawData data, Matrix matrix, BitmapsManager manager){
        PhotoFrameDraw draw = new PhotoFrameDraw(matrix, manager);
        draw.setPhoto(data.bitmapUrl);
        return draw;
    }
}
