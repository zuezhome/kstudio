package com.musicplayer.aow.data.source.db

import com.litesuits.orm.LiteOrm
import com.musicplayer.aow.BuildConfig
import com.musicplayer.aow.Injection

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com.musicpalyer.com.musicplayer.aow
 * Date: 9/10/16
 * Time: 4:00 PM
 * Desc: LiteOrmHelper
 */
object LiteOrmHelper {

    private val DB_NAME = "kingszplayer.db"

    @Volatile private var sInstance: LiteOrm? = null

    val instance: LiteOrm?
        get() {
            if (sInstance == null) {
                synchronized(LiteOrmHelper::class.java) {
                    if (sInstance == null) {
                        sInstance = LiteOrm.newCascadeInstance(Injection.provideContext(), DB_NAME)
                        sInstance!!.setDebugged(BuildConfig.DEBUG)
                    }
                }
            }
            return sInstance
        }
}// Avoid direct instantiate
