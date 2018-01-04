package com.musicplayer.aow.ui.music

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatImageView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.musicplayer.aow.R
import com.musicplayer.aow.RxBus
import com.musicplayer.aow.data.model.PlayList
import com.musicplayer.aow.data.model.Song
import com.musicplayer.aow.data.source.AppRepository
import com.musicplayer.aow.data.source.PreferenceManager
import com.musicplayer.aow.event.PlayAlbumNowEvent
import com.musicplayer.aow.event.PlayListNowEvent
import com.musicplayer.aow.event.PlaySongEvent
import com.musicplayer.aow.player.IPlayback
import com.musicplayer.aow.player.PlayMode
import com.musicplayer.aow.player.PlaybackService
import com.musicplayer.aow.player.Player
import com.musicplayer.aow.ui.base.BaseActivity
import com.musicplayer.aow.ui.base.BaseFragment
import com.musicplayer.aow.ui.widget.ShadowImageView
import com.musicplayer.aow.utils.CircleTransform
import com.musicplayer.aow.utils.TimeUtils
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import java.io.File


class MusicPlayerActivity : BaseActivity(), MusicPlayerContract.View, IPlayback.Callback, View.OnClickListener{

    private var imageViewAlbum: ShadowImageView? = null
    private var textViewName: TextView? = null
    private var textViewArtist: TextView? = null
    private var textViewProgress: TextView? = null
    private var textViewDuration: TextView? = null
    private var seekBarProgress: SeekBar? = null

    private var buttonPlayModeToggle: ImageView? = null
    private var buttonPlayToggle: ImageView? = null
    private var buttonFavoriteToggle: ImageView? = null
    private var buttonPlayNext: ImageView? = null
    private var buttonPlayPrev: ImageView? = null

    private var buttonEqualizer: AppCompatImageView? = null
    var playBackSpeed: SeekBar? = null

    private var mPlayer: IPlayback? = null

    private val mHandler = Handler()

    private var mPresenter: MusicPlayerContract.Presenter? = null

