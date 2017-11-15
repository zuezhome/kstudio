package com.musicplayer.aow.ui.allsongs.model

import com.musicplayer.aow.data.model.Song
import javax.xml.datatype.Duration

/**
 * Created by Arca on 11/9/2017.
 */
class SongModel(songName: String, songDuration: String, songArtist: String, songPath: String, song: Song){
    var mSongName = songName
    var mSongDuration = songDuration
    var mSongArtist = songArtist
    var mSongPath = songPath
    var mSong = song
}