package com.musicplayer.aow.utils.receiver

import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import com.musicplayer.aow.player.Player


/**
 * Created by Arca on 11/29/2017.
 */
class CallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, arg1: Intent) {
        // TODO Auto-generated method stub
        val phoneListener = MyPhoneStateListener()
        val telephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE)
    }

    inner class MyPhoneStateListener : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, incomingNumber: String) {
            when (state) {
                TelephonyManager.CALL_STATE_IDLE -> try {
                    if (!mPlayer!!.isPlaying && wasPlaying) {
                        mPlayer!!.play()
                        wasPlaying = false
                    }
                } catch (e: Exception) {
                }

                TelephonyManager.CALL_STATE_OFFHOOK -> {
                }
                TelephonyManager.CALL_STATE_RINGING -> try {
                    if (mPlayer!!.isPlaying) {
                        mPlayer!!.pause()
                        wasPlaying = true
                    }
                } catch (e: Exception) {
                }

            }
        }
    }

    companion object {
        internal var mPlayer = Player.instance
        internal var wasPlaying = Player.instance!!.isPlaying
    }
}