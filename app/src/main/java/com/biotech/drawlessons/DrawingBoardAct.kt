package com.biotech.drawlessons

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.biotech.drawlessons.photoedit.utils.BitmapsManager
import com.biotech.drawlessons.photoedit.utils.DrawInvoker
import kotlinx.android.synthetic.main.activity_photo_edit.*

/**
 * @author TuXin
 * @date 2020/4/25 10:52 AM.
 *
 * Email : tuxin@pupupula.com
 */
class DrawingBoardAct : AppCompatActivity() {
    private val mUri by lazy { intent.getParcelableExtra<Uri>(KEY_URI) }

    companion object {
        private const val KEY_URI = "key_uri"
        fun startAct(act: Activity, uri: Uri) {
            val intent = Intent(act, DrawingBoardAct::class.java)
            intent.putExtra(KEY_URI, uri)
            act.startActivityForResult(intent, 0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_edit)

        val manager = BitmapsManager()
        val invoker = DrawInvoker(manager)
        vDrawingBoard.init(invoker, manager, getPath(mUri))
    }

    fun getPath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = managedQuery(uri, projection, null, null, null)
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }
}