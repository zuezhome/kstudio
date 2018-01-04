package com.musicplayer.aow.utils.receiver

import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import com.musicplayer.aow.player.Player


/**
 * Created by Arca on 12/3/2017.
 */
class HeadPhonePlug : BroadcastReceiver() {

    var mPlayer = Player.instance

    override fun onReceive(context: Context, intent: Intent) {

        if (intent != null) {
            val state = intent.getIntExtra("state", -1)
            when (state) {
                0 ->
                    //Headset unplug event.
                    mPlayer!!.pause()
                1 ->
                    //Headset plug-in event.
                    mPlayer!!.play()
            }//No idea what just happened.

        }

    }

}