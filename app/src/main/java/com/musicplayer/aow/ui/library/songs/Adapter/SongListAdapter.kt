package com.musicplayer.aow.ui.library.songs.Adapter

import android.annotation.TargetApi
import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Build
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider
import com.musicplayer.aow.R
import com.musicplayer.aow.RxBus
import com.musicplayer.aow.data.model.PlayList
import com.musicplayer.aow.data.model.Song
import com.musicplayer.aow.event.PlayListNowEvent
import com.musicplayer.aow.player.Player
import com.musicplayer.aow.ui.library.songs.`interface`.CustomItemClickListener
import com.musicplayer.aow.utils.TimeUtils.formatDuration
import es.claucookie.miniequalizerlibrary.EqualizerView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete


/**
 * Created by Arca on 11/9/2017.
 */
class SongListAdapter(context: Context,song: List<Song>):RecyclerView.Adapter<SongListAdapter.SongListViewHolder>(), SectionTitleProvider {

    var mPlayer = Player.instance
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

        doAsync{
            val metadataRetriever = MediaMetadataRetriever()
            metadataRetriever.setDataSource(model.path)
            val albumData = metadataRetriever.embeddedPicture
            onComplete {

                Glide.with(context).load(albumData)
                        .error(com.musicplayer.aow.R.drawable.vinyl_blue)
                        .into(holder.albumArt)
                //implementation of item click
                holder!!.setOnCustomItemClickListener(object : CustomItemClickListener{
                    override fun onCustomItemClick(view: View, position: Int) {
                        RxBus.instance!!.post(PlayListNowEvent(PlayList(mSongModel),position))
                        if (mPlayer!!.isPlaying){
                            if (mPlayer!!.playingSong == model) {
                                holder!!.eq.visibility = View.VISIBLE
                                holder!!.eq.animateBars()
                                holder.albumArt.visibility = View.INVISIBLE
                            } else {
                                holder!!.eq.stopBars()
                                holder!!.eq.visibility = View.INVISIBLE
                                holder.albumArt.visibility = View.VISIBLE
                            }
                        }
                    }
                } )

                //here we set item click for songs
                //to set options
                holder!!.option.setOnClickListener {
                    var popMenu = PopupMenu(context, holder!!.option)
                    popMenu.menuInflater.inflate(R.menu.music_action, popMenu.menu)
                    popMenu.setOnMenuItemClickListener {
                        e -> Toast.makeText(context, "" + e.title, Toast.LENGTH_SHORT).show()
                        true
                    }
                    popMenu.show()
                }

                if (mPlayer!!.isPlaying) {
                    if (mPlayer!!.playingSong == model) {
                        holder!!.eq.visibility = View.VISIBLE
                        holder.albumArt.visibility = View.INVISIBLE
                        holder!!.eq.animateBars()
                    } else {
                        holder!!.eq.stopBars()
                        holder!!.eq.visibility = View.INVISIBLE
                        holder.albumArt.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun getSectionTitle(position: Int): String {
        //this String will be shown in a bubble for specified position
        return getItem(position)!!.substring(0, 1);
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SongListViewHolder {
        var view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_local_music,parent,false)
        return SongListViewHolder(view)
    }

    //we get the count of the list
    override fun getItemCount(): Int {
        return mSongModel.size
    }

    fun getItem(position: Int): String? {
        return mSongModel.get(position).title
    }

    class SongListViewHolder(itemView: View):RecyclerView.ViewHolder(itemView), View.OnClickListener{
        var songTV: TextView
        var duration: TextView
        var songArtist: TextView
        var albumArt: AppCompatImageView
        var eq: EqualizerView
        var option: AppCompatImageView
        var mCustomItemClickListener:CustomItemClickListener? = null

        //intialization of our recycler ui elements
        init{
            songTV = itemView.findViewById(R.id.text_view_name) as TextView
            duration = itemView.findViewById(R.id.text_view_duration) as TextView
            songArtist = itemView.findViewById(R.id.text_view_artist) as TextView
            albumArt = itemView.findViewById(R.id.image_view_file) as AppCompatImageView
            eq = itemView.findViewById(R.id.equalizer_view) as EqualizerView
            eq.visibility = View.INVISIBLE
            option = itemView.findViewById(R.id.item_button_action) as AppCompatImageView
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

