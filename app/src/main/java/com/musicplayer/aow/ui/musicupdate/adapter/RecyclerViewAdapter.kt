package com.musicplayer.aow.ui.musicupdate.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.widget.TextView
import android.widget.Toast
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.musicplayer.aow.R
import com.musicplayer.aow.ui.musicupdate.model.sectiondatamodel.SectionDataModel
import java.util.*


/**
 * Created by Arca on 11/28/2017.
 */
class RecyclerViewAdapter(val mContext: Context, val dataList: ArrayList<SectionDataModel>?) : RecyclerView.Adapter<RecyclerViewAdapter.ItemRowHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ItemRowHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_item, null)
        return ItemRowHolder(v)
    }

    override fun onBindViewHolder(itemRowHolder: ItemRowHolder, i: Int) {
        val sectionName = dataList!!.get(i).headerTitle
        val singleSectionItems = dataList!!.get(i).allItemsInSection

        itemRowHolder.itemTitle.setText(sectionName)
        val itemListDataAdapter = SectionListDataAdapter(mContext, singleSectionItems)

        itemRowHolder.recycler_view_list.setHasFixedSize(true)
        itemRowHolder.recycler_view_list.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        itemRowHolder.recycler_view_list.adapter = itemListDataAdapter

        itemRowHolder.btnMore.setOnClickListener{
                Toast.makeText(mContext, "click event on more, " + sectionName, Toast.LENGTH_SHORT).show()
        }

        /* Glide.with(mContext)
                .load(feedItem.getImageURL())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(R.drawable.bg)
                .into(feedListRowHolder.thumbView);*/
    }

    override fun getItemCount(): Int {
        return if (null != dataList) dataList!!.size else 0
    }

    inner class ItemRowHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemTitle: TextView
        var recycler_view_list: RecyclerView
        var btnMore: Button

        init {
            this.itemTitle = view.findViewById(R.id.itemTitle) as TextView
            this.recycler_view_list = view.findViewById(R.id.recycler_view_list) as RecyclerView
            this.btnMore = view.findViewById(R.id.btnMore) as Button
        }

    }

}