package com.musicplayer.aow.utils.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.musicplayer.aow.player.PlaybackService

/**
 * Created by Arca on 11/29/2017.
 */
class AudioNoisey: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
            // signal your service to stop playback
            // (via an Intent, for instance)
            var service = Intent(context, PlaybackService::class.java);
//            service.putExtra(PlaybackService.KEY_ACTION_PLAY, PlaybackService.ACTION_STOP);
//            context.startService(service);
        }

    }

}