    private val mProgressCallback = object : Runnable {
        override fun run() {
//            if (isDetached) return

            if (mPlayer!!.isPlaying) {
                val progress = (seekBarProgress!!.max * (mPlayer!!.progress.toFloat() / currentSongDuration.toFloat())).toInt()
                updateProgressTextWithDuration(mPlayer!!.progress)
                if (progress >= 0 && progress <= seekBarProgress!!.max) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        seekBarProgress!!.setProgress(progress, true)
                    } else {
                        seekBarProgress!!.progress = progress
                    }
                    mHandler.postDelayed(this, UPDATE_PROGRESS_INTERVAL)
                }
            }
        }
    }

    private val currentSongDuration: Int
        get() {
            if (mPlayer!!.playingSong != null) {
                val currentSong = mPlayer!!.playingSong
                var duration = 0
                if (currentSong != null) {
                    duration = currentSong.duration
                }
                return duration
            }else{
                return 0
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ButterKnife.bind(this)
        setContentView(R.layout.fragment_music)

        textViewProgress = findViewById(R.id.text_view_progress) as TextView
        textViewName = findViewById(R.id.text_view_name) as TextView
        textViewArtist = findViewById(R.id.text_view_artist) as TextView
        textViewDuration = findViewById(R.id.text_view_duration) as TextView
        imageViewAlbum = findViewById(R.id.image_view_album) as ShadowImageView
        buttonPlayToggle = findViewById(R.id.button_play_toggle) as ImageView
        buttonFavoriteToggle = findViewById(R.id.button_favorite_toggle) as ImageView
        buttonPlayModeToggle = findViewById(R.id.button_play_mode_toggle) as ImageView
        buttonPlayNext = findViewById(R.id.button_play_next) as ImageView
        buttonPlayPrev = findViewById(R.id.button_play_last) as ImageView
        seekBarProgress = findViewById(R.id.seek_bar) as SeekBar
        buttonEqualizer = findViewById(R.id.android_equalizer) as AppCompatImageView
        playBackSpeed = findViewById(R.id.playback_SeekBar) as SeekBar

        textViewName!!.isSelected = true

        //Android inbuilt eq
        buttonEqualizer!!.setOnClickListener {
//            val intent = Intent()
//            intent.action = "android.media.action.DISPLAY_AUDIO_EFFECT_CONTROL_PANEL"
//            startActivityForResult(intent, 1)
        }

        //Play/Pause button
        buttonPlayToggle!!.setOnClickListener{
            onPlayToggleAction()
        }

        //Play Next button
        buttonPlayNext!!.setOnClickListener{
            onPlayNextAction()
        }

        //Play Next button
        buttonPlayPrev!!.setOnClickListener{
            onPlayLastAction()
        }

        //Favourite button
        buttonFavoriteToggle!!.setOnClickListener{
            onFavoriteToggleAction()
        }

        //PlayMode button
        buttonPlayModeToggle!!.setOnClickListener{
            onPlayModeToggleAction()
        }

        //PlayBack Speed
        playBackSpeed!!.max = 10
        var player = Player.instance
        var mPlayerBack = player!!.mPlayer
        var initSpeed = 0.5f
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mPlayerBack!!.isPlaying) {
                initSpeed = mPlayerBack!!.playbackParams.speed
                playBackSpeed!!.progress = (0.2f  * initSpeed).toInt()
            }else{
                playBackSpeed!!.progress = 5
            }
        }
        playBackSpeed!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var speed: Float = 1.0f

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                speed = getConvertedValue(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                mHandler.removeCallbacks(mProgressCallback)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                changeplayerSpeed(speed!!)
            }
        })

        seekBarProgress!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    updateProgressTextWithProgress(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                mHandler.removeCallbacks(mProgressCallback)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                seekTo(getDuration(seekBar.progress))
                if (mPlayer!!.isPlaying) {
                    mHandler.removeCallbacks(mProgressCallback)
                    mHandler.post(mProgressCallback)
                }
            }
        })

        MusicPlayerPresenter(this, AppRepository.instance!!, this).subscribe()
    }


    override fun onClick(v: View) {
        //nothing
    }


    override fun onStart() {
        super.onStart()
        if (mPlayer != null && mPlayer!!.isPlaying) {
            mHandler.removeCallbacks(mProgressCallback)
            mHandler.post(mProgressCallback)
        }
    }

    override fun onStop() {
        mHandler.removeCallbacks(mProgressCallback)
        super.onStop()
    }

    override fun onDestroy() {
        mPresenter!!.unsubscribe()
        super.onDestroy()
    }

    // Click Events
    fun onPlayToggleAction() {
        if (mPlayer == null) return

        if (mPlayer!!.isPlaying) {
            mPlayer!!.pause()
        } else {
            mPlayer!!.play()
        }
    }

    fun onPlayModeToggleAction() {
        if (mPlayer == null) return

        val current = PreferenceManager.lastPlayMode(this)
        val newMode = PlayMode.switchNextMode(current)
        PreferenceManager.setPlayMode(this, newMode)
        mPlayer?.setPlayMode(newMode)
        updatePlayMode(newMode)
    }


    private fun onPlayLastAction() {
        if (mPlayer == null) return

        mPlayer!!.playLast()
    }

    fun onPlayNextAction() {
        if (mPlayer == null) return

        mPlayer!!.playNext()
    }


    fun onFavoriteToggleAction() {
        if (mPlayer == null) return

        if (mPlayer!!.playingSong != null) {
            val currentSong = mPlayer!!.playingSong
            if (currentSong != null) {
//                view.isEnabled = false
                mPresenter!!.setSongAsFavorite(currentSong, !currentSong.isFavorite)
            }
        }
    }

    // RXBus Events
    override fun subscribeEvents(): Subscription {
        return RxBus.instance?.toObservable()
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.doOnNext({ o ->
                    if (o is PlaySongEvent) {
                        onPlaySongEvent(o)
                    } else if (o is PlayListNowEvent) {
                        onPlayListNowEvent(o)
                    } else if (o is PlayAlbumNowEvent) {
                        onPlayAlbumNowEvent(o)
                    }
                })?.subscribe(RxBus.defaultSubscriber())!!
    }

    private fun onPlaySongEvent(event: PlaySongEvent) {
        val song = event.song
        playSong(song)
    }

    private fun onPlayListNowEvent(event: PlayListNowEvent) {
        val playList = event.playList
        val playIndex = event.playIndex
        playSong(playList, playIndex)
    }

    private fun onPlayAlbumNowEvent(event: PlayAlbumNowEvent) {
        val playList = event.song
        playSong(playList)
    }

    // Music Controls
    private fun playSong(song: Song) {
        val playList = PlayList(song)
        playSong(playList, 0)
    }

    private fun playSong(songs: List<Song>) {
        val playList = PlayList(songs)
        playSong(playList, 0)
    }

    private fun playSong(playList: PlayList?, playIndex: Int) {
        if (playList == null) return
        Log.e("Play List", playList.toString())
        playList.playMode = PreferenceManager.lastPlayMode(this)
        val result = mPlayer!!.play(playList, playIndex)
        val song = playList.currentSong
        //update numer of play
        playList.currentSong.numberOfPlay += 1
        //finished updating
        onSongUpdated(song)
        seekBarProgress!!.progress = 0
        seekBarProgress!!.isEnabled = result
        textViewProgress!!.setText(R.string.mp_music_default_duration)
        if (result) {
//            changeplayerSpeed(1.28f)
            imageViewAlbum!!.startRotateAnimation()
            buttonPlayToggle!!.setImageResource(R.drawable.ic_pause)
            textViewDuration!!.text = TimeUtils.formatDuration(song!!.duration)
        } else {
            buttonPlayToggle!!.setImageResource(R.drawable.ic_play)
            textViewDuration!!.setText(R.string.mp_music_default_duration)
        }

        mHandler.removeCallbacks(mProgressCallback)
        mHandler.post(mProgressCallback)

        this.startService(Intent(this, PlaybackService::class.java))
    }

    private fun updateProgressTextWithProgress(progress: Int) {
        val targetDuration = getDuration(progress)
        textViewProgress!!.text = TimeUtils.formatDuration(targetDuration)
    }

    private fun updateProgressTextWithDuration(duration: Int) {
        textViewProgress!!.text = TimeUtils.formatDuration(duration)
    }

    private fun seekTo(duration: Int) {
        mPlayer!!.seekTo(duration)
    }

    private fun getDuration(progress: Int): Int {
        return (currentSongDuration * (progress.toFloat() / seekBarProgress!!.max)).toInt()
    }

    // Player Callbacks
    override fun onSwitchLast(last: Song?) {
        onSongUpdated(last)
    }

    override fun onSwitchNext(next: Song?) {
        onSongUpdated(next)
    }

    override fun onComplete(next: Song?) {
        onSongUpdated(next)
    }

    override fun onPlayStatusChanged(isPlaying: Boolean) {
        updatePlayToggle(isPlaying)
        if (isPlaying) {
            imageViewAlbum!!.resumeRotateAnimation()
            mHandler.removeCallbacks(mProgressCallback)
            mHandler.post(mProgressCallback)
        } else {
            imageViewAlbum!!.pauseRotateAnimation()
            mHandler.removeCallbacks(mProgressCallback)
        }
    }

    // MVP View
    override fun handleError(error: Throwable) {
        //Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
    }

    override fun onPlaybackServiceBound(service: PlaybackService) {
        mPlayer = service
        mPlayer!!.registerCallback(this)
    }

    override fun onPlaybackServiceUnbound() {
        mPlayer!!.unregisterCallback(this)
        mPlayer = null
    }

    override fun onSongSetAsFavorite(song: Song) {
        buttonFavoriteToggle!!.isEnabled = true
        updateFavoriteToggle(song.isFavorite)
    }

    override fun onSongUpdated(song: Song?) {
        if (song == null) {
            imageViewAlbum!!.cancelRotateAnimation()
            buttonPlayToggle!!.setImageResource(R.drawable.ic_play)
            seekBarProgress!!.progress = 0
            updateProgressTextWithProgress(0)
            seekTo(0)
            mHandler.removeCallbacks(mProgressCallback)
            return
        }

        // Step 1: Song name and artist
        textViewName!!.text = song.displayName
        textViewArtist!!.text = song.artist
        // Step 2: favorite
        buttonFavoriteToggle!!.setImageResource(if (song.isFavorite) R.drawable.ic_favorite_yes else R.drawable.ic_favorite_no)
        // Step 3: Duration
        textViewDuration!!.text = TimeUtils.formatDuration(song.duration)
        // Step 4: Keep these things updated
        // - Album rotation
        // - Progress(textViewProgress & seekBarProgress)
        val metadataRetriever = MediaMetadataRetriever()
        var file = File(song.path)
        metadataRetriever.setDataSource(file.absolutePath)
        val albumData = metadataRetriever.embeddedPicture
        if (this != null) {
            Glide.with(this)
                    .load(albumData)
                    .crossFade()
                    .transform(CircleTransform(this))
                    .error(com.musicplayer.aow.R.drawable.vinyl_blue)
                    .into(imageViewAlbum)
        }

        imageViewAlbum!!.pauseRotateAnimation()
        mHandler.removeCallbacks(mProgressCallback)
        if (mPlayer!!.isPlaying) {
            imageViewAlbum!!.startRotateAnimation()
            mHandler.post(mProgressCallback)
            buttonPlayToggle!!.setImageResource(R.drawable.ic_pause)
        }
    }

    override fun updatePlayMode(playMode: PlayMode) {
        when (playMode) {
            PlayMode.LIST -> buttonPlayModeToggle!!.setImageResource(R.drawable.ic_play_mode_list)
            PlayMode.LOOP -> buttonPlayModeToggle!!.setImageResource(R.drawable.ic_play_mode_loop)
            PlayMode.SHUFFLE -> buttonPlayModeToggle!!.setImageResource(R.drawable.ic_play_mode_shuffle)
            PlayMode.SINGLE -> buttonPlayModeToggle!!.setImageResource(R.drawable.ic_play_mode_single)
            PlayMode.default -> buttonPlayModeToggle!!.setImageResource(R.drawable.ic_play_mode_loop)
        }
    }

    override fun updatePlayToggle(play: Boolean) {
        buttonPlayToggle!!.setImageResource(if (play) R.drawable.ic_pause else R.drawable.ic_play)
    }

    override fun updateFavoriteToggle(favorite: Boolean) {
        buttonFavoriteToggle!!.setImageResource(if (favorite) R.drawable.ic_favorite_yes else R.drawable.ic_favorite_no)
    }

    override fun setPresenter(presenter: MusicPlayerContract.Presenter) {
        mPresenter = presenter
    }

    // speed values displayed in the spinner
    private fun getSpeedStrings(): Array<String> {
        return arrayOf("1.0", "1.05", "1.1", "1.15", "1.2", "1.25", "1.30", "1.35", "1.40")
    }

