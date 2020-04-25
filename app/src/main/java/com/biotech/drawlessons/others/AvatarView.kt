package com.biotech.drawlessons.others

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.biotech.drawlessons.R
import com.biotech.drawlessons.dp2px

/**
 * @author TuXin
 * @date 2020/4/22 5:43 PM.
 *
 * Email : tuxin@pupupula.com
 */
class AvatarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val mBitmap by lazy {
        getAvatar(WIDTH.toInt())
    }

    companion object {
        val WIDTH = 250.dp2px()
        val OFFSET = 50.dp2px()
        const val TRANSLATE_VALUE = 30F
    }

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mXfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    private val mCamera = Camera()
//    private val mBottomFlipAnimator: ObjectAnimator
//    private val mTopFlipAnimator: ObjectAnimator
//    private val mFlipRotationAnimator: ObjectAnimator
//    private val mAnimatorSet: AnimatorSet

//    private val mBottomFlipHolder: PropertyValuesHolder
//    private val mTopFlipHolder: PropertyValuesHolder
//    private val mFlipRotationHolder: PropertyValuesHolder

    var mTopFlip = 0F
        set(value) {
            field = value
            invalidate()
        }
    private var mBottomFlip = 0F
        set(value) {
            field = value
            invalidate()
        }
    private var mFlipRotation = 0F
        set(value) {
            field = value
            invalidate()
        }


    init {
        mCamera.setLocation(0F, 0F, -resources.displayMetrics.density * 12)

//        mBottomFlipAnimator = ObjectAnimator.ofFloat(this, "mBottomFlip", 45F)
//        mBottomFlipAnimator.startDelay = 1000
//
//        mTopFlipAnimator = ObjectAnimator.ofFloat(this, "mTopFlip", 45F)
//        mTopFlipAnimator.startDelay = 1000
//
//        mFlipRotationAnimator = ObjectAnimator.ofFloat(this, "mFlipRotation", 270F)
//        mFlipRotationAnimator.startDelay = 1000
//
//        mAnimatorSet = AnimatorSet()
//        mAnimatorSet.playSequentially(mBottomFlipAnimator, mFlipRotationAnimator, mTopFlipAnimator)
//        mAnimatorSet.startDelay = 1000
//        mAnimatorSet.duration = 5000
//        mAnimatorSet.start()

//        mBottomFlipHolder = PropertyValuesHolder.ofFloat("mBottomFlip", 45F)
//        mTopFlipHolder = PropertyValuesHolder.ofFloat("mTopFlip", -45F)
//        mFlipRotationHolder = PropertyValuesHolder.ofFloat("mFlipRotation", 270F)
//        val animator = ObjectAnimator.ofPropertyValuesHolder(this,
//            mBottomFlipHolder,
//            mTopFlipHolder,
//            mFlipRotationHolder
//        )
//
//        animator.duration = 5000
//        animator.startDelay = 1000
//        animator.start()
    }


    override fun onDraw(canvas: Canvas) {
        // 上半部分
        canvas.save()
        canvas.translate(OFFSET + mBitmap.width / 2F, OFFSET + mBitmap.height / 2)
        canvas.rotate(-mFlipRotation)

        mCamera.save()
        mCamera.rotateX(mTopFlip)
        mCamera.applyToCanvas(canvas)
        mCamera.restore()

        canvas.clipRect(-mBitmap.width * 4, -mBitmap.height, mBitmap.width * 4, 0)
        canvas.rotate(mFlipRotation)
        canvas.translate(-(OFFSET + mBitmap.width / 2F), -(OFFSET + mBitmap.height / 2))
        canvas.drawBitmap(mBitmap,
            OFFSET,
            OFFSET, mPaint)
        canvas.restore()


        // 底部
        canvas.save()
        canvas.translate(OFFSET + mBitmap.width / 2F, OFFSET + mBitmap.height / 2)
        canvas.rotate(-mFlipRotation)

        mCamera.save()
        mCamera.rotateX(mBottomFlip)
        mCamera.applyToCanvas(canvas)
        mCamera.restore()

        canvas.clipRect(-mBitmap.width * 4, 0, mBitmap.width * 4, mBitmap.height)
        canvas.rotate(mFlipRotation)
        canvas.translate(-(OFFSET + mBitmap.width / 2F), -(OFFSET + mBitmap.height / 2))

        canvas.drawBitmap(mBitmap,
            OFFSET,
            OFFSET, mPaint)
        canvas.restore()
    }

    fun getAvatar(width: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(resources,
            R.drawable.ic_task_keep_going_on, options)
        options.inJustDecodeBounds = false
        options.inDensity = options.outWidth
        options.inTargetDensity = width
        return BitmapFactory.decodeResource(resources,
            R.drawable.ic_task_keep_going_on, options)
    }
}