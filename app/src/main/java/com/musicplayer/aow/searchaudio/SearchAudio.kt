package com.musicplayer.aow.searchaudio

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.Button
import com.musicplayer.aow.R
import com.github.ybq.android.spinkit.SpinKitView
import com.musicplayer.aow.utils.ApplicationSettings
import android.widget.TextView
import com.musicplayer.aow.Injection
import com.musicplayer.aow.utils.Settings
import org.jetbrains.anko.act
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import org.jetbrains.anko.uiThread


class SearchAudio : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_audio)
        val toolbar = findViewById(R.id.toolbar2) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        toolbar.title = "SEARCH AUDIO FILES."
        toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_arrow_back_black)
        toolbar.setNavigationOnClickListener{
            finish()
        }

        //loader
        var loaderView = findViewById(R.id.spin_kit) as SpinKitView
        //back button
        var doneBtn  = findViewById(R.id.search_label) as Button
        doneBtn!!.setOnClickListener {
            finish()
        }

        var numbersOfSongs = findViewById(R.id.numbers_of_songs_label) as TextView

        loaderView!!.setOnClickListener {
            //run search in another thread using anko from jetbrains
            doAsync {
                var save_search = Settings.instance?.searchSongs()
//                var save_search = Settings.instance?.getSongsLocally()
                onComplete {
                    if (save_search != null) {
                        //show numbers of audio found
                        var songNumber = Settings.instance?.readSongs()?.size
                        numbersOfSongs.text = "Total numbers of Audio found: "+ songNumber
                        loaderView!!.visibility = View.INVISIBLE
                        numbersOfSongs.visibility = View.VISIBLE
                        doneBtn!!.visibility = View.VISIBLE
                    } else {
                        doneBtn!!.visibility = View.INVISIBLE
                        numbersOfSongs.text = "Error Searching for Audio files."
                        numbersOfSongs.visibility = View.VISIBLE
                    }
                }
            }

        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, SearchAudio::class.java)
            return intent
        }
    }
}
