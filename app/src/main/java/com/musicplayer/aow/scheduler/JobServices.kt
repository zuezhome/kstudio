package com.musicplayer.aow.scheduler

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import com.musicplayer.aow.splashscreen.SplashScreen


/**
 * Created by Arca on 12/4/2017.
 */
class JobServices : JobService() {

    override fun onStartJob(params: JobParameters): Boolean {
        val service = Intent(apply {  }, SplashScreen::class.java)
        getApplicationContext().startService(service)
        UtilServes().scheduleJob(this) // reschedule the job
        return true
    }

    override fun onStopJob(params: JobParameters): Boolean {
        return true
    }

    companion object {
        private val TAG = "SyncService"
    }

}