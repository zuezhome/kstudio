package com.musicplayer.aow.utils

import android.graphics.drawable.GradientDrawable
import android.support.annotation.ColorInt
import android.support.annotation.FloatRange

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com.musicpalyer.com.musicplayer.aow
 * Date: 9/1/16
 * Time: 10:13 PM
 * Desc: GradientUtils
 */
object GradientUtils {

    fun create(@ColorInt startColor: Int, @ColorInt endColor: Int, radius: Int,
               @FloatRange(from = 0.0, to = 1.0) centerX: Float,
               @FloatRange(from = 0.0, to = 1.0) centerY: Float): GradientDrawable {
        val gradientDrawable = GradientDrawable()
        gradientDrawable.colors = intArrayOf(startColor, endColor)
        gradientDrawable.gradientType = GradientDrawable.RADIAL_GRADIENT
        gradientDrawable.gradientRadius = radius.toFloat()
        gradientDrawable.setGradientCenter(centerX, centerY)
        return gradientDrawable
    }
}
