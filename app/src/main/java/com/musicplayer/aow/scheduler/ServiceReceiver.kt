package com.musicplayer.aow.scheduler

import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context


/**
 * Created by Arca on 12/4/2017.
 */
class ServiceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        UtilServes().scheduleJob(context)
    }
}