package com.musicplayer.aow

import android.app.Application


import uk.co.chrisjenx.calligraphy.CalligraphyConfig

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com.musicpalyer.com.musicplayer.aow
 * Date: 8/31/16
 * Time: 9:32 PM
 * Desc: MusicPlayerApplication
 */
class MusicPlayerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        instance = this

//        // Custom fonts
//        CalligraphyConfig.initDefault(
//                CalligraphyConfig.Builder()
//                        .setDefaultFontPath("fonts/Roboto-Regular.ttf")
//                        .setFontAttrId(R.attr.fontPath)
//                        .build()
//        )

    }

    companion object {
        var instance: MusicPlayerApplication? = null
            private set
    }
}
