package com.musicplayer.aow.ui.music

import android.app.Activity
import android.os.Environment
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.view.Gravity
import android.widget.Button
import com.musicplayer.aow.R

import java.io.File
import java.util.ArrayList

class PopActivity : Activity() {

    internal var btn: Button? = null

    private var mRVFishPrice: RecyclerView? = null
    private var mAdapter: PopAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pop)
        //add your objects/initialization here

        //to make the layout looks poped up
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        val width = dm.widthPixels
        val height = dm.heightPixels
        window.setLayout((width * .9).toInt(), (height * .8).toInt())
        val params = window.attributes
        params.gravity = Gravity.CENTER
        params.x = 0
        params.y = -20
        window.attributes = params

        // Setup and Handover data to recyclerview
        mRVFishPrice = findViewById(R.id.fishPriceList) as RecyclerView
        mRVFishPrice!!.adapter = mAdapter
        mRVFishPrice!!.layoutManager = GridLayoutManager(this, 2)
        //      mRVFishPrice.setLayoutManager(new LinearLayoutManager(this));

    }

    companion object {

        fun scanDir(dir: File?): List<File> {
            val songs = ArrayList<File>()
            if (dir != null && dir.isDirectory) {
                val files = dir.listFiles()
                if (files.size > 0) {
                    for (file in files) {
                        if (file.isDirectory && !file.isHidden) {
                            songs.addAll(scanDir(file))
                        } else if (file.isFile) {
                            if (file.name.endsWith(".mp3")) {
                                songs.add(file)
                            }
                        }
                    }
                }
            }
            return songs
        }
    }

}
