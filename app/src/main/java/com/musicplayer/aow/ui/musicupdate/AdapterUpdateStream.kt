package com.musicplayer.aow.ui.musicupdate


import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Environment
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.bumptech.glide.Glide

import java.io.BufferedInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.net.URLConnection
import java.util.Collections

import com.musicplayer.aow.R
import com.musicplayer.aow.player.Player

/**
 * Created by Arca on 10/7/2017.
 */

class AdapterUpdateStream// create constructor to innitilize context and data sent from MainActivity
(private val context: Context, data: List<DataFish>, private val mediaPlayer: Player) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val inflater: LayoutInflater
    internal var data = emptyList<DataFish>()
    internal var current: DataFish? = null
    internal var currentPos = 0
    internal var downloadFileFromURL: DownloadFileFromURL? = null

    init {
        inflater = LayoutInflater.from(context)
        this.data = data
    }

    // Inflate the layout when viewholder created
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = inflater.inflate(com.musicplayer.aow.R.layout.container_fish, parent, false)
        return MyHolder(view, mediaPlayer)
    }

    // Bind data
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // Get current position of item in recyclerview to bind data and assign values from list
        val myHolder = holder as MyHolder
        val current = data.get(position)
        myHolder.textFishName.setText(current.fishName)
        // load image into image view using glide
        Glide.with(context).load("http://zuezhome.com/test/images/" + current.fishImage!!)
                .placeholder(com.musicplayer.aow.R.drawable.musicians_album)
                .error(com.musicplayer.aow.R.drawable.musicians_album)
                .into(myHolder.ivFish)

    }

    // return total item from List
    override fun getItemCount(): Int {
        return data.size
    }


    inner class MyHolder// create constructor to get widget reference
    (itemView: View, //streaming
     private var mediaPlayer: Player?) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private var playing = false
        private val progressDialog: ProgressDialog
        private var initialStage = true
        private val prepared = false
        private val download: Button
        internal var textFishName: TextView
        internal var ivFish: ImageView

        init {
            itemView.setOnClickListener(this)
            textFishName = itemView.findViewById(com.musicplayer.aow.R.id.textFishName) as TextView
            ivFish = itemView.findViewById(com.musicplayer.aow.R.id.ivFish) as ImageView

            //streaming
            progressDialog = ProgressDialog(itemView.context)
            initMediaPlayer()
            //this.mediaPlayer.setStreamType();

            //download button
            download = itemView.findViewById(R.id.left) as Button
            download.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    DownloadFileFromURL().executeOnExecutor(
                            AsyncTask.THREAD_POOL_EXECUTOR, textFishName.text.toString())
                }
            })
        }

        fun initMediaPlayer() {
            if (mediaPlayer != null) {
                //
            }
            mediaPlayer = Player()
            mediaPlayer!!.setStreamType()
        }

        override fun onClick(view: View) {
            initMediaPlayer()
            Toast.makeText(view.context, textFishName.text, Toast.LENGTH_SHORT).show()
            try {
                val uri = URI("http://zuezhome.com/AOWPLAYER/" + textFishName.text)
                //new Player().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, uri.toString());
                Streamer().execute(uri.toString())
                initialStage = false
                playing = true
            } catch (e: URISyntaxException) {
                Toast.makeText(view.context, e.message, Toast.LENGTH_SHORT).show()
            }

        }
    }

    internal inner class Streamer : AsyncTask<String, Void, Boolean>() {
        public override fun doInBackground(vararg strings: String): Boolean? {
            try {
                mediaPlayer.setDataSource(strings as Array<String>)
                mediaPlayer.prepare()
                return true
            } catch (e: Exception) {
                return false
            }

        }

        override fun onPostExecute(aBoolean: Boolean?) {
            super.onPostExecute(aBoolean)
            //                if (progressDialog.isShowing()) {
            //                    progressDialog.cancel();
            //                }
            mediaPlayer.start()
            //                initialStage = false;
        }

        override fun onPreExecute() {
            super.onPreExecute()
            //                progressDialog.setMessage("ON AIR...");
            //                progressDialog.show();
        }

    }

    internal inner class DownloadFileFromURL : AsyncTask<String, String, String>() {

        /**
         * Before starting background thread
         */
        override fun onPreExecute() {
            super.onPreExecute()
            println("Starting download")
            //                progressDialog.setMessage("Loading... Please wait...");
            //                progressDialog.show();
        }

        /**
         * Downloading file in background thread
         */
        override fun doInBackground(vararg f_url: String): String? {
            var count: Int
            try {
                val root = Environment.getExternalStorageDirectory().toString()
                //                    progressDialog.setMessage("DownLoading... Please wait...");
                //                    progressDialog.show();
                val url = URL("http://zuezhome.com/AOWPLAYER/" + f_url[0])
                val conection = url.openConnection()
                conection.connect()
                // getting file length
                val lenghtOfFile = conection.contentLength
                //                    progressDialog.dismiss();
                // input stream to read file - with 8k buffer
                val input = BufferedInputStream(url.openStream(), 8192)
                // Output stream to write file
                val output = FileOutputStream(root + "/" + f_url[0].replace("music/", ""))
                val data = ByteArray(1024)
                var total: Long = 0
                count = input.read(data)
                while ((count) != -1) {
                    total += count.toLong()
                    // writing data to file
                    output.write(data, 0, count)
                }
                // flushing output
                output.flush()
                // closing streams
                output.close()
                input.close()
            } catch (e: Exception) {
                Log.e("Error: ", e.message)
                //                    progressDialog.setMessage("Error downLoading...");
                //                    progressDialog.show();
            }

            return null
        }

        /**
         * After completing background task
         */
        override fun onPostExecute(file_url: String) {
            //                progressDialog.setMessage("File has been downloaded.");
            //                progressDialog.show();
            //                progressDialog.dismiss();
        }

    }

}
