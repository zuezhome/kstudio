package com.musicplayer.aow.player

/**
 * Created with Android Studio.
 * User:
 * Date:
 * Time:
 * Desc: AppContract
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
