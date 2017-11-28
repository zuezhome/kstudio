package com.musicplayer.aow.ui.library


import android.os.Bundle
import android.net.Uri
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import com.musicplayer.aow.ui.base.BaseFragment
import com.musicplayer.aow.R
import com.musicplayer.aow.ui.library.album.AlbumFragment
import com.musicplayer.aow.ui.library.artist.ArtistFragment
import com.musicplayer.aow.ui.library.playlist.PlayListsFragment
import com.musicplayer.aow.ui.library.songs.AllSongsFragment



class LibraryFragment : BaseFragment(),
        ArtistFragment.onFragmentInteractionListener,
        AlbumFragment.onFragmentInteractionListener,
        PlayListsFragment.onFragmentInteractionListener,
        AllSongsFragment.onFragmentInteractionListener {

    internal var tablayout: TabLayout? = null
    internal var viewPager: ViewPager? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view!!)

        tablayout = view.findViewById(R.id.library_tablayout) as TabLayout
        tablayout!!.addTab(tablayout!!.newTab().setIcon(R.drawable.library_songs))
        tablayout!!.addTab(tablayout!!.newTab().setIcon(R.drawable.library_album))
        tablayout!!.addTab(tablayout!!.newTab().setIcon(R.drawable.library_artist))
        tablayout!!.addTab(tablayout!!.newTab().setIcon(R.drawable.library_playlist))
        tablayout!!.setTabGravity(TabLayout.GRAVITY_FILL)

        viewPager = view.findViewById(R.id.library_view_pager) as ViewPager
        var adapter = LibraryAdapter(fragmentManager,tablayout!!.tabCount)
        viewPager!!.adapter = adapter
        viewPager!!.setOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tablayout))

        tablayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager!!.currentItem = tab!!.position
            }

        })

    }

    override fun onFragmentInteraction(uri: Uri) {

    }

}
