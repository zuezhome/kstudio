package com.musicplayer.aow.ui.library.playlist

import com.musicplayer.aow.data.model.PlayList
import com.musicplayer.aow.data.source.AppRepository
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

/**
 * Created by Arca on 1/1/2018.
 */
class PlayListAction(runAction: Any) {
    val runAction = runAction

    private val mRepository: AppRepository? = AppRepository.instance
    private val mSubscriptions: CompositeSubscription? = null

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
                        runAction
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
                        runAction
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
                        runAction
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
                        runAction
//                        mView.onPlayListDeleted(playList)
                    }
                })
        mSubscriptions?.add(subscription)
    }
}