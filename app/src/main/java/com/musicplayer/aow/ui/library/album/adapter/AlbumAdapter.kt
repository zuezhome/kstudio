package com.musicplayer.aow.ui.library.album.adapter

import android.annotation.TargetApi
import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Build
import android.app.Activity
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.musicplayer.aow.R
import com.musicplayer.aow.ui.library.activities.AlbumSongs
import com.musicplayer.aow.ui.library.album.model.AlbumModel
import com.musicplayer.aow.ui.library.songs.`interface`.CustomItemClickListener
import com.musicplayer.aow.utils.AlbumUtils
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import com.musicplayer.aow.R.id.imageView
import com.musicplayer.aow.ui.main.MainActivity
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v4.view.ViewCompat
import org.jetbrains.anko.onComplete


/**
 * Created by Arca on 11/20/2017.
 */
class AlbumAdapter(context: Context,activity: Activity,albumModel: List<AlbumModel>) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {
    val mSongModel = albumModel
    var context = context
    var activity = activity


    @TargetApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: AlbumViewHolder?, position: Int) {
        var model = mSongModel[position]
        var albumName = model.mAlbumName
        var albumArtist = model.mAlbumArtist
        holder!!.albumName.text = albumName
        holder!!.albumArtist.text = albumArtist
        doAsync {
            val metadataRetriever = MediaMetadataRetriever()
            metadataRetriever.setDataSource(model.mAlbumArt)
            val albumData = metadataRetriever.embeddedPicture
            onComplete {
                Glide.with(context).load(albumData)
                        .error(R.drawable.vinyl_blue)
                        .fitCenter()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .thumbnail(0.5f)
                        .into(holder.albumArt)

                holder!!.cardView.setOnClickListener {
                    val intent = AlbumSongs.newIntent(context)
                    intent.putExtra("com.musicplayer.aow.album.name", albumName)
                    startActivity(context, intent, null)
                }
            }
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
        var cardView: CardView
        var mCustomItemClickListener: CustomItemClickListener? = null

        //intialization of our recycler ui elements
        init{
            albumName = itemView.findViewById(R.id.cardname) as TextView
            albumArtist = itemView.findViewById(R.id.cardart) as TextView
            albumArt = itemView.findViewById(R.id.ivFish) as ImageView
            cardView = itemView.findViewById(R.id.card_view_container) as CardView
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