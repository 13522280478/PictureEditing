package com.biotech.drawlessons.photoedit.draws;

import android.graphics.Path;

import java.util.LinkedList;

/**
 * Created by xintu on 2018/2/28.
 */

public class BaseDrawData {
    public LinkedList<PointWithColor> pointsWithColors;
    public Path path;
    public int type;
    public float strokeWidth;
    public String bitmapUrl;
    public int color;

    public BaseDrawData(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public static class PointWithColor{
        public float xOnBitmap, yOnBitmap;
        public int color;
        public PointWithColor(float x, float y, int color) {
            this.color = color;
            xOnBitmap = x;
            yOnBitmap = y;
        }

    }

    public void copy(BaseDrawData baseDrawData) {
        if (baseDrawData.path != null) {
            path = new Path();
            path.set(baseDrawData.path);
        }
        if (baseDrawData.pointsWithColors != null) {
            pointsWithColors = (LinkedList<PointWithColor>) baseDrawData.pointsWithColors.clone();
        }
        strokeWidth = baseDrawData.strokeWidth;
        bitmapUrl = baseDrawData.bitmapUrl;
        color = baseDrawData.color;
    }
}
