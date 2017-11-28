package com.musicplayer.aow.ui.library.album.adapter

import android.annotation.TargetApi
import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.musicplayer.aow.R
import com.musicplayer.aow.ui.library.album.model.AlbumModel
import com.musicplayer.aow.ui.library.songs.`interface`.CustomItemClickListener
import java.io.File

/**
 * Created by Arca on 11/20/2017.
 */
class AlbumAdapter(context: Context,albumModel: List<AlbumModel>) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {
    val mSongModel = albumModel
    var context = context


    @TargetApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: AlbumViewHolder?, position: Int) {
        var model = mSongModel[position]
        var albumName = model.mAlbumName
        var albumArtist = model.mAlbumArtist
        holder!!.albumName.text = albumName
        holder!!.albumArtist.text = albumArtist
        if (model.mAlbumArt != null) {
            val albumData = model.mAlbumArt
            Glide.with(context).load(albumData)
                    .error(com.musicplayer.aow.R.drawable.vinyl_blue)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.albumArt)
        }else{
            holder.albumArt.setImageResource(R.drawable.vinyl_blue)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AlbumViewHolder {
        var view = LayoutInflater.from(parent!!.context).inflate(R.layout.container_fish,parent,false)
        return AlbumViewHolder(view)
    }

    //we get the count of the list
    override fun getItemCount(): Int {
        return mSongModel.size
    }

    class AlbumViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener{
        var albumName: TextView
        var albumArtist: TextView
        var albumArt: ImageView
        var mCustomItemClickListener: CustomItemClickListener? = null

        //intialization of our recycler ui elements
        init{
            albumName = itemView.findViewById(R.id.cardname) as TextView
            albumArtist = itemView.findViewById(R.id.cardart) as TextView
            albumArt = itemView.findViewById(R.id.ivFish) as ImageView
            //we set on click on each element of the list
            itemView.setOnClickListener(this)
        }

        //create a setonclick
        fun setOnCustomItemClickListener(customItemClickListener: CustomItemClickListener){
            this.mCustomItemClickListener = customItemClickListener
        }

        //the onclick function
        override fun onClick(view: View?) {
            //we pass in the view and the list element index position
            this.mCustomItemClickListener!!.onCustomItemClick(view!!,adapterPosition)
        }
    }

}