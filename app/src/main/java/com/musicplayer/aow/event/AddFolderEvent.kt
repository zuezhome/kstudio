package com.musicplayer.aow.event

import java.io.File
import java.util.ArrayList

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com.musicpalyer.com.musicplayer.aow
 * Date: 9/4/16
 * Time: 9:28 PM
 * Desc: AddFolderEvent
 */
class AddFolderEvent {

    var folders: MutableList<File> = ArrayList()

    constructor(file: File) {
        folders.add(file)
    }

    constructor(files: MutableList<File>?) {
        if (files != null) {
            folders = files
        }
    }
}
