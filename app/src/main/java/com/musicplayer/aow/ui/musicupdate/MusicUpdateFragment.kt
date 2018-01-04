package com.musicplayer.aow.ui.musicupdate

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import butterknife.ButterKnife

import com.musicplayer.aow.R
import com.musicplayer.aow.ui.base.BaseFragment

import java.util.ArrayList
import com.musicplayer.aow.ui.musicupdate.model.sectiondatamodel.SectionDataModel
import com.musicplayer.aow.ui.musicupdate.model.singleitemmodel.SingleItemModel
import android.support.v7.widget.LinearLayoutManager
import com.musicplayer.aow.ui.musicupdate.adapter.RecyclerViewAdapter


class MusicUpdateFragment : BaseFragment(){

    private var refresh: Button? = null
    private var mUpdateBody: RecyclerView? = null
    var allSampleData: ArrayList<SectionDataModel>? = null

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view!!)
        refresh = view.findViewById(R.id.refresh) as Button

        // Setup and Handover data to recyclerview
        mUpdateBody = view.findViewById(R.id.music_update_recycler_view_body) as RecyclerView
        allSampleData = ArrayList()

        createDummyData()

        mUpdateBody!!.setHasFixedSize(true)
        val adapter = RecyclerViewAdapter(context, allSampleData)
        mUpdateBody!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mUpdateBody!!.setAdapter(adapter)

    }

    fun createDummyData() {
        for (i in 1..4) {
            val dm = SectionDataModel()
            dm.headerTitle = "Section " + i
            val singleItem = ArrayList<SingleItemModel>()
            for (j in 0..8) {
                singleItem.add(SingleItemModel("Song Name " + j, "URL " + j))
            }
            dm.allItemsInSection = singleItem
            allSampleData?.add(dm)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_music_update, container, false)
    }

    companion object {
        //For ReCycler com.musicpalyer.com.musicplayer.aow list server
        // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
        val CONNECTION_TIMEOUT = 10000
        val READ_TIMEOUT = 15000
    }
}
