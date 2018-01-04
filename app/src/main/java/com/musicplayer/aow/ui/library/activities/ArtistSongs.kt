package com.musicplayer.aow.ui.library.activities

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.*
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.musicplayer.aow.R
import com.musicplayer.aow.RxBus
import com.musicplayer.aow.data.model.Song
import com.musicplayer.aow.event.PlayAlbumNowEvent
import com.musicplayer.aow.ui.library.activities.albumart.AlbumArt
import com.musicplayer.aow.ui.library.activities.artistsonglist.ArtistSongsListAdapter
import com.musicplayer.aow.utils.ApplicationSettings
import com.musicplayer.aow.utils.CircleTransform
import com.musicplayer.aow.utils.Settings
import jp.wasabeef.glide.transformations.BlurTransformation
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.util.*

/**
 * Created by Arca on 12/1/2017.
 */
class ArtistSongs : AppCompatActivity() {

    var songModelData: ArrayList<Song>? = null
    var artistModelData: ArrayList<Song> = ArrayList()
    var songsList:List<Song>? = null
    private var artistArt: ImageView? = null
    private var artistArtMain: ImageView? = null
    private var artistArtName: TextView? = null
    var playArtistFab: FloatingActionButton? = null

    // A method to find height of the status bar
    fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_song_list)
        var toolbar = findViewById(R.id.toolbar_layer) as LinearLayout
        val toolbarNavigation = findViewById(R.id.toolbar_artist_song_list) as AppCompatImageView

        // Set the padding to match the Status Bar height
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0)
        toolbarNavigation.setOnClickListener {
            finish()
        }

        artistArt = findViewById(R.id.image_view_artist_art) as ImageView
        artistArtMain = findViewById(R.id.image_view_artist_art_main) as ImageView
        artistArtName = findViewById(R.id.image_view_artist_name_main) as TextView
        mArtistList = findViewById(R.id.artist_songs_recycler_views) as RecyclerView
        playArtistFab = findViewById(R.id.fab_play_artist) as FloatingActionButton
        //paying audio from other apps
        val intent = intent
        if (intent != null) {
            // To get the data use
            val data = intent.getStringExtra("com.musicplayer.aow.artist.name")
            if (data != null) {
                artistArtName!!.text = data
                loadData(data)
            }
        }

    }


    private var mArtistList: RecyclerView? = null
    private var mArtistAdapter: ArtistSongsListAdapter? = null
    private var artistNotNull: String? = null

    fun loadData(artist: String) {
        //context or activity
        songModelData = Settings.instance?.readSongs()
        if (songModelData!!.isEmpty()) {
            mArtistList!!.visibility = View.INVISIBLE
        } else {
            songModelData?.forEach { e ->
                if (e.artist == artist) {
                    if (e.path != null) {
                        artistNotNull = e.path
                    }
                    artistModelData.add(e)
                }
            }
        }

        val metadataRetriever = MediaMetadataRetriever()
        metadataRetriever.setDataSource(artistNotNull)
        val albumData = metadataRetriever.embeddedPicture

        if(albumData != null) {
            Glide.with(this@ArtistSongs).load(albumData)
                    .error(R.drawable.vinyl_blue)
                    .bitmapTransform(BlurTransformation(applicationContext, 25, 2))
                    .into(artistArt)
            Glide.with(this@ArtistSongs).load(albumData)
                    .error(R.drawable.vinyl_blue)
                    .into(artistArtMain)
//            .transform(CircleTransform(applicationContext))
        }else{
            Glide.with(this@ArtistSongs).load(R.drawable.gradient_skyline)
                    .error(R.drawable.gradient_skyline)
                    .into(artistArt)
            Glide.with(this@ArtistSongs).load(R.drawable.splashscreen_bg)
                    .error(R.drawable.splashscreen_bg)
                    .into(artistArtMain)
        }

        //set onclick listener to the main album art
        artistArtMain!!.setOnClickListener {
            val intent = AlbumArt.newIntent(applicationContext)
            intent.putExtra("image.path", artistNotNull)
            startActivity(intent)
        }

        //sort the song list in ascending order
        songsList = artistModelData.sortedWith(compareBy({ (it.title)!!.toLowerCase() }))
        //play as playlist when album art is clicked
        playArtistFab!!.setOnClickListener {
            RxBus.instance!!.post(PlayAlbumNowEvent(songsList!!))
        }

        mArtistAdapter = ArtistSongsListAdapter(this, songsList!!)
        mArtistList!!.addItemDecoration(DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL))
        mArtistList!!.adapter = mArtistAdapter
        var layoutManager = LinearLayoutManager(this)
        mArtistList!!.setHasFixedSize(true)
        mArtistList!!.layoutManager = layoutManager
    }

    companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, ArtistSongs::class.java)
            return intent
        }
    }
}