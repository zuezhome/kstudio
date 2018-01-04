package com.musicplayer.aow.data.model

import com.musicplayer.aow.utils.Settings
import java.util.*

/**
 * Created by Arca on 12/31/2017.
 */
class TempSongs {

    var songs: ArrayList<Song>? = null

    fun setSongs(){
        this.songs = Settings.instance?.readSongs()
    }

    companion object {

        @Volatile private var sInstance: TempSongs? = null

        val instance: TempSongs?
            get() {
                if (sInstance == null) {
                    synchronized(TempSongs::class.java) {
                        if (sInstance == null) {
                            sInstance = TempSongs()
                        }
                    }
                }
                return sInstance
            }
    }
}