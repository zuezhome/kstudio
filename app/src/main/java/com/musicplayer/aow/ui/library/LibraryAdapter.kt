package com.musicplayer.aow.ui.library

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

import com.musicplayer.aow.ui.library.album.AlbumFragment
import com.musicplayer.aow.ui.library.artist.ArtistFragment
import com.musicplayer.aow.ui.library.playlist.PlayListsFragment
import com.musicplayer.aow.ui.library.songs.AllSongsFragment

/**
 * Created by Arca on 11/19/2017.
 */

class LibraryAdapter(fm: FragmentManager, numbersOfTabs: Int) : FragmentStatePagerAdapter(fm) {

    internal var numbersOfTabs = 0

    init {
        this.numbersOfTabs = numbersOfTabs
    }

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> {
                return AllSongsFragment()
            }
            1 -> {
                return AlbumFragment()
            }
            2 -> {
                return ArtistFragment()
            }
            3 -> {
                return PlayListsFragment()
            }
            else -> return null
        }
    }

    override fun getCount(): Int {
        return numbersOfTabs
    }
}
