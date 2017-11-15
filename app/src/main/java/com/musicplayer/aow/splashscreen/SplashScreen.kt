package com.musicplayer.aow.splashscreen

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator

import com.musicplayer.aow.R

import com.musicplayer.aow.ui.main.MainActivity

import java.io.IOException

/**
 * Created by Arca on 10/2/2017.
 */

class SplashScreen : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed(/*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            {
                // This method will be executed once the timer is over
                // Start your app main activity
                val i = Intent(this@SplashScreen, MainActivity::class.java)
                startActivity(i)
                // close this activity
                finish()
            }, SPLASH_TIME_OUT.toLong())
        }

    companion object {
        // Splash screen timer
        private val SPLASH_TIME_OUT = 5000
    }
}
