package com.musicplayer.aow.ui.allsongs.Adapter

import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Parcel
import android.os.Parcelable
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.musicplayer.aow.R
import com.musicplayer.aow.data.model.Song
import com.musicplayer.aow.event.PlaySongEvent
import com.musicplayer.aow.ui.allsongs.`interface`.CustomItemClickListener
import com.musicplayer.aow.utils.AlbumUtils
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Arca on 11/9/2017.
 */
class SongListAdapter(song: List<Song>, context: Context):RecyclerView.Adapter<SongListAdapter.SongListViewHolder>() {
    var mContext = context
    var mSongModel = song
    var images:ArrayList<Bitmap?> = ArrayList()
    var mAlbumArts = listImages()

    fun listImages(): ArrayList<Bitmap?>? {
        AsyncTaskExample().execute();
        return null
    }


    override fun onBindViewHolder(holder: SongListViewHolder?, position: Int) {
        var model = mSongModel[position]
        var songName = model.title
        var songDuration = toMinuteAndSecond(model.duration.toLong())
        var songArtist = model.artist
        holder!!.songTV.text = songName
        holder!!.duration.text = songDuration
        holder!!.songArtist.text = songArtist
        if (images[position] != null) {
            holder!!.albumArt.setImageBitmap(images[position])
        }else{
            holder!!.albumArt.setImageResource(R.drawable.vinyl_blue)
        }

        //implementation of item click
        holder!!.setOnCustomItemClickListener(object : CustomItemClickListener{
            override fun onCustomItemClick(view: View, position: Int) {
//                val song = mAdapter.getItem(position)
//                Toast.makeText(mContext,song.artist,Toast.LENGTH_SHORT).show()
//                RxBus.instance!!.post(PlaySongEvent(song))
                PlaySongEvent(model)
                    //play song//
//                    mediaPlayer.reset()
//                    mediaPlayer.setDataSource(model.mSongPath)
//                    mediaPlayer.prepare()
//                    mediaPlayer.start()
//                MusicPlayerActivity().playSong(song)
//
            }
        } )
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SongListViewHolder {
        var view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_local_music,parent,false)
        return SongListViewHolder(view)
    }

    //we get the count of the list
    override fun getItemCount(): Int {
        return mSongModel.size
    }

    //we convert the time received into MM:SS
    fun toMinuteAndSecond(millis:Long) : String{
        var duration = String.format("%2d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(millis)
                ))
        return duration
    }

    class SongListViewHolder(itemView: View):RecyclerView.ViewHolder(itemView), View.OnClickListener{
        var songTV: TextView
        var duration: TextView
        var songArtist: TextView
        var albumArt: AppCompatImageView
        var mCustomItemClickListener:CustomItemClickListener? = null

        //intialization of our recycler ui elements
        init{
            songTV = itemView.findViewById(R.id.text_view_name) as TextView
            duration = itemView.findViewById(R.id.text_view_duration) as TextView
            songArtist = itemView.findViewById(R.id.text_view_artist) as TextView
            albumArt = itemView.findViewById(R.id.image_view_file) as AppCompatImageView
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

    //perform system io operation in the background to get song album art
    //to avoid ui delay when loading and lags while scrolling the recycler view
    inner class AsyncTaskExample: AsyncTask<String, String, String>() {
        override fun doInBackground(vararg p0: String?): String? {
            for (data in mSongModel){
                images.add(AlbumUtils.parseAlbum(data))
            }
            return null
        }

    }
}