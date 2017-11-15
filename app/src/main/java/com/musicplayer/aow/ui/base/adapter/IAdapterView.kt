package com.musicplayer.aow.ui.base.adapter

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com.musicpalyer.com.musicplayer.aow
 * Date: 7/11/16
 * Time: 11:43 AM
 * Desc: Reusable list item view.
 * - http://blog.csdn.net/kroclin/article/details/41830315
 */
interface IAdapterView<T> {

    fun bind(item: Any?, position: Int)
}
