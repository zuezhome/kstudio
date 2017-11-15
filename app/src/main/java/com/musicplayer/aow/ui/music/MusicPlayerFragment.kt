package com.musicplayer.aow.ui.music

import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.musicplayer.aow.R
import com.musicplayer.aow.RxBus
import com.musicplayer.aow.data.model.PlayList
import com.musicplayer.aow.data.model.Song
import com.musicplayer.aow.data.source.AppRepository
import com.musicplayer.aow.data.source.PreferenceManager
import com.musicplayer.aow.event.PlayListNowEvent
import com.musicplayer.aow.event.PlaySongEvent
import com.musicplayer.aow.player.IPlayback
import com.musicplayer.aow.player.PlayMode
import com.musicplayer.aow.player.PlaybackService
import com.musicplayer.aow.ui.base.BaseFragment
import com.musicplayer.aow.ui.widget.ShadowImageView
import com.musicplayer.aow.utils.AlbumUtils
import com.musicplayer.aow.utils.TimeUtils
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com.musicpalyer.com.musicplayer.aow
 * Date: 9/1/16
 * Time: 9:58 PM
 * Desc: MusicPlayerFragment
 */

//class MusicPlayerFragment : BaseFragment(), MusicPlayerContract.View, IPlayback.Callback {
class MusicPlayerFragment : BaseFragment(){

    @BindView(R.id.image_view_album)
    internal var imageViewAlbum: ShadowImageView? = null
    @BindView(R.id.text_view_name)
    internal var textViewName: TextView? = null
    @BindView(R.id.text_view_artist)
    internal var textViewArtist: TextView? = null
    @BindView(R.id.text_view_progress)
    internal var textViewProgress: TextView? = null
    @BindView(R.id.text_view_duration)
    internal var textViewDuration: TextView? = null
    @BindView(R.id.seek_bar)
    internal var seekBarProgress: SeekBar? = null

    @BindView(R.id.button_play_mode_toggle)
    internal var buttonPlayModeToggle: ImageView? = null
    @BindView(R.id.button_play_toggle)
    internal var buttonPlayToggle: ImageView? = null
    @BindView(R.id.button_favorite_toggle)
    internal var buttonFavoriteToggle: ImageView? = null
    //    @BindView(R.id.button_pop_playlist)
    //    ImageView buttonPopPlaylist;

    private var mPlayer: IPlayback? = null

    private val mHandler = Handler()

    private var mPresenter: MusicPlayerContract.Presenter? = null

//    private val mProgressCallback = object : Runnable {
//        override fun run() {
//            if (isDetached) return
//
//            if (mPlayer!!.isPlaying) {
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
//            }
//        }
//    }

//    private val currentSongDuration: Int
//        get() {
//            val currentSong = mPlayer!!.playingSong
//            var duration = 0
//            if (currentSong != null) {
//                duration = currentSong.duration
//            }
//            return duration
//        }

    private var currentSongDuration: Int = 0

    fun currentSongDuration(song : Song)  {
            val currentSong = song
            if (currentSong != null) {
                currentSongDuration = currentSong.duration
            }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_music, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view!!)

        textViewName = view.findViewById(R.id.text_view_name) as TextView
        textViewArtist = view.findViewById(R.id.text_view_artist) as TextView
        // Step 3: Duration
        textViewDuration = view.findViewById(R.id.text_view_duration) as TextView

        seekBarProgress = view.findViewById(R.id.seek_bar) as SeekBar
        seekBarProgress!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    updateProgressTextWithProgress(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
//                mHandler.removeCallbacks(mProgressCallback)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                seekTo(getDuration(seekBar.progress))
//                if (mPlayer!!.isPlaying) {
//                    mHandler.removeCallbacks(mProgressCallback)
//                    mHandler.post(mProgressCallback)
//                }
            }
        })
