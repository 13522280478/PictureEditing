package com.biotech.drawlessons.others

import android.content.Context
import android.graphics.Camera
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import androidx.core.view.children
import com.biotech.drawlessons.R
import com.biotech.drawlessons.getCameraLocationZ
import kotlin.math.max

/**
 * @author TuXin
 * @date 2020/4/23 1:51 PM.
 *
 * Email : tuxin@pupupula.com
 */
class RotatableViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private val mMatchParentChildren = arrayListOf<View>()
    private val mCamera = Camera()
    private val mRotateAnim: Rotate3dAnimation

    init {
        mCamera.setLocation(0f, 0f, getCameraLocationZ())
        mCamera.rotateY(30F)
        mRotateAnim = Rotate3dAnimation()
    }

    open class LayoutParams :
        MarginLayoutParams {

        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
            val a = context.obtainStyledAttributes(attrs,
                R.styleable.RotatableViewGroup_Layout
            )
            this.gravity =
                a.getInt(
                    R.styleable.RotatableViewGroup_Layout_android_layout_gravity,
                    GRAVITY_UNDEFINE
                )
            a.recycle()

            Log.e(RotatableViewGroup::class.java.name, "gravity = $gravity")
        }

        constructor(width: Int, height: Int) : super(width, height)
        constructor(layoutParams: ViewGroup.LayoutParams?) : super(layoutParams)

        companion object {
            const val GRAVITY_UNDEFINE = -1
        }

        var gravity: Int =
            GRAVITY_UNDEFINE
    }

    private fun ensureChildrenCount() {
        if (childCount > 2) {
            throw IllegalAccessException("子View的个数不能大于2")
        }
    }

    fun startRotateAnim() {
        if (mRotateAnim.hasStarted()) return

        resetRotationValue()
        startAnimation(mRotateAnim)
    }

    private fun resetRotationValue() {
        mRotateAnim.mCenterX = width / 2F
        mRotateAnim.mCenterY = height / 2F
        mRotateAnim.mFromDegrees = 0F
        mRotateAnim.mToDegrees = 180F
        mRotateAnim.duration = 500
        mRotateAnim.fillAfter = true
        mRotateAnim.interpolator = AccelerateInterpolator()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.e("RotatableViewGroup", "onLayout called")

        ensureChildrenCount()
        val childrenCount = childCount

        var mMaxWidth = 0
        var mMaxHeight = 0
        mMatchParentChildren.clear()

        for (i in 0 until childrenCount) {
            val child = getChildAt(i)
            val lp = child.layoutParams as LayoutParams

            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
            mMaxWidth = max(mMaxWidth, child.measuredWidth + lp.leftMargin + lp.rightMargin)
            mMaxHeight = max(mMaxHeight, child.measuredHeight + lp.leftMargin + lp.rightMargin)
            if (lp.width == ViewGroup.LayoutParams.MATCH_PARENT || lp.height == ViewGroup.LayoutParams.MATCH_PARENT) {
                mMatchParentChildren.add(child)
            }
        }

        mMaxWidth += paddingLeft + paddingRight
        mMaxHeight += paddingTop + paddingBottom

        setMeasuredDimension(
            resolveSize(mMaxWidth, widthMeasureSpec),
            resolveSize(mMaxHeight, heightMeasureSpec)
        )

        for (matchParentChild in mMatchParentChildren) {
            val lp = matchParentChild.layoutParams as LayoutParams
            val childWidthMeasureSpec = if (lp.width == ViewGroup.LayoutParams.MATCH_PARENT) {
                val maxWidth = max(
                    0,
                    measuredWidth - paddingLeft - paddingRight - lp.leftMargin - lp.rightMargin
                )
                MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.EXACTLY)
            } else {
                getChildMeasureSpec(
                    widthMeasureSpec,
                    paddingLeft + paddingRight + lp.leftMargin + lp.rightMargin,
                    lp.width
                )
            }

            val childHeightMeasureSpec = if (lp.height == ViewGroup.LayoutParams.MATCH_PARENT) {
                val maxHeight = max(
                    0,
                    measuredHeight - (paddingTop + paddingBottom + lp.topMargin + lp.bottomMargin)
                )
                MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY)
            } else {
                getChildMeasureSpec(
                    heightMeasureSpec,
                    paddingTop + paddingBottom + lp.topMargin + lp.bottomMargin,
                    lp.height
                )
            }
            matchParentChild.measure(childWidthMeasureSpec, childHeightMeasureSpec)
        }

    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        Log.e("RotatableViewGroup", "onLayout called")
        ensureChildrenCount()

        val parentLeft = paddingLeft
        val parentTop = paddingTop
        val parentRight = right - left - paddingRight
        val parentBottom = bottom - top - paddingTop

        for (child in children) {
            if (child.visibility != View.GONE) {
                val width = child.measuredWidth
                val height = child.measuredHeight
                val lp = child.layoutParams as LayoutParams
                val gravity = lp.gravity
                var childLeft: Int
                var childTop: Int

                val absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection)

                when (absoluteGravity and Gravity.HORIZONTAL_GRAVITY_MASK) {
                    Gravity.CENTER_HORIZONTAL -> {
                        childLeft =
                            parentLeft + (parentRight - parentLeft - width) / 2 + lp.leftMargin - lp.rightMargin
                    }
                    Gravity.LEFT -> {
                        childLeft = parentLeft + lp.leftMargin
                    }

                    Gravity.RIGHT -> {
                        childLeft = parentRight - width - lp.rightMargin
                    }
                    else -> {
                        childLeft = parentLeft + lp.leftMargin
                    }
                }

                when (absoluteGravity and Gravity.VERTICAL_GRAVITY_MASK) {
                    Gravity.CENTER_VERTICAL -> {
                        childTop =
                            parentTop + (parentBottom - height - parentTop) / 2 + lp.topMargin - lp.bottomMargin
                    }

                    Gravity.TOP -> {
                        childTop = parentTop + lp.topMargin
                    }

                    Gravity.BOTTOM -> {
                        childTop = parentBottom - lp.bottomMargin - height
                    }

                    else -> {
                        childTop = parentTop + lp.topMargin
                    }
                }
                child.layout(childLeft, childTop, childLeft + width, childTop + height)
            }
        }
    }

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        ensureChildrenCount()

        val res: Boolean
        if (childCount == 2 && getChildAt(1) == child) {
            canvas.save()
            canvas.scale(-1F, 1F, width / 2F, height / 2F)
            res = super.drawChild(canvas, child, drawingTime)
            canvas.restore()
        } else {
            res = super.drawChild(canvas, child, drawingTime)
        }

        return res
    }

    override fun generateLayoutParams(attrs: AttributeSet?): ViewGroup.LayoutParams {
        return LayoutParams(
            context,
            attrs
        )
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams?): ViewGroup.LayoutParams {
        return LayoutParams(p)
    }

    override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
        return LayoutParams(
            0,
            0
        )
    }

}