package com.ttech.qrscanner.core.manager

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

class BannerAdManager {
    companion object{
        fun loadBannerAd(adView: AdView) {
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        }
    }
}