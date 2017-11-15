package com.musicplayer.aow.utils

import android.graphics.*
import android.graphics.BitmapFactory.decodeByteArray
import android.media.MediaMetadataRetriever
import android.util.Log

import com.musicplayer.aow.R
import com.musicplayer.aow.data.model.Song

import java.io.File

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com.musicpalyer.com.musicplayer.aow
 * Date: 9/14/16
 * Time: 8:42 PM
 * Desc: BitmapUtils
 * TODO To be optimized
 */
object AlbumUtils {

    private val TAG = "AlbumUtils"

    fun parseAlbum(song: String): Bitmap? {
        return parseAlbum(File(song!!))
    }

    fun parseAlbum(song: Song): Bitmap? {
        return parseAlbum(File(song.path!!))
    }

    fun parseAlbum(file: File): Bitmap? {
        val metadataRetriever = MediaMetadataRetriever()
        try {
            metadataRetriever.setDataSource(file.absolutePath)
            val albumData = metadataRetriever.embeddedPicture
            if (albumData != null) {
                return decodeByteArray(albumData, 0, albumData.size)
            } else {
                return null
            }
        } catch (e: IllegalArgumentException) {
//            Log.e(TAG, "parseAlbum: ", e)
            return null
        }
    }

    fun getCroppedBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width,
                bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle((bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat(),
                (bitmap.width / 2).toFloat(), paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output
    }
}
