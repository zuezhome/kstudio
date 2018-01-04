package com.musicplayer.aow.splashscreen

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import com.musicplayer.aow.R
import com.musicplayer.aow.data.model.TempSongs
import com.musicplayer.aow.ui.main.MainActivity
import com.musicplayer.aow.utils.ApplicationSettings
import com.musicplayer.aow.utils.Settings
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete

import java.util.*


/**
 * Created by Arca on 10/2/2017.
 */

class SplashScreen : Activity() {

    var appname:TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //Application Settings
        doAsync {
            Settings.instance!!.intialization()
            TempSongs.instance!!.setSongs()
        }
//        ApplicationSettings.instance!!.intialization(this)

//        var anim = AnimationUtils.loadAnimation(this, R.anim.translate)
//        anim.reset()
//        appname = find(R.id.application_name)
//        appname!!.clearAnimation()
//        appname!!.startAnimation(anim)


        startNextActivity()

    }

    private fun startNextActivity()
    {
        Handler().postDelayed(
        {
            val i = Intent(this@SplashScreen, MainActivity::class.java)
            startActivity(i)
            finish()
        }, timeoutMillis.toLong())
    }


    private fun requiredPermissionsStillNeeded(): Array<String> {
        val permissionStrings: Array<String> = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.INTERNET,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.VIBRATE,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH_PRIVILEGED)
        val permissions = HashSet<String>()
        permissionStrings.forEach { e ->
            if (checkSelfPermission(e) == PackageManager.PERMISSION_GRANTED) {
            } else {
                permissions.add(e)
            }
        }
        return permissions.toTypedArray();
    }



    companion object {
        // Splash screen timer
        private var timeoutMillis = 5000;
    }
}
