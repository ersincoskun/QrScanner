package com.ttech.qrscanner.utils

import android.os.SystemClock
import android.view.View

abstract class OnSingleClickListener : View.OnClickListener {

    companion object {
        const val MIN_CLICK_INTERVAL = 600
    }

    var lastClickTime: Long = 0

    override fun onClick(v: View?) {
        val currentClickTime = SystemClock.uptimeMillis()
        val elapsedTime = currentClickTime - lastClickTime

        lastClickTime = currentClickTime

        if (elapsedTime > MIN_CLICK_INTERVAL)
            onSingleClick(v)
    }

    abstract fun onSingleClick(view: View?)
}