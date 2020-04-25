package com.biotech.drawlessons.others

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatImageView
import com.biotech.drawlessons.dp2px

/**
 * @author TuXin
 * @date 2020/4/22 11:49 AM.
 *
 * Email : tuxin@pupupula.com
 */
class DashboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    companion object {
        val RADIUS = 100.dp2px()
        val STROKE_WIDTH = 3.dp2px()
        const val OPEN_ANGLE = 120
    }

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val dash = Path()
    private var mPathEffect:PathEffect
    private var mPath = Path()
    private var mPathMeasure = PathMeasure()

    init {
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth =
            STROKE_WIDTH

        dash.addRect(0F, 0F, 2.dp2px(), 10.dp2px(), Path.Direction.CW)
        mPathEffect = PathDashPathEffect(dash, 50F, 0F, PathDashPathEffect.Style.ROTATE)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        Log.e(
            DashboardView::class.java.name,
            "onlayout changed = $changed left = $left top = $top right = $right bottom = $bottom"
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mPath.reset()
        mPath.addArc(width / 2 - RADIUS,
            height / 2 - RADIUS,
            width / 2 + RADIUS,
            height / 2 + RADIUS,
            90 + OPEN_ANGLE / 2F,
            360F - OPEN_ANGLE
        )

        mPathMeasure = PathMeasure(mPath, false)

        mPathEffect = PathDashPathEffect(dash, (mPathMeasure.length - 2.dp2px()) / 20, 0F, PathDashPathEffect.Style.ROTATE)
    }

    override fun onDraw(canvas: Canvas) {
        // 画圆
        mPaint.pathEffect = null
        canvas.drawArc(
            width / 2 - RADIUS,
            height / 2 - RADIUS,
            width / 2 + RADIUS,
            height / 2 + RADIUS,
            90 + OPEN_ANGLE / 2F,
            360F - OPEN_ANGLE, false, mPaint
        )

        // 画刻度
        mPaint.pathEffect = mPathEffect
        canvas.drawArc(
            width / 2 - RADIUS,
            height / 2 - RADIUS,
            width / 2 + RADIUS,
            height / 2 + RADIUS,
            90 + OPEN_ANGLE / 2F,
            360F - OPEN_ANGLE, false, mPaint
        )
        mPaint.pathEffect = null
    }
}
