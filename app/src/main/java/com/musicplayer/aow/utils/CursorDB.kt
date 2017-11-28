package com.musicplayer.aow.utils

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.musicplayer.aow.data.model.Song
import java.io.File
import android.graphics.BitmapFactory
import android.content.ContentUris
import android.graphics.Bitmap
import android.net.Uri


/**
 * Created by Arca on 11/9/2017.
 */
class CursorDB {

    private val MEDIA_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    private val WHERE = (MediaStore.Audio.Media.IS_MUSIC + "=1 AND "
            + MediaStore.Audio.Media.SIZE + ">0")
    private val ORDER_BY = MediaStore.Audio.Media.DISPLAY_NAME + " ASC"
    private val PROJECTIONS = arrayOf(
            MediaStore.Audio.Media.DATA, // the real path
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.IS_RINGTONE,
            MediaStore.Audio.Media.IS_MUSIC,
            MediaStore.Audio.Media.IS_NOTIFICATION,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE)

    fun callCursor(context: Context): Cursor? {
        return context.contentResolver.query(
                MEDIA_URI,
                PROJECTIONS,
                WHERE,
                null,
                ORDER_BY);
    }

    fun cursorToMusic(context: Context,cursor: Cursor, indexPosition: Int): Song {
        val realPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
        val songFile = File(realPath)
        var song: Song?
        if (songFile.exists()) {
            // Using song parsed from file to avoid encoding problems
            song = FileUtilities.fileToMusic(songFile)
            if (song != null) {
                return song
            }
        }
        song = Song()
        song.id = indexPosition
        song!!.title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
        var displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
        if (displayName.endsWith(".mp3")) {
            displayName = displayName.substring(0, displayName.length - 4)
        }
        song!!.displayName = displayName
        song!!.artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
        song!!.album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
        if (song!!.album.isNullOrEmpty()){
            song!!.album = "Unknown"
        }
        song!!.path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
        song!!.duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
        song!!.size = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE))

        return song
    }

    fun getAlbumart(context: Context, album_id: Long?): Bitmap? {
        var bm: Bitmap? = null
        try {
            val sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart")
            val uri = ContentUris.withAppendedId(sArtworkUri, album_id!!)
            val pfd = context.getContentResolver()
                    .openFileDescriptor(uri, "r")
            if (pfd != null) {
                val fd = pfd!!.getFileDescriptor()
                bm = BitmapFactory.decodeFileDescriptor(fd)
            }
        } catch (e: Exception) {
        }
        return bm
    }

}