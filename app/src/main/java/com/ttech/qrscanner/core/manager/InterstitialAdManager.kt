package com.ttech.qrscanner.core.manager

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.ttech.qrscanner.utils.printErrorLog

object InterstitialAdManager {

    var interstitialAd: InterstitialAd? = null

    fun loadInterstitialAd(context: Context?) {
        context?.let { safeContext ->
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(safeContext, "ca-app-pub-6126124107542425/9284869904", adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    printErrorLog(adError.toString())
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    printErrorLog("ad loaded")
                    this@InterstitialAdManager.interstitialAd = interstitialAd
                }
            })
        }
    }
}