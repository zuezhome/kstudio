package com.musicplayer.aow.ui.scheduler

import android.app.AlarmManager
import android.os.Bundle
import android.R.id.button2
import android.R.id.button1
import java.util.Calendar;
import android.app.PendingIntent;
import android.content.Context
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.musicplayer.aow.R


/**
 * Created by Arca on 12/8/2017.
 */
class SchedulerActivity : FragmentActivity() {
    internal var textView1: TextView? = null
    internal var alarmManager: AlarmManager? = null
    private var pendingIntent: PendingIntent? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_scheduler)
        textView1 = findViewById(R.id.msg1) as TextView
        textView1!!.text = ""+ timeHour + ":" + timeMinute
        textView2 = findViewById(R.id.msg2) as TextView

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val myIntent = Intent(this, AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0)

        val listener1 = object : View.OnClickListener {
            override fun onClick(view: View) {
                textView2!!.text = ""
                val bundle = Bundle()
                bundle.putInt(MyConstants.HOUR, timeHour)
                bundle.putInt(MyConstants.MINUTE, timeMinute)
                val fragment = MyDialogFragment(MyHandler())
                fragment.setArguments(bundle)
                val manager = supportFragmentManager
                val transaction = manager.beginTransaction()
                transaction.add(fragment, MyConstants.TIME_PICKER)
                transaction.commit()
            }
        }

        val btn1 = findViewById(R.id.button1) as Button
        btn1.setOnClickListener(listener1)
        val listener2 = object : View.OnClickListener {
            override fun onClick(view: View) {
                textView2!!.text = ""
                cancelAlarm()
            }
        }
        val btn2 = findViewById(R.id.button2) as Button
        btn2.setOnClickListener(listener2)
    }

    internal inner class MyHandler : Handler() {
        override fun handleMessage(msg: Message) {
            val bundle = msg.getData()
            timeHour = bundle.getInt(MyConstants.HOUR)
            timeMinute = bundle.getInt(MyConstants.MINUTE)
            textView1!!.text = ""+ timeHour + ":" + timeMinute
            setAlarm()
        }
    }

    private fun setAlarm() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, timeHour)
        calendar.set(Calendar.MINUTE, timeMinute)
        alarmManager!!.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent)
    }

    private fun cancelAlarm() {
        if (alarmManager != null) {
            alarmManager!!.cancel(pendingIntent)
        }
    }

    companion object {
        private var timeHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        private var timeMinute = Calendar.getInstance().get(Calendar.MINUTE)
        var textView2: TextView? = null
            private set

        fun newIntent(context: Context): Intent {
            val intent = Intent(context, SchedulerActivity::class.java)
            return intent
        }
    }
}