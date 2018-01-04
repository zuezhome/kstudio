package com.musicplayer.aow.ui.library.playlist

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.musicplayer.aow.R
import com.musicplayer.aow.RxBus
import com.musicplayer.aow.data.model.PlayList
import com.musicplayer.aow.event.PlayListNowEvent
import com.musicplayer.aow.ui.library.songs.`interface`.CustomItemClickListener

/**
 * Created by Arca on 12/6/2017.
 */
class PlayListAdapter(context: Context, playlist: List<PlayList>): RecyclerView.Adapter<PlayListAdapter.PlayListViewHolder>() {

    val TAG = "PlayListAdapter"
    var context = context
    val mSongModel = playlist

    @TargetApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: PlayListViewHolder?, position: Int) {
        var model = mSongModel[position]
        var playlistName = model.name
        var playlistdetails = model.numOfSongs
        holder!!.pName.text = playlistName
        holder!!.pDetails.text = playlistdetails.toString()

        //implementation of item click
        holder!!.setOnCustomItemClickListener(object : CustomItemClickListener{
            override fun onCustomItemClick(view: View, position: Int) {

            }
        } )
        //here we set item click for songs
        //to set options
        holder!!.pOption.setOnClickListener {
            var popMenu = PopupMenu(context, holder!!.pOption)
            popMenu.menuInflater.inflate(R.menu.playlist_action, popMenu.menu)
            popMenu.setOnMenuItemClickListener {
                e ->
//                Toast.makeText(context, "" + e.title, Toast.LENGTH_SHORT).show()
                if (e.itemId == R.id.menu_item_play_now){
                    RxBus.instance!!.post(PlayListNowEvent(PlayList(model.songs),0))
                }else if(e.itemId == R.id.menu_item_delete){
                    val actionPlayList = PlayListAction("null")
                    actionPlayList.deletePlayList(PlayList(model.songs))
                }
                true
            }
            popMenu.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PlayListViewHolder {
        var view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_play_list,parent,false)
        return PlayListViewHolder(view)
    }

    //we get the count of the list
    override fun getItemCount(): Int {
        return mSongModel.size
    }

    class PlayListViewHolder(itemView: View):RecyclerView.ViewHolder(itemView), View.OnClickListener{
        var pName: TextView
        var pDetails: TextView
        var pOption: AppCompatImageView
        var mCustomItemClickListener: CustomItemClickListener? = null

        //intialization of our recycler ui elements
        init{
            pName = itemView.findViewById(R.id.text_view_name) as TextView
            pDetails = itemView.findViewById(R.id.text_view_info) as TextView
            pOption = itemView.findViewById(R.id.image_button_action) as AppCompatImageView
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