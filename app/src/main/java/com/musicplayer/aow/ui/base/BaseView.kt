package com.musicplayer.aow.ui.base

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com.musicpalyer.com.musicplayer.aow
 * Date: 7/8/16
 * Time: 11:49 PM
 * Desc: BaseView
 */
interface BaseView<T> {

    fun setPresenter(presenter: T)
}
