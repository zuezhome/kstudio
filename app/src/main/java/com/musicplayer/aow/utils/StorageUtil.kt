package com.musicplayer.aow.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.musicplayer.aow.data.model.Song
import java.util.*
import android.util.Log
import java.io.*
import android.graphics.BitmapFactory
import android.graphics.Bitmap




class StorageUtil(context: Context) {

    var applicationFoldercontext = "com.musicplayer.aow"

    var context = context

    private val STORAGE = "com.musicplayer.aow.STORAGE"
    private var preferences: SharedPreferences? = null



    fun storeAudio(arrayList: ArrayList<Song>): Boolean {
        preferences = this.context.getSharedPreferences(STORAGE,Context.MODE_PRIVATE);
        val editor = preferences!!.edit()
        val gson = Gson()
        val json = gson.toJson(arrayList)
        editor.putString("songs", json)
        editor.apply()
        return true
    }

    fun loadAudio(): ArrayList<Song>? {
        preferences = this.context.getSharedPreferences(STORAGE,Context.MODE_PRIVATE);
        val gson = Gson()
        val json = preferences!!.getString("songs", null)
        val type = object : TypeToken<ArrayList<Song>>() {

        }.type
        if (json.isNullOrEmpty()) {
            return ArrayList()
        }else {
            return gson.fromJson<ArrayList<Song>>(json, type)
        }
    }

    fun storeAudioIndex(index: Int) {
        preferences = this.context.getSharedPreferences(STORAGE,Context.MODE_PRIVATE);
        val editor = preferences!!.edit()
        editor.putInt("audioIndex", index)
        editor.apply()
    }

    fun loadAudioIndex(): Int {
        preferences = this.context.getSharedPreferences(STORAGE,Context.MODE_PRIVATE);
        if (preferences != null) {
            return preferences!!.getInt("audioIndex", -1)//return -1 if no data found
        }else{
            return 0
        }
    }

    fun clearCachedAudioPlaylist() {
        preferences = this.context.getSharedPreferences(STORAGE,Context.MODE_PRIVATE);
        val editor = preferences!!.edit()
        editor.clear()
        editor.commit()
    }

    fun saveStringValue(name: String, value: String){
        preferences = this.context.getSharedPreferences(STORAGE,Context.MODE_PRIVATE);
        val editor = preferences!!.edit()
        editor.putString(name, value)
        editor.apply()
    }

    fun loadStringValue(name: String): String? {
        preferences = this.context.getSharedPreferences(STORAGE,Context.MODE_PRIVATE);
        return preferences!!.getString(name, "empty")
    }

    fun storageLocationDir(){
        val mediaStorageDir = File(Environment.getExternalStorageDirectory(), applicationFoldercontext)
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e("App", "failed to create directory")
            }else{
                var folderNames = arrayListOf("/tmpImg", "/update", "/cache/img", "/.media", "/.nomedia",
                        "/download/lyrics", "/download/audio", "/download/albumart","/items")
                folderNames
                        .asSequence()
                        .map { File(Environment.getExternalStorageDirectory(), applicationFoldercontext + it) }
                        .filter { !it.exists() && !it.mkdirs() }
                        .forEach { Log.e("App", "failed to create directory") }
            }
        }
    }

    fun byteArrayToFile(byteArray: ByteArray, name: String): String? {
        val file = File(Environment.getExternalStorageDirectory(), applicationFoldercontext + "/cache/img/" + name)
        if (!file.exists()) {
            file.createNewFile();
        }
        val fos = FileOutputStream(file)
        fos.write(byteArray)
        fos.close()
        return file.absolutePath
    }

    // Decodes image and scales it to reduce memory consumption
    fun decodeFile(f: File): Bitmap? {
        try {
            // Decode image size
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            BitmapFactory.decodeStream(FileInputStream(f), null, o)

            // The new size we want to scale to
            val REQUIRED_SIZE = 70

            // Find the correct scale value. It should be the power of 2.
            var scale = 1
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2
            }

            // Decode with inSampleSize
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            return BitmapFactory.decodeStream(FileInputStream(f), null, o2)
        } catch (e: FileNotFoundException) {
        }

        return null
    }
}
