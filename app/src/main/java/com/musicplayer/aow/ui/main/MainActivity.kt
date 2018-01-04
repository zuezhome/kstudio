package com.musicplayer.aow.ui.main

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.Toolbar
import butterknife.ButterKnife
import com.musicplayer.aow.R
import com.musicplayer.aow.ui.base.BaseActivity
import com.musicplayer.aow.ui.base.BaseFragment
import com.musicplayer.aow.ui.library.LibraryFragment
import com.github.nisrulz.sensey.Sensey
import com.google.android.gms.ads.MobileAds
import com.musicplayer.aow.ui.settings.SettingsFragment
import com.musicplayer.aow.utils.ApplicationSettings
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.AppCompatImageView
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
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
import com.musicplayer.aow.ui.music.MusicPlayerActivity
import com.musicplayer.aow.ui.musicupdate.MusicUpdateFragment
import com.musicplayer.aow.ui.widget.ShadowImageView
import com.musicplayer.aow.utils.CircleTransform
import com.musicplayer.aow.utils.TimeUtils
import es.claucookie.miniequalizerlibrary.EqualizerView
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import java.io.File


open class MainActivity : BaseActivity(), MusicPlayerContract.View, IPlayback.Callback, View.OnClickListener{

    internal var toolbar: Toolbar? = null
    private var viewPager: ViewPager? = null
    internal var radioButtons: List<RadioButton>? = null
    private lateinit var mTitles: Array<String>

    var layoutBottomSheet: LinearLayout? = null
    var sheetBehavior: BottomSheetBehavior<*>? = null


    //Music BottomSheet Init Componet
    private var mPlayer: IPlayback? = null
    private val mHandler = Handler()
    private var mPresenter: MusicPlayerContract.Presenter? = null
    private var songName: TextView? = null
    private var songArtist: TextView? = null
    private var albumArt: AppCompatImageView? = null
    private var eq: EqualizerView? = null

    //Change app status color every 5 seconds
    var whichColor = true

    fun themBlue(){
        this.setTheme(R.style.AppBlueTheme)
    }

    fun themRed(){
        this.setTheme(R.style.AppBlueTheme)
    }

    @SuppressLint("ResourceAsColor")
    @TargetApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)
        volumeControlStream = AudioManager.STREAM_MUSIC


        //change status bar color
        var window = this.window //app.android.foregroundActivity.getWindow()
        var act = this
        Thread(Runnable {
            while (true) {
                try {
                    Thread.sleep(5000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                this.runOnUiThread {
                    if (whichColor)
                        act.setTheme(R.style.AppBlueTheme)//window!!.setStatusBarColor(R.color.blue)
                    else
                        act.setTheme(R.style.AppTheme)//window!!.setStatusBarColor(R.color.red)
                }
                whichColor = !whichColor
            }
        }).start()

        //START BOTTOMSHEET
        //init componets
        //music bottomsheet
        layoutBottomSheet = findViewById(R.id.bottom_sheet) as LinearLayout
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet)
        /**
         * bottom sheet state change listener
         * we are changing button text when sheet changed state
         * */
        sheetBehavior!!.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        //btnBottomSheet.setText("Close Sheet")
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        //btnBottomSheet.setText("Expand Sheet")
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })

        songName = findViewById(R.id.text_view_name_peep) as TextView
        songArtist = findViewById(R.id.text_view_artist_peep) as TextView
        albumArt = findViewById(R.id.image_view_file_peep) as AppCompatImageView
        eq = findViewById(R.id.equalizer_view_peep) as EqualizerView
        var openMusicVinyl = findViewById(R.id.bottom_sheet_open_music) as LinearLayout


        openMusicVinyl.setOnClickListener {
            val intent = MusicPlayerActivity.newIntent(applicationContext)
            startActivity(intent)
        }

        MusicPlayerPresenter(this, AppRepository.instance!!, this).subscribe()
        //END OF BOTTOMSHEET





        //Application settings
//        ApplicationSettings.instance!!.intialization(this)
//        StorageUtil(applicationContext)!!.storageLocationDir()

        //Google Admob
        MobileAds.initialize(this, "ca-app-pub-4728638624800158~3304357448")

        var radioBtn: List<RadioButton> = listOf(
                findViewById(R.id.radio_button_all_songs) as RadioButton,
                findViewById(R.id.radio_button_musicupdate) as RadioButton,
                findViewById(R.id.radio_button_settings) as RadioButton)
