package com.ttech.qrscanner.core.base

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.ttech.qrscanner.utils.IntentUtils.restartApp

open class BaseActivity() : AppCompatActivity() {

    private var mIsRecreated: Boolean = false
    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler(Looper.getMainLooper())
        mIsRecreated = savedInstanceState != null
        if (mIsRecreated) {
            restartApp(this)
        } else {
            //do what you want do
        }
    }

    fun isRecreated() = mIsRecreated
}