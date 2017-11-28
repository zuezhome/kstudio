package com.musicplayer.aow.ui.main

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.Toolbar
import android.widget.RadioButton
import butterknife.ButterKnife
import com.musicplayer.aow.R
import com.musicplayer.aow.ui.base.BaseActivity
import com.musicplayer.aow.ui.base.BaseFragment
import com.musicplayer.aow.ui.library.LibraryFragment
import com.musicplayer.aow.ui.musicupdate.MusicUpdateFragment
import com.musicplayer.aow.ui.music.MusicPlayerFragment
import shortbread.Shortbread
import com.github.nisrulz.sensey.Sensey
import com.musicplayer.aow.ui.settings.SettingsFragment
import com.musicplayer.aow.utils.Settings
import com.musicplayer.aow.utils.StorageUtil


open class MainActivity : BaseActivity() {

    internal var toolbar: Toolbar? = null
    private var viewPager: ViewPager? = null
    internal var radioButtons: List<RadioButton>? = null
    private lateinit var mTitles: Array<String>

    @TargetApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)

        Shortbread.create(this);

        //Application settings
        Settings.instance!!.intialization(this)
        StorageUtil(applicationContext)!!.storageLocationDir()

        var radioBtn: List<RadioButton> = listOf(
                findViewById(R.id.radio_button_music) as RadioButton,
                findViewById(R.id.radio_button_all_songs) as RadioButton,
                findViewById(R.id.radio_button_musicupdate) as RadioButton,
                findViewById(R.id.radio_button_settings) as RadioButton)
        radioButtons = radioBtn
        toolbar = findViewById(R.id.toolbar) as Toolbar

        viewPager = findViewById(R.id.view_pager) as ViewPager?

        // Main Controls' Titles
        mTitles = resources.getStringArray(R.array.mp_main_titles)

        // Fragments
        var fragments: Array<BaseFragment?> = arrayOfNulls<BaseFragment>(mTitles.size)
        fragments[0] = MusicPlayerFragment()
        fragments[1] = LibraryFragment()
        fragments[2] = MusicUpdateFragment()
        fragments[3] = SettingsFragment()

        // Inflate ViewPager
        var adapter = MainPagerAdapter(supportFragmentManager, mTitles, fragments)
        viewPager!!.adapter = adapter
        viewPager!!.offscreenPageLimit = adapter.count - 1
        viewPager!!.pageMargin = resources.getDimensionPixelSize(R.dimen.mp_margin_large)
        viewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) =// Empty
                    Unit

            override fun onPageScrollStateChanged(state: Int) =// Empty
                    Unit

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

    override fun onStart(){
        super.onStart()
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    override fun onDestroy(){
        //sensey gesture
        Sensey.getInstance().stop();
        super.onDestroy()
    }

    private fun onItemChecked(position: Int) {
        viewPager!!.currentItem = position
        toolbar!!.title = mTitles[position]
    }

    companion object {

        //default layout to display 0= player, 1= playlist, 2 = files, .....
        internal val DEFAULT_PAGE_INDEX = 1

        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }
}

