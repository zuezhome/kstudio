package com.musicplayer.aow.ui.library.activities.albumsonglist

import com.musicplayer.aow.data.model.Song

/**
 * Created by Arca on 12/1/2017.
 */
class AlbumSongsModel(songName: String, songDuration: String, songArtist: String, songPath: String, song: Song){
    var mSongName = songName
    var mSongDuration = songDuration
    var mSongArtist = songArtist
    var mSongPath = songPath
    var mSong = song
}