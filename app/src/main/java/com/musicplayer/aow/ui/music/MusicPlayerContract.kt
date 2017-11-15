package com.musicplayer.aow.ui.music

import com.musicplayer.aow.data.model.Song
import com.musicplayer.aow.player.PlayMode
import com.musicplayer.aow.player.PlaybackService
import com.musicplayer.aow.ui.base.BasePresenter
import com.musicplayer.aow.ui.base.BaseView

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com.musicpalyer.com.musicplayer.aow
 * Date: 9/12/16
 * Time: 8:27 AM
 * Desc: MusicPlayerContract
 */
/* package */  interface MusicPlayerContract {

    interface View : BaseView<Presenter> {

        fun handleError(error: Throwable)

        fun onPlaybackServiceBound(service: PlaybackService)

        fun onPlaybackServiceUnbound()

        fun onSongSetAsFavorite(song: Song)

        fun onSongUpdated(song: Song?)

        fun updatePlayMode(playMode: PlayMode)

        fun updatePlayToggle(play: Boolean)

        fun updateFavoriteToggle(favorite: Boolean)
    }

    interface Presenter : BasePresenter {

        fun retrieveLastPlayMode()

//        fun setSongAsFavorite(song: Song, favorite: Boolean)

        fun bindPlaybackService()

        fun unbindPlaybackService()
    }
}
