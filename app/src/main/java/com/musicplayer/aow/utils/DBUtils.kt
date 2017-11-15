package com.musicplayer.aow.utils

import android.content.Context
import android.os.Environment

import com.musicplayer.aow.R
import com.musicplayer.aow.data.model.*

import java.io.File
import java.util.ArrayList

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com.musicpalyer.com.musicplayer.aow
 * Date: 9/9/16
 * Time: 10:27 PM
 * Desc: DBUtils
 */
object DBUtils {

    fun generateFavoritePlayList(context: Context?): PlayList {
        val favorite = PlayList()
        favorite.isFavorite = true
        favorite.name = context?.getString(R.string.mp_play_list_favorite)
        return favorite
    }

    //
    fun generateDefaultFolders(): List<Folder> {
        val defaultFolders = ArrayList<Folder>(4)
        val internalDir = Environment.getExternalStorageDirectory()
        val sdcardDir = Environment.getExternalStorageDirectory().parentFile.parentFile
        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
        val all = File("/storage/")
        defaultFolders.add(FileUtils.folderFromDir(internalDir))
        defaultFolders.add(FileUtils.folderFromDir(sdcardDir))
        defaultFolders.add(FileUtils.folderFromDir(downloadDir))
        defaultFolders.add(FileUtils.folderFromDir(musicDir))
        defaultFolders.add(FileUtils.folderFromDir(all))
        return defaultFolders
    }
}
