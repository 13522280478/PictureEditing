package com.biotech.drawlessons

import android.content.res.Resources
import android.graphics.Bitmap

/**
 * @author TuXin
 * @date 2020/4/22 1:52 PM.
 *
 * Email : tuxin@pupupula.com
 */
fun Int.dp2px(): Float {
    return Resources.getSystem().displayMetrics.density * this
}

fun Float.dp2px(): Float {
    return Resources.getSystem().displayMetrics.density * this
}

fun getCameraLocationZ(): Float {
    return -Resources.getSystem().displayMetrics.density * 12
}