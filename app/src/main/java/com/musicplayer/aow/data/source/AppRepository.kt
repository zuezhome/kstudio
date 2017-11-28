package com.musicplayer.aow.data.source

import com.musicplayer.aow.Injection
import com.musicplayer.aow.data.model.*
import com.musicplayer.aow.data.source.db.LiteOrmHelper
import rx.Observable
import rx.functions.Action1

import java.util.ArrayList

class AppRepository private constructor() : AppContract {

    private val mLocalDataSource: AppLocalDataSource

    private var mCachedPlayLists: List<PlayList>? = null

    init {
        mLocalDataSource = AppLocalDataSource(Injection.provideContext(), LiteOrmHelper.instance)
    }

    // Play List
    override fun playLists(): Observable<MutableList<PlayList>> {
        return mLocalDataSource.playLists()
                .doOnNext { playLists -> mCachedPlayLists = playLists }
    }

    override fun cachedPlayLists(): MutableList<PlayList> {
        return if (mCachedPlayLists == null) {
            ArrayList(0)
        } else mCachedPlayLists as MutableList<PlayList>
    }

    override fun create(playList: PlayList): Observable<PlayList> {
        return mLocalDataSource.create(playList)
    }

    override fun update(playList: PlayList): Observable<PlayList> {
        return mLocalDataSource.update(playList)
    }

    override fun delete(playList: PlayList): Observable<PlayList> {
        return mLocalDataSource.delete(playList)
    }

    // Folders

    override fun folders(): Observable<MutableList<Folder>> {
        return mLocalDataSource.folders()
    }

    override fun create(folder: Folder): Observable<Folder> {
        return mLocalDataSource.create(folder)
    }

    override fun create(folders: List<Folder>): Observable<MutableList<Folder>> {
        return mLocalDataSource.create(folders)
    }

    override fun update(folder: Folder): Observable<Folder> {
        return mLocalDataSource.update(folder)
    }

    override fun delete(folder: Folder): Observable<Folder> {
        return mLocalDataSource.delete(folder)
    }

    override fun insert(songs: MutableList<Song>): Observable<MutableList<Song>> {
        return mLocalDataSource.insert(songs)
    }

    override fun update(song: Song): Observable<Song> {
        return mLocalDataSource.update(song)
    }

    override fun setSongAsFavorite(song: Song, favorite: Boolean): Observable<Song> {
        return mLocalDataSource.setSongAsFavorite(song, favorite)
    }

    companion object {

        @Volatile private var sInstance: AppRepository? = null

        val instance: AppRepository?
            get() {
                if (sInstance == null) {
                    synchronized(AppRepository::class.java) {
                        if (sInstance == null) {
                            sInstance = AppRepository()
                        }
                    }
                }
                return sInstance
            }
    }
}

