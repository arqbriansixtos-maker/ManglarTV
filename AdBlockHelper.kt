package com.manglar.tv

import android.content.Context
import android.webkit.WebView
import com.nickoala.adblock.AdFilter

object AdBlockHelper {

    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return
        isInitialized = true

        val filter = AdFilter.get(context)
        val viewModel = filter.viewModel

        if (!filter.hasInstallation) {
            val filterLists = mapOf(
                "EasyList" to "https://easylist.to/easylist/easylist.txt",
                "EasyPrivacy" to "https://easylist.to/easylist/easyprivacy.txt",
                "AdGuard Base" to "https://filters.adtidy.org/extension/chromium/filters/2.txt",
                "AdGuard Tracking Protection" to "https://filters.adtidy.org/extension/chromium/filters/3.txt",
                "AdGuard Annoyances" to "https://filters.adtidy.org/extension/chromium/filters/14.txt",
                "AdGuard Chinese" to "https://filters.adtidy.org/extension/chromium/filters/224.txt"
            )

            for ((name, url) in filterLists) {
                val subscription = viewModel.addFilter(name, url)
                viewModel.download(subscription.id)
            }
        }
    }

    fun setupWebView(webView: WebView) {
        val filter = AdFilter.get(webView.context)
        filter.setupWebView(webView)
    }

    fun isAd(url: String): Boolean {
        val filter = AdFilter.get(null)
        return filter.isAd(url)
    }
}
