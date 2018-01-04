package com.musicplayer.aow.ui.library.songs

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import com.musicplayer.aow.Injection
import com.musicplayer.aow.R
import com.musicplayer.aow.data.model.PlayList
import com.musicplayer.aow.ui.library.songs.Adapter.SongListAdapter
import java.util.*
import kotlinx.android.synthetic.main.fragment_all_songs.*
import com.musicplayer.aow.data.model.Song
import com.musicplayer.aow.data.model.TempSongs
import com.musicplayer.aow.data.source.AppRepository
import com.musicplayer.aow.utils.DeviceUtils
import com.musicplayer.aow.utils.Settings
import com.musicplayer.aow.utils.layout.PreCachingLayoutManager
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import org.jetbrains.anko.uiThread
import android.support.v7.widget.DividerItemDecoration
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider


class AllSongsFragment : Fragment(){

    var songModelData:ArrayList<Song>? = null
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
        songModelData = TempSongs.instance!!.songs

        if (songModelData == null) {
//            recycler_views.visibility = View.INVISIBLE
        } else {
//            recycler_views.visibility = View.VISIBLE
            //sort the song list in ascending order
//                    songsList = songModelData!!.sortedWith(compareBy({ (it.title)!!.toLowerCase() }))
            Collections.sort(songModelData) { a, b -> a.title!!.compareTo(b.title!!) }
            songListAdapter = SongListAdapter(Injection.provideContext()!!, songModelData!!)
            //Setup layout manager
            val layoutManager = PreCachingLayoutManager(Injection.provideContext()!!)
            layoutManager.orientation = LinearLayoutManager.VERTICAL
            layoutManager.setExtraLayoutSpace(DeviceUtils.getScreenHeight(Injection.provideContext()!!))
            recycler_views.setHasFixedSize(true)
            recycler_views.addItemDecoration(DividerItemDecoration(activity,
                    DividerItemDecoration.VERTICAL))
            recycler_views.layoutManager = layoutManager
            recycler_views.adapter = songListAdapter
            //set fastscroll
            fastscroll.setRecyclerView(recycler_views)
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

    inner class AsyncTaskLoadBackground : AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg p0: String?): String {
            return ""
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
        }
    }
}
