package com.musicplayer.aow.ui.base

import android.graphics.Point
import android.support.v4.app.DialogFragment
import android.view.Window
import android.view.WindowManager

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com.musicpalyer.com.musicplayer.aow
 * Date: 9/14/16
 * Time: 2:03 AM
 * Desc: BaseDialogFragment
 */
open class BaseDialogFragment : DialogFragment() {

    protected fun resizeDialogSize() {
        val window = dialog.window
        val size = Point()
        window!!.windowManager.defaultDisplay.getSize(size)
        window.setLayout((size.x * DIALOG_WIDTH_PROPORTION).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
    }

    companion object {

        private val DIALOG_WIDTH_PROPORTION = 0.85f
    }
}
