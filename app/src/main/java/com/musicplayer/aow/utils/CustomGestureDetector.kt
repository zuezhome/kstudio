package com.musicplayer.aow.utils

import android.util.Log
import android.view.MotionEvent
import android.view.GestureDetector



/**
 * Created by Arca on 11/22/2017.
 */
internal class CustomGestureDetector : GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        Log.e("CustomeGesture","onSingleTapConfirmed")
        return true
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        Log.e("CustomeGesture","onDoubleTap")
        return true
    }

    override fun onDoubleTapEvent(e: MotionEvent): Boolean {
        Log.e("CustomeGesture","onDoubleTapEvent")
        return true
    }

    override fun onDown(e: MotionEvent): Boolean {
        Log.e("CustomeGesture","onDown")
        return true
    }

    override fun onShowPress(e: MotionEvent) {
        Log.e("CustomeGesture","onShowPress")
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        Log.e("CustomeGesture","onSingleTapUp")
        return true
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        Log.e("CustomeGesture","onScroll")
        return true
    }

    override fun onLongPress(e: MotionEvent) {
        Log.e("CustomeGesture","onLongPress")
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        Log.e("CustomeGesture","onFling")
        Log.e("Gesture", "onFling " + e1.x + " - " + e2.x)
        if (e1.x < e2.x) {
            Log.e("Gesture", "Left to Right swipe performed")
        }
        if (e1.x > e2.x) {
            Log.e("Gesture", "Right to Left swipe performed")
        }
        if (e1.y < e2.y) {
            Log.e("Gesture", "Up to Down swipe performed")
        }
        if (e1.y > e2.y) {
            Log.e("Gesture", "Down to Up swipe performed")
        }
        return true
    }
}