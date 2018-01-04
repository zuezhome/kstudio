package com.musicplayer.aow

import android.content.Context

object Injection {

    fun provideContext(): Context? {
        return MusicPlayerApplication.instance
    }
}
