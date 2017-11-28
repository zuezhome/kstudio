package com.musicplayer.aow.ui.library.artist


import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife

import com.musicplayer.aow.R
import com.musicplayer.aow.data.model.Song
import com.musicplayer.aow.ui.library.artist.adapter.ArtistAdapter
import com.musicplayer.aow.ui.library.artist.model.ArtistModel
import com.musicplayer.aow.utils.Settings
import java.util.*

class ArtistFragment : Fragment() {

    var songModelData: ArrayList<Song> = ArrayList()
    var artistModelData: ArrayList<ArtistModel> = ArrayList()
    private var mArtistList: RecyclerView? = null
    private var mArtistAdapter: ArtistAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_artist, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view!!)
        mArtistList = view.findViewById(R.id.artist_recycler_views) as RecyclerView
        loadData()
    }

    fun loadData(){
        //context or activity
        songModelData = Settings.instance!!.readSongs(activity)!!
        if (songModelData.isEmpty()){
            mArtistList!!.visibility = View.INVISIBLE
        }else {
            var artistName: ArrayList<String?>? = ArrayList()
            for (album in songModelData) {
                var songsByArtist = 0
                songModelData.forEach{e ->
                    if (e.artist == album.artist) {
                        songsByArtist += 1
                    }
                }
                if (!artistName!!.contains(album.artist)) {
                    var albumNotNull: String? = null
                    if (album.albumArt != null) {
                        albumNotNull = album.albumArt
                    }
                    artistModelData.add(ArtistModel(songsByArtist, album.artist!!, albumNotNull))
                    artistName?.add(album!!.artist)
                }
            }
        }

        mArtistAdapter = ArtistAdapter(activity, artistModelData)
        mArtistList!!.adapter = mArtistAdapter
        mArtistList!!.layoutManager = GridLayoutManager(activity, 3)
    }

    private var mListener: onFragmentInteractionListener? = null

    interface onFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is onFragmentInteractionListener) {
            mListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }


}// Required empty public constructor

