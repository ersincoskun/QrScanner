package com.ttech.qrscanner.ui

import androidx.appcompat.widget.Toolbar
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.ttech.qrscanner.R
import com.ttech.qrscanner.core.base.BaseFragment
import com.ttech.qrscanner.databinding.FragmentSplashBinding
import com.ttech.qrscanner.utils.printErrorLog
import com.ttech.qrscanner.utils.remove
import com.ttech.qrscanner.utils.show

class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    private var toolbar: Toolbar? = null

    override fun onLayoutReady() {
        super.onLayoutReady()
        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar?.remove()
        loadAndShowInterstitialAd()
    }

    private fun loadAndShowInterstitialAd() {
        context?.let { safeContext ->
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(safeContext, "ca-app-pub-6126124107542425/9284869904", adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    printErrorLog(adError.toString())
                    toolbar?.show()
                    val action = SplashFragmentDirections.actionSplashFragmentToBarcodeScannerFragment()
                    navigate(navDirections = action)
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    printErrorLog("ad loaded")
                    interstitialAd.show(requireActivity())
                    toolbar?.show()
                    val action = SplashFragmentDirections.actionSplashFragmentToBarcodeScannerFragment()
                    navigate(navDirections = action)
                }
            })
        }
    }

}