package com.musicplayer.aow.player

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com.musicpalyer.com.musicplayer.aow
 * Date: 9/5/16
 * Time: 5:48 PM
 * Desc: PlayMode
 */
enum class PlayMode {
    SINGLE,
    default,
    LIST,
    LOOP,

    SHUFFLE;


    companion object {

        fun switchNextMode(current: PlayMode?): PlayMode {
            if (current == null) return default

            when (current) {
                LOOP -> return LIST
                LIST -> return SHUFFLE
                SHUFFLE -> return SINGLE
                SINGLE -> return LOOP
            }
            return default
        }
    }
}
