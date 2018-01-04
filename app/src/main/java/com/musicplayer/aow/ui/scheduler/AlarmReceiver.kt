package com.musicplayer.aow.ui.scheduler

import android.content.Context
import android.media.RingtoneManager
import android.media.Ringtone
import android.content.Intent
import android.support.v4.content.WakefulBroadcastReceiver
import com.musicplayer.aow.RxBus
import com.musicplayer.aow.data.model.PlayList
import com.musicplayer.aow.event.PlayListNowEvent
import com.musicplayer.aow.utils.ApplicationSettings


/**
 * Created by Arca on 12/8/2017.
 */
class AlarmReceiver : WakefulBroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        var songModelData = ApplicationSettings.instance!!.readSongs(context)!!
        if (songModelData.isEmpty()){
            SchedulerActivity.textView2!!.setText("No Audio Found!")
        }else
        {
            SchedulerActivity.textView2!!.setText("Playing Audio now!")
            //sort the song list in ascending order
            var songsList = songModelData.sortedWith(compareBy({ (it.title)!!.toLowerCase() }))
            RxBus.instance!!.post(PlayListNowEvent(PlayList(songsList),0))
        }

        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val ringtone = RingtoneManager.getRingtone(context, uri)
        ringtone.play()
    }
}