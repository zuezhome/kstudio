package com.musicplayer.aow.ui.scheduler

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.DialogFragment
import android.widget.TimePicker

@SuppressLint("ValidFragment")
class MyDialogFragment(private val handler: Handler) : DialogFragment() {
    private var timeHour: Int = 0
    private var timeMinute: Int = 0
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bundle = arguments
        timeHour = bundle.getInt(MyConstants.HOUR)
        timeMinute = bundle.getInt(MyConstants.MINUTE)
        val listener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            timeHour = hourOfDay
            timeMinute = minute
            val b = Bundle()
            b.putInt(MyConstants.HOUR, timeHour)
            b.putInt(MyConstants.MINUTE, timeMinute)
            val msg = Message()
            msg.data = b
            handler.sendMessage(msg)
        }
        return TimePickerDialog(activity, listener, timeHour, timeMinute, false)
    }
}