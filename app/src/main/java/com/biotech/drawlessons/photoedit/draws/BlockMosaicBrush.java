package com.biotech.drawlessons.photoedit.draws;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;

import com.biotech.drawlessons.photoedit.utils.BitmapsManager;
import com.biotech.drawlessons.photoedit.utils.IPhotoEditType;

import java.util.LinkedList;


/**
 * Created by xintu on 2017/12/4.
 * 方块的马赛克，默认在最底层（图片的上一层），绘制的时候，不能覆盖除了马赛克之外的任何笔刷
 */

public class BlockMosaicBrush extends BaseBrush {
    private Paint mPaint;
    private int mMosaicsWH;
    private BitmapsManager mManager;

    public BlockMosaicBrush(Matrix matrix, Paint paint, float strokeWidth, int mosaicsWH, BitmapsManager manager) {
        // 创建的时候，就是直接绘制在 bitmap 上
        super(IPhotoEditType.BRUSH_BLOCK_MOSAICS, matrix, DrawOnWhere.DRAW_ON_INTERNAL_BITMAP);
        mPaint = paint;
        mStroke = strokeWidth;
        mMosaicsWH = mosaicsWH;
        mManager = manager;
        mDrawData.strokeWidth = strokeWidth;
    }

//    public BlockMosaicBrush(BaseDrawData drawData, Matrix matrix, Paint paint, int mosaicsWH, BitmapsManager manager) {
//        // 创建的时候，就是直接绘制在 bitmap 上
//        this(matrix, paint, drawData.strokeWidth, mosaicsWH, manager);
//        // 新建path
//        mPath = new Path();
//        linkPathByDrawData(mPath, drawData);
//    }

    public static BlockMosaicBrush createBrush(BaseDrawData drawData, Matrix matrix, Paint paint, int mosaicsWH, BitmapsManager manager) {

        BlockMosaicBrush brush = new BlockMosaicBrush(matrix, paint, drawData.strokeWidth, mosaicsWH, manager);
        brush.mDrawData = new BaseDrawData(drawData.type);
        brush.mDrawData.copy(drawData);
        brush.mPath = brush.mDrawData.path;

        return brush;
    }

    @Override
    public void draw(Canvas canvas) {
        resetBasePaint(mPaint);
        // TODO:这里也不合理
        mPaint.setShader(mManager.getMosaicsShader(mMosaicsWH));
        mPaint.setStrokeWidth(mStroke);

        canvas.drawPath(mPath, mPaint);

    }
}
