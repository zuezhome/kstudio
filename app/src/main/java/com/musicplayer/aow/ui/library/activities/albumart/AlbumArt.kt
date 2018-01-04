package com.musicplayer.aow.ui.library.activities.albumart

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.musicplayer.aow.R

/**
 * Created by Arca on 12/30/2017.
 */
class AlbumArt : AppCompatActivity() {

    private var albumArt: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_art)
        val toolbar = findViewById(R.id.toolbar_album_art) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_arrow_back_black)
        toolbar.setNavigationOnClickListener{
            finish()
        }

        albumArt = findViewById(R.id.album_art) as ImageView
        //paying audio from other apps
        val intent = intent
        if (intent != null) {
            // To get the data use
            val data = intent.getStringExtra("image.path")
            if (data != null) {
                val metadataRetriever = MediaMetadataRetriever()
                metadataRetriever.setDataSource(data)
                val albumData = metadataRetriever.embeddedPicture

                Glide.with(this@AlbumArt).load(albumData)
                        .error(com.musicplayer.aow.R.drawable.vinyl_blue)
                        .into(albumArt)
            }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, AlbumArt::class.java)
            return intent
        }
    }

}