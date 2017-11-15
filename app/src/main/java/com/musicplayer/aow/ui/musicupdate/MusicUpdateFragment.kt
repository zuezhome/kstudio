package com.musicplayer.aow.ui.musicupdate

import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast

import butterknife.ButterKnife

import com.musicplayer.aow.R
import com.musicplayer.aow.ui.base.BaseFragment

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*

import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.ArrayList


class MusicUpdateFragment : BaseFragment(), View.OnClickListener {

    private val playPause = true
    private val progressDialog: ProgressDialog? = null
    private var refresh: Button? = null
    private var mRVFishPrice: RecyclerView? = null
    private var mAdapter: AdapterUpdateStream? = null

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view!!)
        refresh = view.findViewById(R.id.refresh) as Button

        //Make call to AsyncTask
        AsyncFetch().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        // Setup and Handover data to recyclerview
        mRVFishPrice = view.findViewById(R.id.fishPriceList) as RecyclerView

        refresh!!.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        //Make call to AsyncTask
        AsyncFetch().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }


    override fun onPause() {
        super.onPause()
    }


    internal inner class AsyncFetch : AsyncTask<String, String, String>() {

        private var mediaPlayer: com.musicplayer.aow.player.Player? = null

        var pdLoading = ProgressDialog(activity)
        lateinit var conn: HttpURLConnection
        var url: URL? = null

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String): String {
            try {

                // Enter URL address where your json file resides
                // Even you can make call to php file which returns json data
                url = URL("http://zuezhome.com/AOWPLAYER/music_today.php")

            } catch (e: MalformedURLException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
                return e.toString()
            }

            try {

                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = url!!.openConnection() as HttpURLConnection
                conn.readTimeout = READ_TIMEOUT
                conn.connectTimeout = CONNECTION_TIMEOUT
                conn.requestMethod = "GET"

                // setDoOutput to true as we recieve data from json file
                conn.doOutput = true

            } catch (e1: IOException) {
                // TODO Auto-generated catch block
                e1.printStackTrace()
                return e1.toString()
            }

            try {

                val response_code = conn.responseCode
                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {
                    // Read data sent from server
                    val input = conn.inputStream
                    val reader = BufferedReader(InputStreamReader(input) as Reader?)
                    val result = StringBuilder()
                    var line: String
                    line = reader.readLine()
                    while ((line) != null) {
//                        result.append(line)
                    }
                    // Pass data to onPostExecute method
                    return result.toString()
                } else {
                    return "unsuccessful"
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return e.toString()
            } finally {
                conn.disconnect()
            }
        }

        override fun onPostExecute(result: String) {
            //this method will be running on UI thread
            pdLoading.dismiss()
            val data = ArrayList<DataFish>()
            pdLoading.dismiss()
            try {
                val jArray = JSONArray(result)
                // Extract data from json and store into ArrayList as class objects
                for (i in 0 until jArray.length()) {
                    val json_data = jArray.getJSONObject(i)
                    val fishData = DataFish()
                    fishData.fishImage = json_data.getString("fish_img")
                    fishData.fishName = json_data.getString("fish_name")
                    data.add(fishData)
                }
                //init mediaPlayer
                mediaPlayer = com.musicplayer.aow.player.Player()
                mediaPlayer!!.setStreamType()
                mAdapter = AdapterUpdateStream(activity, data, mediaPlayer!!)
                mRVFishPrice!!.adapter = mAdapter
                mRVFishPrice!!.layoutManager = GridLayoutManager(activity, 2)
                //                mRVFishPrice.setLayoutManager(new LinearLayoutManager(getActivity()));

            } catch (e: JSONException) {
                Toast.makeText(activity, "loading error, pleas try again.", Toast.LENGTH_LONG).show()
            }

        }

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_music_update, container, false)
    }

    companion object {


        //For ReCycler com.musicpalyer.com.musicplayer.aow list server
        // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
        val CONNECTION_TIMEOUT = 10000
        val READ_TIMEOUT = 15000
    }
}
