package com.musicplayer.aow.ui.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import butterknife.ButterKnife
import com.github.nisrulz.sensey.Sensey
import com.musicplayer.aow.R
import com.musicplayer.aow.ui.base.BaseFragment
import com.musicplayer.aow.utils.ApplicationSettings
import com.musicplayer.aow.utils.StorageUtil
import com.musicplayer.aow.searchaudio.SearchAudio
import android.support.design.widget.Snackbar
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.musicplayer.aow.sharedata.bluetooth.BluetoothActivity
import com.musicplayer.aow.ui.scheduler.SchedulerActivity
import com.musicplayer.aow.utils.sharefile.wifihotspot.WifiHotSpotManager


class SettingsFragment : BaseFragment() {

    var appSettings = ApplicationSettings.instance
    private var shakeSwitchBtn: Switch? = null
    private var flipSwitchBtn: Switch? = null
    private var searchBtn: Button? = null
    private var wifiBtn: Button? = null
    private var scheduler: Button? = null
    private var bluetooth: Button? = null

    //Google Admob
    lateinit var mAdView : AdView

    var wifiHotSpotManager: WifiHotSpotManager? = null;
    var pos = 1

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view!!)

        flipSwitchBtn = view.findViewById(R.id.flip_btn) as Switch
        shakeSwitchBtn = view.findViewById(R.id.shake_btn) as Switch
        shakeSettings(context as Activity, shakeSwitchBtn!!)
        flipSettings(context as Activity, flipSwitchBtn!!)

        searchBtn = view.findViewById(R.id.search_audio) as Button
        searchBtn!!.setOnClickListener {
            val intent = SearchAudio.newIntent(context)
            startActivity(intent)
        }

        //Google Admob
        mAdView = view.findViewById(R.id.adView) as AdView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        //wifi connection
        wifiHotSpotManager= WifiHotSpotManager(context)
        wifiBtn = view.findViewById(R.id.wifi_activity) as Button
        wifiBtn!!.setOnClickListener {
            //Settings change permission
            settingsPermission(view)
        }

        //scheduler
        scheduler = view.findViewById(R.id.scheduler_activity) as Button
        scheduler!!.setOnClickListener {
            val intent = SchedulerActivity.newIntent(context)
            startActivity(intent)
        }

        //bluetooth
        bluetooth = view.findViewById(R.id.bluetooth_activity) as Button
        bluetooth!!.setOnClickListener {
            val intent = Intent(context,BluetoothActivity::class.java)
            startActivity(intent)
        }
    }

    fun settingsPermission(view: View?){
        if (!Settings.System.canWrite(context)) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + context.getPackageName()))
            startActivityForResult(intent, 200)
        }else{
            when (pos) {
                1 -> {
                    var wifiSet = wifiHotSpotManager?.setWifiApEnabled(null, true)
                    if (wifiSet == true){
                        Snackbar.make(view!!, "Wifi : {ip: "+ wifiHotSpotManager?.getIpAddr() + " }", Snackbar.LENGTH_LONG).show()
                    }
                }
                2 -> {
                    var wifiUnSet = wifiHotSpotManager?.setWifiApEnabled(null, false)
                    if (wifiUnSet == true){
                        Snackbar.make(view!!, "Wifi : Off ", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
            if (pos == 1){
                pos = 2
            }else{
                pos = 1
            }
        }
    }

    fun shakeSettings(activity: Activity,switch: Switch){
        val action = appSettings!!.shakeaction
        var save: StorageUtil? = StorageUtil(activity)
        var shakeSettingsState = save!!.loadStringValue(action)
        if (shakeSettingsState!!.equals("on")){
            switch.isChecked = true
        }
        //implement on checked on each radio button
        switch.setOnCheckedChangeListener{ buttonView, isChecked ->
            if (isChecked) {
                save!!.saveStringValue(action, "on")
                var shakeSettings = save!!.loadStringValue(action)
                Log.e("Shake", shakeSettings)
                Sensey.getInstance().startShakeDetection(appSettings!!.shakeGesture);
            }else {
                save!!.saveStringValue(action, "off")
                var shakeSettings = save!!.loadStringValue(action)
                Log.e("Shake", shakeSettings)
                Sensey.getInstance().stopShakeDetection(appSettings!!.shakeGesture);
            }
        }
    }

    fun flipSettings(activity: Activity,switch: Switch){
        val action = appSettings!!.flipaction
        var save: StorageUtil? = StorageUtil(activity)
        var shakeSettingsState = save!!.loadStringValue(action)
        if (shakeSettingsState!!.equals("on")){
            switch.isChecked = true
        }
        //implement on checked on each radio button
        switch.setOnCheckedChangeListener{ buttonView, isChecked ->
            if (isChecked) {
                save!!.saveStringValue(action, "on")
                Sensey.getInstance().startFlipDetection(appSettings!!.flipGesture);
            }else {
                save!!.saveStringValue(action, "off")
                Sensey.getInstance().stopFlipDetection(appSettings!!.flipGesture);
            }
        }
    }

    override fun onPause() {
        super.onPause()
    }


    companion object {

    }

}