//        findViewById(R.id.radio_button_music) as RadioButton,
        radioButtons = radioBtn
        toolbar = findViewById(R.id.toolbar) as Toolbar

        //viewpager
        viewPager = findViewById(R.id.view_pager) as ViewPager?

        // Main Controls' Titles
        mTitles = resources.getStringArray(R.array.mp_main_titles)

        // Fragments
        var fragments: Array<BaseFragment?> = arrayOfNulls<BaseFragment>(mTitles.size)
//        fragments[0] = MusicPlayerActivity()
        fragments[0] = LibraryFragment()
        fragments[1] = MusicUpdateFragment()
        fragments[2] = SettingsFragment()

        // Inflate ViewPager
        var adapter = MainPagerAdapter(supportFragmentManager, mTitles, fragments)
        viewPager!!.adapter = adapter
        viewPager!!.offscreenPageLimit = adapter.count - 1
        viewPager!!.pageMargin = resources.getDimensionPixelSize(R.dimen.mp_margin_large)
        viewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageScrollStateChanged(state: Int){}

            override fun onPageSelected(position: Int) {
                radioButtons!![position].isChecked = true
            }
        })

        //implement on checked on each radio button
        radioBtn.forEach { e ->
            e.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    onItemChecked(radioButtons!!.indexOf(buttonView))
                }
            }
        }
        radioButtons!![DEFAULT_PAGE_INDEX].isChecked = true
    }

    override fun onBackPressed(){
        moveTaskToBack(true)
    }

    override fun onResume(){
        ApplicationSettings.instance?.ShakeWithSensorDetectorResume()
        super.onResume()
    }

    override fun onStop() {
        mHandler.removeCallbacks(mProgressCallback)
        ApplicationSettings.instance?.ShakeWithSensorDetectorStop()
        super.onStop()
    }

    override fun onDestroy(){
        mPresenter!!.unsubscribe()
        //sensey gesture
        Sensey.getInstance().stop();
        ApplicationSettings.instance?.ShakeWithSensorDetectorDestroy()
        super.onDestroy()
    }

    private fun onItemChecked(position: Int) {
        viewPager!!.currentItem = position
        toolbar!!.title = mTitles[position]
    }


