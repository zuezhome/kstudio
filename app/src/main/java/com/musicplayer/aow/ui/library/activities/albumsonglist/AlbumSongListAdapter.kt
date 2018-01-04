package com.musicplayer.aow.ui.library.activities.albumsonglist

import android.annotation.TargetApi
import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Build
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.musicplayer.aow.R
import com.musicplayer.aow.RxBus
import com.musicplayer.aow.data.model.Song
import com.musicplayer.aow.event.PlaySongEvent
import com.musicplayer.aow.ui.library.songs.`interface`.CustomItemClickListener
import com.musicplayer.aow.utils.TimeUtils.formatDuration
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import java.io.File


/**
 * Created by Arca on 12/1/2017.
 */
class AlbumSongListAdapter(context: Context, song: List<Song>):RecyclerView.Adapter<AlbumSongListAdapter.SongListViewHolder>() {


    val TAG = "SongListAdapter"
    var context = context
    val mSongModel = song

    @TargetApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: SongListViewHolder?, position: Int) {
        var model = mSongModel[position]
        var songName = model.title
        var songDuration = formatDuration(model.duration)
        var songArtist = model.artist
        holder!!.songTV.text = songName
        holder!!.duration.text = songDuration
        holder!!.songArtist.text = songArtist
        if (model.path != null) {
            val metadataRetriever = MediaMetadataRetriever()
            metadataRetriever.setDataSource(model.path)
            doAsync{
                val albumData = metadataRetriever.embeddedPicture
                onComplete {
                    Glide.with(context).load(albumData)
                            .error(R.drawable.vinyl_blue)
                            .centerCrop()
                            .into(holder.albumArt)
                }
            }
        } else {
            holder.albumArt.setImageResource(R.drawable.vinyl_blue)
        }
        //implementation of item click
        holder!!.setOnCustomItemClickListener(object : CustomItemClickListener {
            override fun onCustomItemClick(view: View, position: Int) {
                RxBus.instance!!.post(PlaySongEvent(mSongModel[position]))
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SongListViewHolder {
        var view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_local_music, parent, false)
        return SongListViewHolder(view)
    }

    //we get the count of the list
    override fun getItemCount(): Int {
        return mSongModel.size
    }

    class SongListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var songTV: TextView
        var duration: TextView
        var songArtist: TextView
        var albumArt: AppCompatImageView
        var mCustomItemClickListener: CustomItemClickListener? = null

        //intialization of our recycler ui elements
        init {
            songTV = itemView.findViewById(R.id.text_view_name) as TextView
            duration = itemView.findViewById(R.id.text_view_duration) as TextView
            songArtist = itemView.findViewById(R.id.text_view_artist) as TextView
            albumArt = itemView.findViewById(R.id.image_view_file) as AppCompatImageView
            //we set on click on each element of the list
            itemView.setOnClickListener(this)
        }

        //create a setonclick
        fun setOnCustomItemClickListener(customItemClickListener: CustomItemClickListener) {
            this.mCustomItemClickListener = customItemClickListener
        }

        //the onclick function
        override fun onClick(view: View?) {
            //we pass in the view and the list element index position
            this.mCustomItemClickListener!!.onCustomItemClick(view!!, adapterPosition)
        }
    }
}