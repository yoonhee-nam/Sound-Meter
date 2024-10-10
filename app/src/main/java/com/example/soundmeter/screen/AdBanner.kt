package com.example.soundmeter.screen

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun BannersAds() {
    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = "ca-app-pub-3940256099942544/6300978111"
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        Log.d("BannerAd", "Ad loaded successfully.")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        Log.e("BannerAd", "Ad failed to load: ${error.message}")
                    }

                    override fun onAdOpened() {
                        Log.d("BannerAd", "Ad opened.")
                    }

                    override fun onAdClosed() {
                        Log.d("BannerAd", "Ad closed.")
                    }
                }
                loadAd(AdRequest.Builder().build())
            }
        },
        update = { adView ->
            adView.loadAd(AdRequest.Builder().build())
        }
    )
}