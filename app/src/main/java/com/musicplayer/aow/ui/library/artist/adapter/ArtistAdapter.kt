package com.musicplayer.aow.ui.library.artist.adapter

import android.annotation.TargetApi
import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Build
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.musicplayer.aow.R
import com.musicplayer.aow.ui.library.activities.ArtistSongs
import com.musicplayer.aow.ui.library.artist.model.ArtistModel
import com.musicplayer.aow.ui.library.songs.`interface`.CustomItemClickListener
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete

/**
 * Created by Arca on 11/27/2017.
 */
class ArtistAdapter(context: Context, albumModel: List<ArtistModel>) : RecyclerView.Adapter<ArtistAdapter.AlbumViewHolder>() {
    val mSongModel = albumModel
    var context = context


    @TargetApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: AlbumViewHolder?, position: Int) {

        var model = mSongModel[position]
        var numOfSong = model.mNumberOfSongs
        var albumArtist = model.mAlbumArtist
        holder!!.noOfSongs.text = numOfSong.toString().plus(" songs")
        holder!!.albumArtist.text = albumArtist

        holder!!.cardView.setOnClickListener {
            val intent = ArtistSongs.newIntent(context)
            intent.putExtra("com.musicplayer.aow.artist.name", albumArtist)
            startActivity(context, intent, null)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AlbumViewHolder {
        var view = LayoutInflater.from(parent!!.context).inflate(R.layout.artist_card_view,parent,false)
        return AlbumViewHolder(view)
    }

    //we get the count of the list
    override fun getItemCount(): Int {
        return mSongModel.size
    }

    class AlbumViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener{
        var noOfSongs: TextView
        var albumArtist: TextView
        var cardView: RelativeLayout
        var mCustomItemClickListener: CustomItemClickListener? = null

        //intialization of our recycler ui elements
        init{
            noOfSongs = itemView.findViewById(R.id.artist_no_songs) as TextView
            albumArtist = itemView.findViewById(R.id.artist_name) as TextView
            cardView = itemView.findViewById(R.id.card_view_container_artist) as RelativeLayout
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