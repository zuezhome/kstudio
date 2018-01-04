package com.musicplayer.aow.ui.library.playlist


import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import butterknife.BindView
import butterknife.ButterKnife
import com.musicplayer.aow.R
import com.musicplayer.aow.data.model.PlayList
import com.musicplayer.aow.data.source.AppRepository
import com.musicplayer.aow.utils.DeviceUtils
import com.musicplayer.aow.utils.layout.PreCachingLayoutManager
import kotlinx.android.synthetic.main.fragment_play_lists.*
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.util.*


class PlayListsFragment : Fragment(){

    @BindView(R.id.recycler_view)
    internal var recyclerView: RecyclerView? = null
    @BindView(R.id.progress_bar)
    internal var progressBar: ProgressBar? = null
    private val mRepository: AppRepository? = AppRepository.instance
    private val mSubscriptions: CompositeSubscription? = null

    var mPlayLists: MutableList<PlayList>? = null

//    var playlistModelData:List<PlayList> = ArrayList()
    var playListAdapter:PlayListAdapter? = null
    var playList:List<PlayList>? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_play_lists, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view!!)

        //create playlist
        create_playlist.setOnClickListener {
            var newPlayList = PlayList()
            newPlayList?.name = "New"
            createPlayList(newPlayList)
//            val actionPlayList = PlayListAction(loadPlayLists())
//            actionPlayList.createPlayList(newPlayList)
        }

        loadPlayLists()
    }

    fun loadData(playlist: List<PlayList>?){
        var playlistModelData = playlist
        //get audio from shearPref
        if (playlistModelData == null){
//            recycler_playlist_views.visibility = View.INVISIBLE
        }else {
            //sort the song list in ascending order
            playList = playlistModelData.sortedWith(compareBy({ (it.name)!!.toLowerCase() }))
            //Save to database
            playListAdapter = PlayListAdapter(activity, playList!!)
            //Setup layout manager
            val layoutManager = PreCachingLayoutManager(activity)
            layoutManager.orientation = LinearLayoutManager.VERTICAL
            layoutManager.setExtraLayoutSpace(DeviceUtils.getScreenHeight(activity))
            recycler_playlist_views.setHasFixedSize(true)
            recycler_playlist_views.layoutManager = layoutManager
            recycler_playlist_views.adapter = playListAdapter
        }
    }

    fun loadPlayLists(){
        val subscription = mRepository!!.playLists()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<List<PlayList>>() {
                    override fun onStart() {
                        //
                    }

                    override fun onCompleted() {
                        //
                    }

                    override fun onError(e: Throwable) {
                        //
                    }

                    override fun onNext(playLists: List<PlayList>) {
                        loadData(playLists)
//                        mView.onPlayListsLoaded(playLists)
                    }
                })
        mSubscriptions?.add(subscription)
    }

    fun createPlayList(playList: PlayList) {
        val subscription = mRepository
                ?.create(playList)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(object : Subscriber<PlayList>() {
                    override fun onStart() {
                        //
                    }

                    override fun onCompleted() {
                        //
                    }

                    override fun onError(e: Throwable) {
                       //
                    }

                    override fun onNext(result: PlayList) {
                        loadPlayLists()
//                        mView.onPlayListCreated(result)
                    }
                })
        mSubscriptions?.add(subscription)
    }

    fun editPlayList(playList: PlayList) {
        val subscription = mRepository
                ?.update(playList)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(object : Subscriber<PlayList>() {
                    override fun onStart() {
                        //
                    }

                    override fun onCompleted() {
                        //
                    }

                    override fun onError(e: Throwable) {
                        //
                    }

                    override fun onNext(result: PlayList) {
//                        mView.onPlayListEdited(result)
                    }
                })
        mSubscriptions?.add(subscription)
    }

    fun deletePlayList(playList: PlayList) {
        val subscription = mRepository?.delete(playList)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(object : Subscriber<PlayList>() {
                    override fun onStart() {
                        //
                    }

                    override fun onCompleted() {
                        //
                    }

                    override fun onError(e: Throwable) {
                        //
                    }

                    override fun onNext(playList: PlayList) {
//                        mView.onPlayListDeleted(playList)
                    }
                })
        mSubscriptions?.add(subscription)
    }

    private var mListener: onFragmentInteractionListener? = null

    interface onFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is onFragmentInteractionListener) {
            mListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

}
