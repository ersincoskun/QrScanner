package com.ttech.qrscanner.core.manager

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdRequest.Builder
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import com.ttech.qrscanner.utils.printErrorLog


object AppOpenAdManager {

    private val AD_UNIT_ID = "ca-app-pub-6126124107542425/2376195679"

    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false

    fun loadAd(context: Context) {
        if (isLoadingAd || isAdAvailable()) {
            return
        }
        isLoadingAd = true
        val request: AdRequest = Builder().build()
        AppOpenAd.load(
            context, AD_UNIT_ID, request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    // Called when an app open ad has loaded.
                    printErrorLog("appOpenAd loaded")
                    appOpenAd = ad
                    isLoadingAd = false
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Called when an app open ad has failed to load.
                    printErrorLog("appOpenAd load error: ${loadAdError.message}")
                    isLoadingAd = false
                }
            })
    }

    fun showAdIfAvailable(activity: Activity) {
        if (isAdAvailable()) appOpenAd?.show(activity)
    }

    private fun isAdAvailable(): Boolean {
        return appOpenAd != null
    }

}