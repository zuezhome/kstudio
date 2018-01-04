package com.musicplayer.aow.utils.killactivity

import android.app.Activity

/**
 * Created by Arca on 11/29/2017.
 */
class ActivityStack{
    var mActivity: Activity? = null

    fun setMainActivity(activity: Activity){
        if (mActivity == null) {
            mActivity = activity
        }
    }

    fun getMainActivity(): Activity? {
        return mActivity
    }

    companion object {

        private val TAG = "ActivityStack"

        @Volatile private var sInstance: ActivityStack? = null

        val instance: ActivityStack?
            get() {
                if (sInstance == null) {
                    synchronized(ActivityStack.Companion) {
                        if (sInstance == null) {
                            sInstance = ActivityStack()
                        }
                    }
                }
                return sInstance
            }
    }
}