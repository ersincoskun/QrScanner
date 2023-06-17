package com.ttech.qrscanner.core.base

import android.content.Context
import androidx.viewbinding.ViewBinding
import com.ttech.qrscanner.utils.ProjectSettings
import com.ttech.qrscanner.utils.printErrorLog

abstract class BaseUtilityActivity<T : ViewBinding>:BaseTemplateActivity<T>() {
    companion object {
        var isAppInForeground = false
    }

    lateinit var context: Context
    lateinit var activity: BaseActivity<*>

    private var mActivityId: Int = -1
    private var mIsStopped = true

    override fun onCreated() {
        if (ProjectSettings.IS_PAGE_LOG_ENABLE) {
            printErrorLog(javaClass.simpleName + "(" + mActivityId + ") created")
        }
        isAppInForeground = true
        context = this
        activity = this as BaseActivity<*>
        super.onCreated()
    }

    override fun onStarted() {
        if (ProjectSettings.IS_PAGE_LOG_ENABLE) {
            printErrorLog(javaClass.simpleName + "(" + mActivityId + ") started")
        }
        super.onStarted()
        mIsStopped = false
    }

    override fun onResumed() {
        if (ProjectSettings.IS_PAGE_LOG_ENABLE) {
            printErrorLog(javaClass.simpleName + "(" + mActivityId + ") resumed")
        }
        isAppInForeground = true
        super.onResumed()
    }

    override fun onLayoutReady() {
        if (ProjectSettings.IS_PAGE_LOG_ENABLE) {
            printErrorLog(javaClass.simpleName + "(" + mActivityId + ") onLayoutReady")
        }
        super.onLayoutReady()
    }

    override fun onPaused() {
        if (ProjectSettings.IS_PAGE_LOG_ENABLE) {
            printErrorLog(javaClass.simpleName + "(" + mActivityId + ") paused")
        }
        super.onPaused()
        isAppInForeground = false
    }

    override fun onStopped() {
        if (ProjectSettings.IS_PAGE_LOG_ENABLE) {
            printErrorLog(javaClass.simpleName + "(" + mActivityId + ") stopped")
        }
        super.onStopped()
        mIsStopped = true
    }

    override fun onDestroyed() {
        if (ProjectSettings.IS_PAGE_LOG_ENABLE) {
            printErrorLog(javaClass.simpleName + "(" + mActivityId + ") destroyed")
        }
        super.onDestroyed()
    }
}