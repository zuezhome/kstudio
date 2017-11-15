package com.musicplayer.aow.ui.allsongs


import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import butterknife.ButterKnife
import com.musicplayer.aow.R
import com.musicplayer.aow.ui.allsongs.Adapter.SongListAdapter
import com.musicplayer.aow.ui.allsongs.model.SongModel
import com.musicplayer.aow.ui.base.BaseFragment
import java.util.*
import kotlinx.android.synthetic.main.fragment_all_songs.*
import com.musicplayer.aow.data.model.Song



class AllSongsFragment : BaseFragment(){

    var songModelData:ArrayList<Song> = ArrayList()
    var songListAdapter:SongListAdapter? = null

    var mView : View? = null

    companion object {
        var PERMISSION_REQUEST_CODE = 12
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_all_songs, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view!!)
        mView = view
        loadData()
    }

    fun loadData(){
        //context or activity
        var songCursor = CursorDB().callCursor(context)
        if (songCursor != null) {
            while (songCursor != null && songCursor.moveToNext()) {
                songModelData.add(CursorDB().cursorToMusic(songCursor))
            }
            songCursor.close()
        }else{
            Toast.makeText(context.applicationContext,"No music file on this device.", Toast.LENGTH_LONG).show()
        }
        //sort the song list in ascending order
        var songsList = songModelData.sortedWith(compareBy({ (it.title )!!.toLowerCase()}))
        songCursor!!.close()
        songListAdapter = SongListAdapter(songsList, context)
        var layoutManager = LinearLayoutManager(context)
        recycler_views.layoutManager = layoutManager
        recycler_views.adapter = songListAdapter
    }

}