//        MusicPlayerPresenter(activity, AppRepository, this).subscribe()
    }

    override fun onStart() {
        super.onStart()
//        if (mPlayer != null && mPlayer!!.isPlaying) {
//            mHandler.removeCallbacks(mProgressCallback)
//            mHandler.post(mProgressCallback)
//        }
    }

    override fun onStop() {
        super.onStop()
//        mHandler.removeCallbacks(mProgressCallback)
    }

    override fun onDestroyView() {
//        mPresenter!!.unsubscribe()
        super.onDestroyView()
    }

    // Click Events
    //    @OnClick(R.id.button_pop_playlist)
    //    public void onPopPlaylistAction(View view){
    //        Intent i = new Intent(getContext(), PopActivity.class);
    //        startActivity(i);
    //    }

    @OnClick(R.id.button_play_toggle)
    fun onPlayToggleAction(view: View) {
        if (mPlayer == null) return

        if (mPlayer!!.isPlaying) {
            mPlayer!!.pause()
        } else {
            mPlayer!!.play()
        }
    }

    @OnClick(R.id.button_play_mode_toggle)
    fun onPlayModeToggleAction(view: View) {
        if (mPlayer == null) return

        val current = PreferenceManager.lastPlayMode(activity)
        val newMode = PlayMode.switchNextMode(current)
        PreferenceManager.setPlayMode(activity, newMode)
        mPlayer!!.setPlayMode(newMode)
        updatePlayMode(newMode)
    }

    @OnClick(R.id.button_play_last)
    fun onPlayLastAction(view: View) {
        if (mPlayer == null) return

        mPlayer!!.playLast()
    }

    @OnClick(R.id.button_play_next)
    fun onPlayNextAction(view: View) {
        if (mPlayer == null) return
        mPlayer!!.playNext()
    }

    @OnClick(R.id.button_favorite_toggle)
    fun onFavoriteToggleAction(view: View) {
        if (mPlayer == null) return

        val currentSong = mPlayer!!.playingSong
        if (currentSong != null) {
            view.isEnabled = false
//            mPresenter!!.setSongAsFavorite(currentSong, !currentSong.isFavorite)
        }
    }

    // RXBus Events

    override fun subscribeEvents(): Subscription? {
        return RxBus.instance?.toObservable()
                ?.observeOn(AndroidSchedulers.mainThread())?.doOnNext { o ->
            if (o is PlaySongEvent) {
                onPlaySongEvent(o)
            } else if (o is PlayListNowEvent) {
                onPlayListNowEvent(o)
            }
        }?.subscribe(RxBus.defaultSubscriber())
    }

    //this plays a single song selected from explore activity
    private fun onPlaySongEvent(event: PlaySongEvent) {
        val song = event.song
        playSong(song)
    }

    //this plays playlist (play now) action
    private fun onPlayListNowEvent(event: PlayListNowEvent) {
        val playList = event.playList
        val playIndex = event.playIndex
        playSong(playList, playIndex)
    }

    // Music Controls
    fun playSong(song: Song) {
//        val playList = PlayList(song)
        playOneSong(song)
//        playSong(playList, 0)
    }

    private fun playOneSong(playSong: Song) {
        var mediaPlayer = MediaPlayer()
        val result = true
        val song = playSong

        //set current song duration
        currentSongDuration(song)

        mediaPlayer.reset()
        mediaPlayer.setDataSource(song.path)
        mediaPlayer.prepare()
        mediaPlayer.start()

        // Step 1: Song name and artist
        textViewName!!.text = song.title
        textViewArtist!!.text = song.artist
        // Step 2: favorite
//        buttonFavoriteToggle!!.setImageResource(if (song.isFavorite) R.drawable.ic_favorite_yes else R.drawable.ic_favorite_no)
        // Step 3: Duration
        textViewDuration!!.text = TimeUtils.formatDuration(song.duration)
        // Step 4: Keep these things updated
        // - Album rotation
        // - Progress(textViewProgress & seekBarProgress)
        val bitmap = AlbumUtils.parseAlbum(song)
        if (bitmap == null) {
            imageViewAlbum!!.setImageResource(R.drawable.vinyl_blue)
        } else {
//            imageViewAlbum!!.setImageResource(R.drawable.vinyl_blue)
            imageViewAlbum!!.setImageBitmap(AlbumUtils.getCroppedBitmap(bitmap))
        }
//        imageViewAlbum!!.startRotateAnimation()
        imageViewAlbum!!.pauseRotateAnimation()
//        mHandler.removeCallbacks(mProgressCallback)
        if (mPlayer!!.isPlaying) {
            imageViewAlbum!!.startRotateAnimation()
//            mHandler.post(mProgressCallback)
            buttonPlayToggle!!.setImageResource(R.drawable.ic_pause)
        }

        seekBarProgress!!.progress = 0
        seekBarProgress!!.isEnabled = true
        textViewProgress!!.setText(R.string.mp_music_default_duration)
        if (result) {
            imageViewAlbum!!.startRotateAnimation()
            buttonPlayToggle!!.setImageResource(R.drawable.ic_pause)
            textViewDuration!!.text = TimeUtils.formatDuration(song!!.duration)
        } else {
            buttonPlayToggle!!.setImageResource(R.drawable.ic_play)
            textViewDuration!!.setText(R.string.mp_music_default_duration)
        }
//
//        mHandler.removeCallbacks(mProgressCallback)
//        mHandler.post(mProgressCallback)

//        activity.startService(Intent(activity, PlaybackService::class.java))
    }

    private fun playSong(playList: PlayList?, playIndex: Int) {
        if (playList == null) return
        playList.playMode = PreferenceManager.lastPlayMode(activity)
        val result = mPlayer!!.play(playList, playIndex)
        val song = playList.currentSong
//        onSongUpdated(song)
        seekBarProgress!!.progress = 0
        seekBarProgress!!.isEnabled = result
        textViewProgress!!.setText(R.string.mp_music_default_duration)
        if (result) {
            imageViewAlbum!!.startRotateAnimation()
            buttonPlayToggle!!.setImageResource(R.drawable.ic_pause)
            textViewDuration!!.text = TimeUtils.formatDuration(song!!.duration)
        } else {
            buttonPlayToggle!!.setImageResource(R.drawable.ic_play)
            textViewDuration!!.setText(R.string.mp_music_default_duration)
        }

//        mHandler.removeCallbacks(mProgressCallback)
//        mHandler.post(mProgressCallback)

        activity.startService(Intent(activity, PlaybackService::class.java))
    }

    private fun updateProgressTextWithProgress(progress: Int) {
        val targetDuration = getDuration(progress)
        textViewProgress?.text  = TimeUtils.formatDuration(targetDuration)
    }

    private fun updateProgressTextWithDuration(duration: Int) {
        textViewProgress?.text = TimeUtils.formatDuration(duration)
    }

    private fun seekTo(duration: Int) {
        mPlayer!!.seekTo(duration)
    }

    private fun getDuration(progress: Int): Int {
        return (currentSongDuration * (progress.toFloat() / seekBarProgress!!.max)).toInt()
    }

    // Player Callbacks

