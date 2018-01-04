package com.musicplayer.aow.ui.library.album


import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife

import com.musicplayer.aow.R
import com.musicplayer.aow.data.model.Song
import com.musicplayer.aow.data.model.TempSongs
import com.musicplayer.aow.ui.library.album.adapter.AlbumAdapter
import com.musicplayer.aow.ui.library.album.model.AlbumFindModel
import com.musicplayer.aow.ui.library.album.model.AlbumModel
import com.musicplayer.aow.utils.Settings
import java.util.*

class AlbumFragment : Fragment() {

    var songModelData:ArrayList<Song>? = ArrayList()
    var albumModelData: ArrayList<AlbumModel> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_album, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view!!)
        mAlbumList = view.findViewById(R.id.album_recycler_views) as RecyclerView
        loadData()
    }

    private var mAlbumList: RecyclerView? = null
    private var mAlbumAdapter: AlbumAdapter? = null
    var songsList:List<Song>? = null

    fun loadData(){
        songModelData = TempSongs.instance!!.songs
        if (songModelData == null){
//            mAlbumList!!.visibility = View.INVISIBLE
        }else {
//            mAlbumList!!.visibility = View.VISIBLE
            var albumName: ArrayList<AlbumFindModel>? = ArrayList()
            songsList = songModelData!!.sortedWith(compareBy({ (it.album)!!.toLowerCase() }))
            for (album in songsList!!) {
                var foundInAlbum = 0
                albumName?.forEach{e ->
                    if (e.album == album.album) {
                        foundInAlbum = 1
                    }
                }
                if (foundInAlbum == 0){
                    albumName?.add(AlbumFindModel(album.album!!, album.artist))
                    var albumNotNull: String? = null
                    if (album.path != null) {
                        albumNotNull = album.path
                    }
                    albumModelData.add(AlbumModel(album.album!!, album.artist!!, albumNotNull))
                }
            }
            mAlbumAdapter = AlbumAdapter(activity, activity,albumModelData)
            mAlbumList!!.adapter = mAlbumAdapter
            mAlbumList!!.layoutManager = GridLayoutManager(activity, 2)
        }
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
}