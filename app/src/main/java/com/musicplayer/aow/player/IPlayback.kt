package com.musicplayer.aow.player

import com.musicplayer.aow.data.model.*


interface IPlayback {

    val isPlaying: Boolean

    val progress: Int

    val playingSong: Song?

    fun setPlayList(list: PlayList)

    fun play(): Boolean

    fun play(list: PlayList): Boolean

    fun play(list: PlayList, startIndex: Int): Boolean

    fun play(song: Song): Boolean

    fun playLast(): Boolean

    fun playNext(): Boolean

    fun pause(): Boolean

    fun seekTo(progress: Int): Boolean

    fun setPlayMode(playMode: PlayMode)

    fun registerCallback(callback: Callback)

    fun unregisterCallback(callback: Callback)

    fun removeCallbacks()

    fun releasePlayer()

    interface Callback {

        fun onSwitchLast(last: Song?)

        fun onSwitchNext(next: Song?)

        fun onComplete(next: Song?)

        fun onPlayStatusChanged(isPlaying: Boolean)
    }
}
