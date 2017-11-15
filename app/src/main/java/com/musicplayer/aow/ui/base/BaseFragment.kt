package com.musicplayer.aow.ui.base

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import rx.Subscription
import rx.subscriptions.CompositeSubscription

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com.musicpalyer.com.musicplayer.aow
 * Date: 3/16/16
 * Time: 12:14 AM
 * Desc: BaseFragment
 */
abstract class BaseFragment : Fragment() {

    private var mSubscriptions: CompositeSubscription? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addSubscription(subscribeEvents())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mSubscriptions != null) {
            mSubscriptions!!.clear()
        }
    }

    protected open fun subscribeEvents(): Subscription? {
        return null
    }

    protected fun addSubscription(subscription: Subscription?) {
        if (subscription == null) return
        if (mSubscriptions == null) {
            mSubscriptions = CompositeSubscription()
        }
        mSubscriptions!!.add(subscription)
    }
}
