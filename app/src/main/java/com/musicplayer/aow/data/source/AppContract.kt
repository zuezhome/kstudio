package com.musicplayer.aow.data.source

import com.musicplayer.aow.data.model.*

import rx.Observable

/**
 * Created with Android Studio.
 * User:
 * Date:
 * Time:
 * Desc: AppContract
 */
internal interface AppContract {

    // Play List

    fun playLists(): Observable<List<PlayList>>?

    fun cachedPlayLists(): MutableList<PlayList>?

    fun create(playList: PlayList): Observable<PlayList>

    fun update(playList: PlayList): Observable<PlayList>

    fun delete(playList: PlayList): Observable<PlayList>

    // Folder

    fun folders(): Observable<MutableList<Folder>>

    fun create(folder: Folder): Observable<Folder>

    fun create(folders: List<Folder>): Observable<MutableList<Folder>>

    fun update(folder: Folder): Observable<Folder>

    fun delete(folder: Folder): Observable<Folder>

    // Song

    fun insert(songs: MutableList<Song>): Observable<MutableList<Song>>

    fun update(song: Song): Observable<Song>

    fun setSongAsFavorite(song: Song, favorite: Boolean): Observable<Song>

}
