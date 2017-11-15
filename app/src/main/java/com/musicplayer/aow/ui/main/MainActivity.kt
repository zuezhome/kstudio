package com.musicplayer.aow.ui.main

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.Toolbar
import android.widget.RadioButton
import butterknife.BindView
import butterknife.BindViews
import butterknife.ButterKnife
import butterknife.OnCheckedChanged
import com.musicplayer.aow.R
import com.musicplayer.aow.R.id.*
import com.musicplayer.aow.ui.allsongs.AllSongsFragment
import com.musicplayer.aow.ui.base.BaseActivity
import com.musicplayer.aow.ui.base.BaseFragment
import com.musicplayer.aow.ui.musicupdate.MusicUpdateFragment
import com.musicplayer.aow.ui.music.MusicPlayerFragment

class MainActivity : BaseActivity() {

    //default layout to display 0= player, 1= playlist, 2 = files, .....
//    private val DEFAULT_PAGE_INDEX = 1

    @BindView(R.id.toolbar)
    internal var toolbar: Toolbar? = null

    @BindView(view_pager)
    internal var viewPager: ViewPager? = null

    @BindViews(radio_button_music,
            radio_button_all_songs,
            radio_button_musicupdate)
    internal var radioButtons: List<RadioButton>? = null

    internal lateinit var mTitles: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)

        var radioBtn : List<RadioButton> = listOf(findViewById(R.id.radio_button_music) as RadioButton,
                findViewById(R.id.radio_button_all_songs) as RadioButton,
                findViewById(R.id.radio_button_musicupdate) as RadioButton)
        radioButtons = radioBtn

        viewPager = findViewById(R.id.view_pager) as ViewPager?

        // Main Controls' Titles
        mTitles = resources.getStringArray(R.array.mp_main_titles)

        // Fragments
        var fragments : Array<BaseFragment?> = arrayOfNulls<BaseFragment>(mTitles.size)
        fragments[0] = MusicPlayerFragment()
        fragments[1] = AllSongsFragment()
        fragments[2] = MusicUpdateFragment()

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

        radioButtons!![DEFAULT_PAGE_INDEX].isChecked = true
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    @OnCheckedChanged(radio_button_music,
            radio_button_all_songs,
            radio_button_musicupdate)
    fun onRadioButtonChecked(button: RadioButton, isChecked: Boolean) {
        if (isChecked) {
            onItemChecked(radioButtons!!.indexOf(button))
        }
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
