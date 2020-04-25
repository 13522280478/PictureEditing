package com.biotech.drawlessons

import android.animation.Keyframe
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.biotech.drawlessons.others.Rotate3dAnimation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {
    companion object {
        var REQUEST_ALBUM_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvSelectPhoto.setOnClickListener {
            if (!PermissionUtil.hasExSdCardPermission(this)) {
                PermissionUtil.requestExSdCardPermission(this)
                return@setOnClickListener
            }
            if (!PermissionUtil.hasExSdCardWritePermission(this)) {
                PermissionUtil.requestWriteSDCardPermission(this)
            }

            activityOpenSystemAlbum(this)
        }

        tvFront.setOnClickListener {
            var length = 200.dp2px()
            val keyframe1 = Keyframe.ofFloat(0F, 0F)
            val keyframe2 = Keyframe.ofFloat(0.1F, 0.4F * length)
            val keyframe3 = Keyframe.ofFloat(0.9F, 0.6F * length)
            val keyframe4 = Keyframe.ofFloat(1F, 1F * length)

            val holders = PropertyValuesHolder.ofKeyframe(
                "translationX",
                keyframe1,
                keyframe2,
                keyframe3,
                keyframe4
            )

            val animator = ObjectAnimator.ofPropertyValuesHolder(frameLayout, holders)
            animator.duration = 2000
            animator.start()
        }

        GlobalScope.launch(Dispatchers.Main) {
            ioCode1()
            uiCode1()
            ioCode2()
            uiCode2()
            ioCode3()
            uiCode3()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_ALBUM_CODE -> {
                    onPhotoSelected(data)
                }
            }
        }
    }


    private fun onPhotoSelected(intent: Intent?) {
        if (intent == null) {
            return
        }
        val uri = intent.data ?: return
        DrawingBoardAct.startAct(this, uri)
    }


    fun activityOpenSystemAlbum(activity: Activity) {
        val mimeTypes = arrayOf("image/jpeg", "image/png")
        val photoPickerIntent = Intent(Intent.ACTION_PICK).setType("image/*")
            .putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        activity.startActivityForResult(photoPickerIntent, REQUEST_ALBUM_CODE)
    }

    /**
     * Setup a new 3D rotation on the container view.
     *
     * @param position
     * the item that was clicked to show a picture, or -1 to show the
     * list
     * @param start
     * the start angle at which the rotation must begin
     * @param end
     * the end angle of the rotation
     */
    private fun applyRotation(
        position: Int,
        start: Float,
        end: Float
    ) { // Find the center of the container
        val centerX: Float = tvFront.width / 2.0f
        val centerY: Float = tvFront.height / 2.0f
        // Create a new 3D rotation with the supplied parameter
        // The animation listener is used to trigger the next animation
        val rotation = Rotate3dAnimation(
            start, end,
            centerX, centerY, 310.0f, true
        )
        rotation.duration = 500
        rotation.fillAfter = true
        rotation.interpolator = AccelerateInterpolator()
        frameLayout.startAnimation(rotation)
    }


    private suspend fun ioCode1() {
        withContext(Dispatchers.IO) {
            Thread.sleep(1000)
            println("Coroutines Cap io1 ${Thread.currentThread().name}")
        }
    }

    private fun uiCode1() {
        println("Coroutines Cap ui1 ${Thread.currentThread().name}")
    }

    private suspend fun ioCode2() {
        delay(1000)
        withContext(Dispatchers.IO) {
            println("Coroutines Cap io2 ${Thread.currentThread().name}")
        }
    }

    private fun uiCode2() {
        println("Coroutines Cap ui2 ${Thread.currentThread().name}")
    }

    private suspend fun ioCode3() {
        withContext(Dispatchers.IO) {
            println("Coroutines Cap io3 ${Thread.currentThread().name}")
        }
    }

    private fun uiCode3() {
        println("Coroutines Cap ui3 ${Thread.currentThread().name}")
    }
}