//     manually opening / closing bottom sheet on button click
//    @OnClick(R.id.btn_bottom_sheet)
//    fun toggleBottomSheet() {
//        if (sheetBehavior!!.getState() !== BottomSheetBehavior.STATE_EXPANDED) {
//            sheetBehavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)
//            btnBottomSheet.setText("Close sheet")
//        } else {
//            sheetBehavior!!.setState(BottomSheetBehavior.STATE_COLLAPSED)
//            btnBottomSheet.setText("Expand sheet")
//        }
//    }

    //BOTTOMSHEET FUNCTIONS START
    private val mProgressCallback = object : Runnable {
        override fun run() {
//            if (isDetached) return
            if (mPlayer!!.isPlaying) {
//                val progress = (seekBarProgress!!.max * (mPlayer!!.progress.toFloat() / currentSongDuration.toFloat())).toInt()
//                updateProgressTextWithDuration(mPlayer!!.progress)
//                if (progress >= 0 && progress <= seekBarProgress!!.max) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        seekBarProgress!!.setProgress(progress, true)
//                    } else {
//                        seekBarProgress!!.progress = progress
//                    }
//                    mHandler.postDelayed(this, UPDATE_PROGRESS_INTERVAL)
//                }
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

        playList.playMode = PreferenceManager.lastPlayMode(this)
        val result = mPlayer!!.play(playList, playIndex)
        val song = playList.currentSong
        //update numer of play
        playList.currentSong.numberOfPlay += 1
        //finished updating
        onSongUpdated(song)
//        seekBarProgress!!.progress = 0
//        seekBarProgress!!.isEnabled = result
//        textViewProgress!!.setText(R.string.mp_music_default_duration)
        if (result) {
            //bottomsheet eq
            eq!!.animateBars()
//            imageViewAlbum!!.startRotateAnimation()
//            buttonPlayToggle!!.setImageResource(R.drawable.ic_pause)
//            textViewDuration!!.text = TimeUtils.formatDuration(song!!.duration)
        } else {
            //bottomsheet eq
            eq!!.stopBars()
//            buttonPlayToggle!!.setImageResource(R.drawable.ic_play)
//            textViewDuration!!.setText(R.string.mp_music_default_duration)
        }

        mHandler.removeCallbacks(mProgressCallback)
        mHandler.post(mProgressCallback)

        this.startService(Intent(this, PlaybackService::class.java))
    }

    private fun updateProgressTextWithProgress(progress: Int) {
        val targetDuration = getDuration(progress)
//        textViewProgress!!.text = TimeUtils.formatDuration(targetDuration)
    }

    private fun updateProgressTextWithDuration(duration: Int) {
//        textViewProgress!!.text = TimeUtils.formatDuration(duration)
    }

    private fun seekTo(duration: Int) {
        mPlayer!!.seekTo(duration)
    }

    private fun getDuration(progress: Int): Int {
        return (currentSongDuration * (progress.toFloat())).toInt()
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
            //bottomsheet eq
            eq!!.animateBars()
//            imageViewAlbum!!.resumeRotateAnimation()
            mHandler.removeCallbacks(mProgressCallback)
            mHandler.post(mProgressCallback)
        } else {
            //bottomsheet eq
            eq!!.stopBars()
//            imageViewAlbum!!.pauseRotateAnimation()
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
//        buttonFavoriteToggle!!.isEnabled = true
        updateFavoriteToggle(song.isFavorite)
    }

    override fun onSongUpdated(song: Song?) {
        if (song == null) {
            //botomsheet equalizer
            eq!!.stopBars()
//            imageViewAlbum!!.cancelRotateAnimation()
//            buttonPlayToggle!!.setImageResource(R.drawable.ic_play)
//            seekBarProgress!!.progress = 0
            updateProgressTextWithProgress(0)
            seekTo(0)
            mHandler.removeCallbacks(mProgressCallback)
            return
        }
        //bottomsheet
        songName!!.text = song.displayName
        songArtist!!.text = song.artist

        // Step 1: Song name and artist
//        textViewName!!.text = song.displayName
//        textViewArtist!!.text = song.artist
//        // Step 2: favorite
//        buttonFavoriteToggle!!.setImageResource(if (song.isFavorite) R.drawable.ic_favorite_yes else R.drawable.ic_favorite_no)
//        // Step 3: Duration
//        textViewDuration!!.text = TimeUtils.formatDuration(song.duration)
        // Step 4: Keep these things updated
        // - Album rotation
        // - Progress(textViewProgress & seekBarProgress)
        val metadataRetriever = MediaMetadataRetriever()
        var file = File(song.path)
        metadataRetriever.setDataSource(file.absolutePath)
        val albumData = metadataRetriever.embeddedPicture
        if (this != null) {
            //bottomsheet
            Glide.with(this)
                    .load(albumData)
                    .crossFade()
                    .error(com.musicplayer.aow.R.drawable.vinyl_blue)
                    .into(albumArt)
            //main
//            Glide.with(this)
//                    .load(albumData)
//                    .crossFade()
//                    .transform(CircleTransform(this))
//                    .error(com.musicplayer.aow.R.drawable.vinyl_blue)
//                    .into(imageViewAlbum)
        }

//        imageViewAlbum!!.pauseRotateAnimation()
        mHandler.removeCallbacks(mProgressCallback)
        if (mPlayer!!.isPlaying) {
//            imageViewAlbum!!.startRotateAnimation()
            mHandler.post(mProgressCallback)
//            buttonPlayToggle!!.setImageResource(R.drawable.ic_pause)
        }
    }

    override fun updatePlayMode(playMode: PlayMode) {
        when (playMode) {
//            PlayMode.LIST -> buttonPlayModeToggle!!.setImageResource(R.drawable.ic_play_mode_list)
//            PlayMode.LOOP -> buttonPlayModeToggle!!.setImageResource(R.drawable.ic_play_mode_loop)
//            PlayMode.SHUFFLE -> buttonPlayModeToggle!!.setImageResource(R.drawable.ic_play_mode_shuffle)
//            PlayMode.SINGLE -> buttonPlayModeToggle!!.setImageResource(R.drawable.ic_play_mode_single)
//            PlayMode.default -> buttonPlayModeToggle!!.setImageResource(R.drawable.ic_play_mode_loop)
        }
    }

    override fun updatePlayToggle(play: Boolean) {
//        buttonPlayToggle!!.setImageResource(if (play) R.drawable.ic_pause else R.drawable.ic_play)
    }

    override fun updateFavoriteToggle(favorite: Boolean) {
//        buttonFavoriteToggle!!.setImageResource(if (favorite) R.drawable.ic_favorite_yes else R.drawable.ic_favorite_no)
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
    //BOTTOMSHEET FUNCTIONS END

    companion object {

        //default layout to display 0= player, 1= playlist, 2 = files, .....
        internal var DEFAULT_PAGE_INDEX = 0
        // Update seek bar every second
        private val UPDATE_PROGRESS_INTERVAL: Long = 1000

        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }
}

