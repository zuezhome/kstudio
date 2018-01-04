package com.musicplayer.aow.ui.library.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.musicplayer.aow.R
import com.musicplayer.aow.RxBus
import com.musicplayer.aow.data.model.Song
import com.musicplayer.aow.event.PlayAlbumNowEvent
import com.musicplayer.aow.ui.library.activities.albumsonglist.AlbumSongListAdapter
import com.musicplayer.aow.utils.CircleTransform
import com.musicplayer.aow.utils.Settings
import jp.wasabeef.glide.transformations.BlurTransformation
import java.util.*
import com.musicplayer.aow.ui.library.activities.albumart.AlbumArt
import android.app.Activity
import android.graphics.Color
import android.view.WindowManager
import android.os.Build
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.*
import android.view.Window
import android.widget.LinearLayout


/**
 * Created by Arca on 12/1/2017.
 */
class AlbumSongs : AppCompatActivity() {

    var songModelData:ArrayList<Song>? = null
    var albumModelData: ArrayList<Song> = ArrayList()
    var songsList:List<Song>? = null
    private var albumArt: ImageView? = null
    private var albumArtMain: ImageView? = null
    private var albumArtName: TextView? = null
    var playAlbumFab: FloatingActionButton? = null

    // A method to find height of the status bar
    fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_song_list)
        var toolbar = findViewById(R.id.toolbar_layer) as LinearLayout
        val toolbarNavigation = findViewById(R.id.toolbar_album_song_list) as AppCompatImageView
//        setSupportActionBar(toolbar)


//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        supportActionBar!!.setDisplayShowHomeEnabled(true)
        // Set the padding to match the Status Bar height
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0)
        toolbarNavigation.setOnClickListener {
            finish()
        }

        albumArt = findViewById(R.id.image_view_album_art) as ImageView
        albumArtMain = findViewById(R.id.image_view_album_art_main) as ImageView
        albumArtName = findViewById(R.id.image_view_album_name_main) as TextView
        mAlbumList = findViewById(R.id.album_songs_recycler_views) as RecyclerView
        playAlbumFab = findViewById(R.id.fab_play_album) as FloatingActionButton
        //paying audio from other apps
        val intent = intent
        if (intent != null) {
            // To get the data use
            val data = intent.getStringExtra("com.musicplayer.aow.album.name")
            if (data != null) {
                albumArtName!!.text = data
                loadData(data)
            }
        }
    }

    private var mAlbumList: RecyclerView? = null
    private var mAlbumAdapter: AlbumSongListAdapter? = null
    private var albumNotNull: String? = null

    fun loadData(album: String){
        //context or activity
        songModelData = Settings.instance?.readSongs()
        if (songModelData!!.isEmpty()){
            mAlbumList!!.visibility = View.INVISIBLE
        }else {
                songModelData?.forEach{e ->
                    if (e.album == album ) {
                        if (e.path != null) {
                            albumNotNull = e.path
                        }
                        albumModelData.add(e)
                    }
                }
        }


        val metadataRetriever = MediaMetadataRetriever()
        metadataRetriever.setDataSource(albumNotNull)
        val albumData = metadataRetriever.embeddedPicture

        if (albumData != null) {
            Glide.with(this@AlbumSongs).load(albumData)
                    .error(R.drawable.vinyl_blue)
                    .bitmapTransform(BlurTransformation(applicationContext, 10, 2))
                    .into(albumArt)
            Glide.with(this@AlbumSongs).load(albumData)
                    .error(com.musicplayer.aow.R.drawable.vinyl_blue)
                    .into(albumArtMain)
        }else{
            Glide.with(this@AlbumSongs).load(R.drawable.gradient_skyline)
                    .error(R.drawable.gradient_skyline)
                    .into(albumArt)
            Glide.with(this@AlbumSongs).load(R.drawable.splashscreen_bg)
                    .error(R.drawable.splashscreen_bg)
                    .into(albumArtMain)
        }


        //set onclick listener to the main album art
        albumArtMain!!.setOnClickListener {
            val intent = AlbumArt.newIntent(applicationContext)
            intent.putExtra("image.path", albumNotNull)
            startActivity(intent)
        }


        //sort the song list in ascending order
        songsList = albumModelData.sortedWith(compareBy({ (it.title)!!.toLowerCase() }))
        //play as playlist when album art is clicked
        playAlbumFab!!.setOnClickListener {
            RxBus.instance!!.post(PlayAlbumNowEvent(songsList!!))
        }

        mAlbumAdapter = AlbumSongListAdapter(this, songsList!!)
        mAlbumList!!.addItemDecoration(DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL))
        mAlbumList!!.adapter = mAlbumAdapter
        mAlbumList!!.setHasFixedSize(true)
        mAlbumList!!.layoutManager = LinearLayoutManager(this)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, AlbumSongs::class.java)
            return intent
        }
    }
}