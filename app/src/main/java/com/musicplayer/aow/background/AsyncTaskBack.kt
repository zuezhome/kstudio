package com.musicplayer.aow.background

import android.os.AsyncTask

/**
 * Created by Arca on 12/14/2017.
 */
//AsyncTaskExample().execute()
class AsyncTaskBack : AsyncTask<String, String, String>() {

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg p0: String?): String {

        return ""
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)

    }
}