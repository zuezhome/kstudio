package com.musicplayer.aow.player

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.os.Binder
import android.os.IBinder
import android.support.v7.app.NotificationCompat
import android.widget.ImageView
import android.widget.RemoteViews
import com.bumptech.glide.Glide

import com.musicplayer.aow.R
import com.musicplayer.aow.data.model.*
import com.musicplayer.aow.ui.main.MainActivity
import com.musicplayer.aow.utils.AlbumUtils
import java.io.File

class PlaybackService : Service(), IPlayback, IPlayback.Callback {

    private var mContentViewBig: RemoteViews? = null
    private var mContentViewSmall: RemoteViews? = null

    private var mPlayer: Player? = Player.instance

    private val mBinder = LocalBinder()

    override var isPlaying: Boolean = false
        get() = mPlayer!!.isPlaying

    override var progress: Int = 0
        get() = mPlayer!!.progress

    override var playingSong: Song? = null
        get() = mPlayer!!.playingSong

    private val smallContentView: RemoteViews
        get() {
            if (mContentViewSmall == null) {
                mContentViewSmall = RemoteViews(packageName, R.layout.remote_view_music_player_small)
                setUpRemoteView(mContentViewSmall!!)
            }
            updateRemoteViews(mContentViewSmall!!)
            return mContentViewSmall!!
        }

    private val bigContentView: RemoteViews
        get() {
            if (mContentViewBig == null) {
                mContentViewBig = RemoteViews(packageName, R.layout.remote_view_music_player)
                setUpRemoteView(mContentViewBig!!)
            }
            updateRemoteViews(mContentViewBig!!)
            return mContentViewBig!!
        }

    inner class LocalBinder : Binder() {
        val service: PlaybackService
            get() = this@PlaybackService
    }

    override fun onCreate() {
        super.onCreate()
        mPlayer = Player.instance
        mPlayer!!.registerCallback(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val action = intent.action
            if (ACTION_PLAY_TOGGLE == action) {
                if (isPlaying) {
                    pause()
                } else {
                    play()
                }
            } else if (ACTION_PLAY_NEXT == action) {
                playNext()
            } else if (ACTION_PLAY_LAST == action) {
                playLast()
            } else if (ACTION_STOP_SERVICE == action) {
                if (isPlaying) {
                    pause()
                }
                stopForeground(true)
                unregisterCallback(this)
            }
        }
        return Service.START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    override fun stopService(name: Intent): Boolean {
        stopForeground(true)
        unregisterCallback(this)
        return super.stopService(name)
    }

    override fun onDestroy() {
        stopForeground(true)
        unregisterCallback(this)
        releasePlayer()
        super.onDestroy()
    }

    override fun setPlayList(list: PlayList) {
        mPlayer!!.setPlayList(list)
    }

    override fun play(): Boolean {
        return mPlayer!!.play()
    }

    override fun play(list: PlayList): Boolean {
        return mPlayer!!.play(list)
    }

    override fun play(list: PlayList, startIndex: Int): Boolean {
        return mPlayer!!.play(list, startIndex)
    }

    override fun play(song: Song): Boolean {
        return mPlayer!!.play(song)
    }

    override fun playLast(): Boolean {
        return mPlayer!!.playLast()
    }

    override fun playNext(): Boolean {
        return mPlayer!!.playNext()
    }

    override fun pause(): Boolean {
        return mPlayer!!.pause()
    }

    override fun seekTo(progress: Int): Boolean {
        return mPlayer!!.seekTo(progress)
    }

    override fun setPlayMode(playMode: PlayMode) {
        mPlayer!!.setPlayMode(playMode)
    }

    override fun registerCallback(callback: IPlayback.Callback) {
        mPlayer!!.registerCallback(callback)
    }

    override fun unregisterCallback(callback: IPlayback.Callback) {
        mPlayer!!.unregisterCallback(callback)
    }

    override fun removeCallbacks() {
        mPlayer!!.removeCallbacks()
    }

    override fun releasePlayer() {
        mPlayer!!.releasePlayer()
        super.onDestroy()
    }

    // Playback Callbacks

    override fun onSwitchLast(last: Song?) {
        showNotification()
    }

    override fun onSwitchNext(next: Song?) {
        showNotification()
    }

    override fun onComplete(next: Song?) {
        showNotification()
    }

    override fun onPlayStatusChanged(isPlaying: Boolean) {
        showNotification()

    }

    // Notification

    /**
     * Show a notification while this service is running.
     */
    private fun showNotification() {
        // The PendingIntent to launch our activity if the user selects this notification
        val contentIntent = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), 0)

