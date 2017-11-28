package com.musicplayer.aow.ui.music

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.musicplayer.aow.R

/**
 * Created by Arca on 10/29/2017.
 */

class PopAdapter// create constructor to innitilize context and data sent from MainActivity
(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(context)
    }

    // Inflate the layout when viewholder created
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = inflater.inflate(R.layout.container_fish, parent, false)
        return MyHolder(view)
    }

    // Bind data
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // Get current position of item in recyclerview to bind data and assign values from list
        val myHolder = holder as PopAdapter.MyHolder
        // load image into image view using glide
        Glide.with(context).load("http://zuezhome.com/test/images/")
                .placeholder(R.drawable.musicians_album)
                .error(R.drawable.musicians_album)
                .into(myHolder.ivFish)

    }

    // return total item from List
    override fun getItemCount(): Int {
        return 0
    }


    inner class MyHolder// create constructor to get widget reference
    (itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var textFishName: TextView
        internal var ivFish: ImageView

        init {
            textFishName = itemView.findViewById(com.musicplayer.aow.R.id.cardname) as TextView
            ivFish = itemView.findViewById(com.musicplayer.aow.R.id.ivFish) as ImageView
        }
    }

}
