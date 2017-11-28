package com.musicplayer.aow.ui.library.songs

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.ButterKnife
import com.musicplayer.aow.R
import com.musicplayer.aow.ui.library.songs.Adapter.SongListAdapter
import java.util.*
import kotlinx.android.synthetic.main.fragment_all_songs.*
import com.musicplayer.aow.data.model.Song
import com.musicplayer.aow.utils.Settings


class AllSongsFragment : Fragment(){

    var songModelData:ArrayList<Song> = ArrayList()
    var songListAdapter:SongListAdapter? = null
    var songsList:List<Song>? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_all_songs, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view!!)
        loadData()
    }

    fun loadData(){
        //get audio from shearPref
        songModelData = Settings.instance!!.readSongs(activity)!!
        if (songModelData.isEmpty()){
            recycler_views.visibility = View.INVISIBLE
        }else {
            //sort the song list in ascending order
            songsList = songModelData.sortedWith(compareBy({ (it.title)!!.toLowerCase() }))
            songListAdapter = SongListAdapter(activity, songsList!!)
            var layoutManager = LinearLayoutManager(context)
            recycler_views.setHasFixedSize(true)
            recycler_views.layoutManager = layoutManager
            recycler_views.adapter = songListAdapter
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
