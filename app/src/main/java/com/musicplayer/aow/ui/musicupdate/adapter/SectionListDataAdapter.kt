package com.musicplayer.aow.ui.musicupdate.adapter

import android.content.Context
import android.widget.Toast
import android.widget.TextView
import android.support.v7.widget.RecyclerView
import com.musicplayer.aow.ui.musicupdate.model.singleitemmodel.SingleItemModel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.musicplayer.aow.R
import java.util.*


/**
 * Created by Arca on 11/28/2017.
 */
class SectionListDataAdapter(private val mContext: Context, private val itemsList: ArrayList<SingleItemModel>?) : RecyclerView.Adapter<SectionListDataAdapter.SingleItemRowHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SingleItemRowHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_single_card, null)
        return SingleItemRowHolder(v)
    }

    override fun onBindViewHolder(holder: SingleItemRowHolder, i: Int) {
        val singleItem = itemsList!!.get(i)
        holder.tvTitle.setText(singleItem.name)
        /* Glide.with(mContext)
                .load(feedItem.getImageURL())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(R.drawable.bg)
                .into(feedListRowHolder.thumbView);*/
    }

    override fun getItemCount(): Int {
        return if (null != itemsList) itemsList!!.size else 0
    }

    inner class SingleItemRowHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvTitle: TextView
        var itemImage: ImageView

        init {
            this.tvTitle = view.findViewById(R.id.tvTitle) as TextView
            this.itemImage = view.findViewById(R.id.itemImage) as ImageView

            view.setOnClickListener{
                    Toast.makeText(view.getContext(), tvTitle.text, Toast.LENGTH_SHORT).show()
            }
        }

    }

}