        // Set the info for the views that show in the notification panel.
        val notification = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification_app_logo)  // the status icon
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .setCustomContentView(smallContentView)
                .setCustomBigContentView(bigContentView)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true)
                .build()

        // Send the notification.
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun setUpRemoteView(remoteView: RemoteViews) {
        remoteView.setImageViewResource(R.id.image_view_close, R.drawable.ic_remote_view_close)
        remoteView.setImageViewResource(R.id.image_view_play_last, R.drawable.ic_remote_view_play_last)
        remoteView.setImageViewResource(R.id.image_view_play_next, R.drawable.ic_remote_view_play_next)

        remoteView.setOnClickPendingIntent(R.id.button_close, getPendingIntent(ACTION_STOP_SERVICE))
        remoteView.setOnClickPendingIntent(R.id.button_play_last, getPendingIntent(ACTION_PLAY_LAST))
        remoteView.setOnClickPendingIntent(R.id.button_play_next, getPendingIntent(ACTION_PLAY_NEXT))
        remoteView.setOnClickPendingIntent(R.id.button_play_toggle, getPendingIntent(ACTION_PLAY_TOGGLE))
    }

    private fun updateRemoteViews(remoteView: RemoteViews) {
        val currentSong = mPlayer!!.playingSong
        if (currentSong != null) {
            remoteView.setTextViewText(R.id.text_view_name, currentSong!!.displayName)
            remoteView.setTextViewText(R.id.text_view_artist, currentSong!!.artist)
        }
        remoteView.setImageViewResource(R.id.image_view_play_toggle, if (isPlaying)
            R.drawable.ic_remote_view_pause
        else
            R.drawable.ic_remote_view_play)
//        val metadataRetriever = MediaMetadataRetriever()
//        try {
//            var imageView = applicationCo
//            metadataRetriever.setDataSource(file.absolutePath)
//            val albumData = metadataRetriever.embeddedPicture
//            Glide.with(applicationContext).load(albumData)
//                    .placeholder(com.musicplayer.aow.R.drawable.ic_music_player)
//                    .error(com.musicplayer.aow.R.drawable.ic_music_player)
//                    .into()
//        }catch (e: IllegalArgumentException){}
        val album = AlbumUtils.parseAlbum(playingSong!!)
        if (album == null) {
            remoteView.setImageViewResource(R.drawable.vinyl_blue, R.drawable.vinyl_blue)
        } else {
            remoteView.setImageViewBitmap(R.drawable.vinyl_blue, album)
        }
    }

    // PendingIntent
    private fun getPendingIntent(action: String): PendingIntent {
        return PendingIntent.getService(this, 0, Intent(action), 0)
    }

    companion object {

        private val ACTION_PLAY_TOGGLE = "com.musicplayer.aow.ACTION.PLAY_TOGGLE"
        private val ACTION_PLAY_LAST = "com.musicplayer.aow.ACTION.PLAY_LAST"
        private val ACTION_PLAY_NEXT = "com.musicplayer.aow.ACTION.PLAY_NEXT"
        private val ACTION_STOP_SERVICE = "com.musicplayer.aow.ACTION.STOP_SERVICE"

        private val NOTIFICATION_ID = 1
    }
}

