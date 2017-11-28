package com.musicplayer.aow.utils

import android.app.Activity
import android.app.Application
import android.media.MediaMetadataRetriever
import android.os.Environment
import android.os.SystemClock
import com.github.nisrulz.sensey.FlipDetector
import com.github.nisrulz.sensey.Sensey
import com.github.nisrulz.sensey.ShakeDetector
import com.musicplayer.aow.data.model.Song
import com.musicplayer.aow.player.Player
import java.io.File
import java.util.*


/**
 * Created by Arca on 11/23/2017.
 */
class Settings (): Application() {

    var context: Activity? = null
    var mPlayer: Player? = Player.instance
    var flipGesture = flipListener()
    var shakeGesture = shakeListner()
    val shakeaction = "shake"
    val flipaction = "flip"

    fun intialization(context: Activity){
        this.context = context
        var getSettings: StorageUtil? = StorageUtil(this.context!!)
        //Shake Gesture settings
        var shakeSettings = getSettings!!.loadStringValue(shakeaction)
        Sensey.getInstance().init(this.context);
        if (shakeSettings.equals("on")){
            getSettings!!.saveStringValue(shakeaction, "on")
            Sensey.getInstance().startShakeDetection(shakeGesture);
        }else{
            getSettings!!.saveStringValue(shakeaction, "off")
            Sensey.getInstance().stopShakeDetection(shakeGesture);
        }
        //Flip gesture settings
        var flipSettings = getSettings!!.loadStringValue(shakeaction)
        Sensey.getInstance().init(this.context);
        if (flipSettings.equals("on")){
            getSettings!!.saveStringValue(flipaction, "on")
            Sensey.getInstance().startFlipDetection(flipGesture);
        }else{
            getSettings!!.saveStringValue(flipaction, "off")
            Sensey.getInstance().stopFlipDetection(flipGesture);
        }
    }

    fun shakeListner(): ShakeDetector.ShakeListener {
        //Player instance
        val shakeListener = object : ShakeDetector.ShakeListener {
            override fun onShakeDetected() {
                if (mPlayer != null) {
                    if (mPlayer!!.isPlaying) {
                        mPlayer!!.playNext()
                    }
                }
            }
            override fun onShakeStopped() {
                // Shake stopped, do something
            }
        }
        return shakeListener
    }

    fun flipListener(): FlipDetector.FlipListener {
        //Sensey Flip Gesture
        val flipListener = object : FlipDetector.FlipListener {
            override fun onFaceUp() {
                if (mPlayer != null) {
                    if (!mPlayer!!.isPlaying) {
                        mPlayer!!.play()
                    }
                }
            }
            override fun onFaceDown() {
                if (mPlayer != null) {
                    if (mPlayer!!.isPlaying) {
                        mPlayer!!.pause()
                    }
                }
            }
        }
        return flipListener
    }

    fun searchSongs(context: Activity): Boolean {
        var songModelData: ArrayList<Song> = ArrayList()
        //context or activity
        var songCursor = CursorDB().callCursor(context)
        if (songCursor != null) {
            var indexPosition = 0
            //clear albumart cache
            var applicationFoldercontext = "com.musicplayer.aow"
            val dir = File(Environment.getExternalStorageDirectory(), applicationFoldercontext + "/cache/img")
            dir.delete()
            while (songCursor != null && songCursor!!.moveToNext()) {
                indexPosition = indexPosition + 1
                songModelData.add(CursorDB().cursorToMusic(context, songCursor!!, indexPosition))

                //save album art to app folder
                val metadataRetriever = MediaMetadataRetriever()
                metadataRetriever.setDataSource(songModelData[indexPosition - 1].path)
                val albumData = metadataRetriever.embeddedPicture
                if(albumData != null){
                    songModelData[indexPosition - 1].albumArt = StorageUtil(context).byteArrayToFile(albumData.clone(), SystemClock.currentThreadTimeMillis().toString())
                }
            }
            songCursor!!.close()
        }else{
            //
        }

        return StorageUtil(context).storeAudio(songModelData)
    }

    fun readSongs(context: Activity): ArrayList<Song>?{
        var getSettings: StorageUtil? = StorageUtil(this.context!!)
        var getAudios = getSettings!!.loadAudio()
        return getAudios
    }


    companion object {

        private val TAG = "Setings"

        @Volatile private var sInstance: Settings? = null

        val instance: Settings?
            get() {
                if (sInstance == null) {
                    synchronized(Settings.Companion) {
                        if (sInstance == null) {
                            sInstance = Settings()
                        }
                    }
                }
                return sInstance
            }
    }

}