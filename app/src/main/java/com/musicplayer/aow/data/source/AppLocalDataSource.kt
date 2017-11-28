package com.musicplayer.aow.data.source

import android.content.Context
import android.util.Log
import com.litesuits.orm.LiteOrm
import com.litesuits.orm.db.assit.QueryBuilder
import com.litesuits.orm.db.model.ConflictAlgorithm
import com.musicplayer.aow.data.model.*
import com.musicplayer.aow.utils.DBUtils
import rx.Observable

import java.io.File
import java.util.Date

/**
 * Created with Android Studio.
 * User:
 * Date:
 * Time:
 * Desc: AppContract
 */
/* package */ internal class AppLocalDataSource(private val mContext: Context?, private val mLiteOrm: LiteOrm?) : AppContract {

    // Play List

    override fun playLists(): Observable<MutableList<PlayList>> {
        return Observable.create { subscriber ->
            val playLists = mLiteOrm?.query<PlayList>(PlayList::class.java)
            if (playLists != null) {
                    // First query, create the default play list
                    val playList = mContext?.let { DBUtils.generateFavoritePlayList(it) }
                    val result = mLiteOrm?.save(playList)
                    Log.d(TAG, "Create default playlist(Favorite) with " + if (result == 1L) "success" else "failure")
                    playLists.add(playList)
            }
            subscriber.onNext(playLists)
            subscriber.onCompleted()
        }
    }

    //i messed with this
    override fun cachedPlayLists(): MutableList<PlayList> {
        var playLists = mLiteOrm?.query<PlayList>(PlayList::class.java)
        return playLists!!
    }

    override fun create(playList: PlayList): Observable<PlayList> {
        return Observable.create { subscriber ->
            val now = Date()
            playList.createdAt = now
            playList.updatedAt = now

            val result = mLiteOrm?.save(playList)
            if (result!! > 0) {
                subscriber.onNext(playList)
            } else {
                subscriber.onError(Exception("Create play list failed"))
            }
            subscriber.onCompleted()
        }
    }

    override fun update(playList: PlayList): Observable<PlayList> {
        return Observable.create { subscriber ->
            playList.updatedAt = Date()

            val result = mLiteOrm?.update(playList)?.toLong()
            if (result!! > 0) {
                subscriber.onNext(playList)
            } else {
                subscriber.onError(Exception("Update play list failed"))
            }
            subscriber.onCompleted()
        }
    }

    override fun delete(playList: PlayList): Observable<PlayList> {
        return Observable.create { subscriber ->
            val result = mLiteOrm?.delete(playList)?.toLong()
            if (result!! > 0) {
                subscriber.onNext(playList)
            } else {
                subscriber.onError(Exception("Delete play list failed"))
            }
            subscriber.onCompleted()
        }
    }

    // Folder

    override fun folders(): Observable<MutableList<Folder>> {
        return Observable.create { subscriber ->
            if (PreferenceManager.isFirstQueryFolders(mContext)) {
                val defaultFolders = DBUtils.generateDefaultFolders()
                val result = mLiteOrm?.save(defaultFolders)?.toLong()
                Log.d(TAG, "Create default folders effected " + result + "rows")
                PreferenceManager.reportFirstQueryFolders(mContext)
            }
            val folders = mLiteOrm?.query(
                    QueryBuilder.create<Folder>(Folder::class.java).appendOrderAscBy(Folder.COLUMN_NAME)
            )
            subscriber.onNext(folders)
            subscriber.onCompleted()
        }
    }

    override fun create(folder: Folder): Observable<Folder> {
        return Observable.create { subscriber ->
            folder.createdAt = Date()

            val result = mLiteOrm?.save(folder)
            if (result!! > 0) {
                subscriber.onNext(folder)
            } else {
                subscriber.onError(Exception("Create folder failed"))
            }
            subscriber.onCompleted()
        }
    }

    override fun create(folders: List<Folder>): Observable<MutableList<Folder>> {
        return Observable.create { subscriber ->
            val now = Date()
            for (folder in folders) {
                folder.createdAt = now
            }

            val result = mLiteOrm?.save(folders)?.toLong()
            if (result!! > 0) {
                val allNewFolders = mLiteOrm?.query(
                        QueryBuilder.create<Folder>(Folder::class.java).appendOrderAscBy(Folder.COLUMN_NAME)
                )
                subscriber.onNext(allNewFolders)
            } else {
                subscriber.onError(Exception("Create folders failed"))
            }
            subscriber.onCompleted()
        }
    }

    override fun update(folder: Folder): Observable<Folder> {
        return Observable.create { subscriber ->
            mLiteOrm?.delete(folder)
            val result = mLiteOrm?.save(folder)
            if (result!! > 0) {
                subscriber.onNext(folder)
            } else {
                subscriber.onError(Exception("Update folder failed"))
            }
            subscriber.onCompleted()
        }
    }

    override fun delete(folder: Folder): Observable<Folder> {
        return Observable.create { subscriber ->
            val result = mLiteOrm?.delete(folder)?.toLong()
            if (result!! > 0) {
                subscriber.onNext(folder)
            } else {
                subscriber.onError(Exception("Delete folder failed"))
            }
            subscriber.onCompleted()
        }
    }

    override fun insert(songs: MutableList<Song>): Observable<MutableList<Song>> {
        return Observable.create { subscriber ->
            for (song in songs) {
                mLiteOrm?.insert(song, ConflictAlgorithm.Abort)
            }
            val allSongs = mLiteOrm?.query<Song>(Song::class.java)
            var file: File
            val iterator = allSongs?.iterator()
            while (iterator?.hasNext()!!) {
                val song = iterator.next()
                file = File(song.path!!)
                val exists = file.exists()
                if (!exists) {
                    iterator.remove()
                    mLiteOrm?.delete(song)
                }
            }
            subscriber.onNext(allSongs)
            subscriber.onCompleted()
        }
    }

    override fun update(song: Song): Observable<Song> {
        return Observable.create { subscriber ->
            val result = mLiteOrm?.update(song)
            if (result!! > 0) {
                subscriber.onNext(song)
            } else {
                subscriber.onError(Exception("Update song failed"))
            }
            subscriber.onCompleted()
        }
    }

    override fun setSongAsFavorite(song: Song, isFavorite: Boolean): Observable<Song> {
        return Observable.create { subscriber ->
            val playLists = mLiteOrm?.query(
                    QueryBuilder.create<PlayList>(PlayList::class.java).whereEquals(PlayList.COLUMN_FAVORITE, true.toString())
            )
            if (playLists?.isEmpty()!!) {
                val defaultFavorite = DBUtils.generateFavoritePlayList(mContext)
                playLists.add(defaultFavorite)
            }
            val favorite = playLists?.get(0)
            song.isFavorite = isFavorite
            favorite.updatedAt = Date()
            if (isFavorite) {
                // Insert song to the beginning of the list
                favorite.addSong(song, 0)
            } else {
                favorite.removeSong(song)
            }
            mLiteOrm?.insert(song, ConflictAlgorithm.Replace)
            val result = mLiteOrm?.insert(favorite, ConflictAlgorithm.Replace)
            if (result!! > 0) {
                subscriber.onNext(song)
            } else {
                if (isFavorite) {
                    subscriber.onError(Exception("Set song as favorite failed"))
                } else {
                    subscriber.onError(Exception("Set song as unfavorite failed"))
                }
            }
            subscriber.onCompleted()
        }
    }

    companion object {

        private val TAG = "AppLocalDataSource"
    }
}