//    private fun setSpeedOptions() {
//        val speeds = getSpeedStrings()
//        var arrayAdapter =  ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, speeds)
//        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        speedOptions!!.setAdapter(arrayAdapter)
////
////        // change player playback speed if a speed is selected
//        speedOptions!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
//                if (mPlayer != null) {
//                    var selectedSpeed = speedOptions!!.getItemAtPosition(position).toString().toFloat()
//
//                    changeplayerSpeed(selectedSpeed);
//                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                /*Do something if nothing selected*/
//            }
//        }
//    }

    fun changeplayerSpeed(speed: Float) {
        var player = Player.instance
        var mPlayer = player!!.mPlayer
        // this checks on API 23 and up
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mPlayer!!.isPlaying) {
                mPlayer.playbackParams = mPlayer.playbackParams.setSpeed(speed)
            } else {
//                mPlayer.playbackParams = mPlayer.playbackParams.setSpeed(speed)
//                mPlayer.playbackParams.setSpeed(speed)
//                mPlayer.pause()
            }
        }
    }

    fun getConvertedValue(intVal: Int): Float {
        var floatVal = 0.0f
        floatVal = .2f * intVal
        return floatVal
    }

    companion object {

        private val TAG = "MusicPlayerActivity"

        // Update seek bar every second
        private val UPDATE_PROGRESS_INTERVAL: Long = 1000

        fun newIntent(context: Context): Intent {
            val intent = Intent(context, MusicPlayerActivity::class.java)
            return intent
        }
    }
}
