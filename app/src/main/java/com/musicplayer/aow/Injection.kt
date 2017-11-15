package com.musicplayer.aow

import android.content.Context

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com.musicpalyer.com.musicplayer.aow
 * Date: 9/10/16
 * Time: 4:11 PM
 * Desc: Injection
 */
object Injection {

    fun provideContext(): Context? {
        return MusicPlayerApplication.instance
    }
}
