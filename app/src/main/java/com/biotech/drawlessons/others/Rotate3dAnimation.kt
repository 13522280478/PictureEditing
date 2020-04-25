package com.biotech.drawlessons.others

import android.graphics.Camera
import android.graphics.Matrix
import android.view.animation.Animation
import android.view.animation.Transformation
import com.biotech.drawlessons.getCameraLocationZ


/**
 * @author TuXin
 * @date 2020/4/23 5:34 PM.
 *
 * Email : tuxin@pupupula.com
 */
class Rotate3dAnimation : Animation {
    var mFromDegrees = 0f
    var mToDegrees = 0f
    var mCenterX = 0f
    var mCenterY = 0f
    var mDepthZ = 0f
    var mReverse = false
    private var mCamera: Camera = Camera()

    init {
        mCamera.setLocation(0f, 0f, getCameraLocationZ())
    }

    constructor(){}

    /**
     * Creates a new 3D rotation on the Y axis. The rotation is defined by its
     * start angle and its end angle. Both angles are in degrees. The rotation
     * is performed around a center point on the 2D space, definied by a pair
     * of X and Y coordinates, called centerX and centerY. When the animation
     * starts, a translation on the Z axis (depth) is performed. The length
     * of the translation can be specified, as well as whether the translation
     * should be reversed in time.
     *
     * @param fromDegrees the start angle of the 3D rotation
     * @param toDegrees the end angle of the 3D rotation
     * @param centerX the X center of the 3D rotation
     * @param centerY the Y center of the 3D rotation
     * @param reverse true if the translation should be reversed, false otherwise
     */
    constructor(
        fromDegrees: Float, toDegrees: Float,
        centerX: Float, centerY: Float, depthZ: Float, reverse: Boolean
    ) {
        mFromDegrees = fromDegrees
        mToDegrees = toDegrees
        mCenterX = centerX
        mCenterY = centerY
        mDepthZ = depthZ
        mReverse = reverse
    }

    override fun applyTransformation(
        interpolatedTime: Float,
        t: Transformation
    ) {
        val fromDegrees = mFromDegrees
        val degrees = fromDegrees + (mToDegrees - fromDegrees) * interpolatedTime
        val centerX = mCenterX
        val centerY = mCenterY
        val camera: Camera = mCamera
        val matrix: Matrix = t.matrix
        camera.save()
//        if (mReverse) {
//            camera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime)
//        } else {
//            camera.translate(0.0f, 0.0f, mDepthZ * (1.0f - interpolatedTime))
//        }
        camera.rotateY(degrees)
        camera.getMatrix(matrix)
        camera.restore()
        matrix.preTranslate(-centerX, -centerY)
        matrix.postTranslate(centerX, centerY)
    }
}