//    override fun onSwitchLast(last: Song?) {
//        onSongUpdated(last)
//    }
//
//    override fun onSwitchNext(next: Song?) {
//        onSongUpdated(next)
//    }
//
//    override fun onComplete(next: Song?) {
//        onSongUpdated(next)
//    }
//
//    override fun onPlayStatusChanged(isPlaying: Boolean) {
//        updatePlayToggle(isPlaying)
//        if (isPlaying) {
//            imageViewAlbum!!.resumeRotateAnimation()
//            mHandler.removeCallbacks(mProgressCallback)
//            mHandler.post(mProgressCallback)
//        } else {
//            imageViewAlbum!!.pauseRotateAnimation()
//            mHandler.removeCallbacks(mProgressCallback)
//        }
//    }

    // MVP View

//    override fun handleError(error: Throwable) {
//        Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
//    }
//
//    override fun onPlaybackServiceBound(service: PlaybackService) {
//        mPlayer = service
//        mPlayer!!.registerCallback(this)
//    }
//
//    override fun onPlaybackServiceUnbound() {
//        mPlayer!!.unregisterCallback(this)
//        mPlayer = null
//    }
//
//    override fun onSongSetAsFavorite(song: Song) {
//        buttonFavoriteToggle!!.isEnabled = true
//        updateFavoriteToggle(song.isFavorite)
//    }

//    override fun onSongUpdated(song: Song?) {
//        if (song == null) {
//            imageViewAlbum!!.cancelRotateAnimation()
//            buttonPlayToggle!!.setImageResource(R.drawable.ic_play)
//            seekBarProgress!!.progress = 0
//            updateProgressTextWithProgress(0)
//            seekTo(0)
//            mHandler.removeCallbacks(mProgressCallback)
//            return
//        }
//
//        // Step 1: Song name and artist
//        textViewName!!.text = song.title
//        textViewArtist!!.text = song.artist
//        // Step 2: favorite
//        buttonFavoriteToggle!!.setImageResource(if (song.isFavorite) R.drawable.ic_favorite_yes else R.drawable.ic_favorite_no)
//        // Step 3: Duration
//        textViewDuration!!.text = TimeUtils.formatDuration(song.duration)
//        // Step 4: Keep these things updated
//        // - Album rotation
//        // - Progress(textViewProgress & seekBarProgress)
//        val bitmap = AlbumUtils.parseAlbum(song)
//        if (bitmap == null) {
//            imageViewAlbum!!.setImageResource(R.drawable.vinyl_blue)
//        } else {
//            imageViewAlbum!!.setImageBitmap(AlbumUtils.getCroppedBitmap(bitmap))
//        }
//        imageViewAlbum!!.pauseRotateAnimation()
//        mHandler.removeCallbacks(mProgressCallback)
//        if (mPlayer!!.isPlaying) {
//            imageViewAlbum!!.startRotateAnimation()
//            mHandler.post(mProgressCallback)
//            buttonPlayToggle!!.setImageResource(R.drawable.ic_pause)
//        }
//    }

    fun updatePlayMode(playMode: PlayMode) {
        var playMode = playMode
        if (playMode == null) {
            playMode = PlayMode.default
        }
        when (playMode) {
            PlayMode.LIST -> buttonPlayModeToggle!!.setImageResource(R.drawable.ic_play_mode_list)
            PlayMode.LOOP -> buttonPlayModeToggle!!.setImageResource(R.drawable.ic_play_mode_loop)
            PlayMode.SHUFFLE -> buttonPlayModeToggle!!.setImageResource(R.drawable.ic_play_mode_shuffle)
            PlayMode.SINGLE -> buttonPlayModeToggle!!.setImageResource(R.drawable.ic_play_mode_single)
        }
    }

//    override fun updatePlayToggle(play: Boolean) {
//        buttonPlayToggle!!.setImageResource(if (play) R.drawable.ic_pause else R.drawable.ic_play)
//    }
//
//    override fun updateFavoriteToggle(favorite: Boolean) {
//        buttonFavoriteToggle!!.setImageResource(if (favorite) R.drawable.ic_favorite_yes else R.drawable.ic_favorite_no)
//    }
//
//    override fun setPresenter(presenter: MusicPlayerContract.Presenter) {
//        mPresenter = presenter
//    }

    companion object {

        private val TAG = "MusicPlayerFragment"

        // Update seek bar every second
        private val UPDATE_PROGRESS_INTERVAL: Long = 1000
    }
}
