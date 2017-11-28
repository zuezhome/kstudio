package com.musicplayer.aow.ui.settings

import android.app.Activity
import android.os.Bundle
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
import com.musicplayer.aow.utils.Settings
import com.musicplayer.aow.utils.StorageUtil
import com.musicplayer.aow.searchaudio.SearchAudio


class SettingsFragment : BaseFragment() {

    var appSettings = Settings.instance
    private var shakeSwitchBtn: Switch? = null
    private var flipSwitchBtn: Switch? = null
    private var searchBtn: Button? = null

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