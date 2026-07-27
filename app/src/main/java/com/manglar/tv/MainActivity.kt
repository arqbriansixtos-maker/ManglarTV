package com.manglar.tv

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.SystemClock
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayInputStream

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var fullscreenContainer: FrameLayout
    private lateinit var progressBar: ProgressBar

    private var customView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null

    // Historial propio de URLs: muchos sitios tipo SPA navegan con history.replaceState()
    // en vez de pushState(), lo que significa que webView.canGoBack() nunca detecta esas
    // navegaciones. Lo llevamos nosotros mismos como respaldo.
    private val pilaHistorial = mutableListOf<String>()
    private var ultimoBackPressTime = 0L

    private val targetUrl = "https://manglarpelis.manglar.fun/"

    private val adHostFragments = listOf(
        "doubleclick.net", "googlesyndication.com", "google-analytics.com",
        "googletagmanager.com", "googletagservices.com", "adservice.google",
        "pagead2.googlesyndication", "ads.google.com",
        "propellerads.com", "propeller-ads.com", "popads.net", "poper.pro",
        "exoclick.com", "juicyads.com", "adsterra.com", "adnxs.com",
        "taboola.com", "outbrain.com", "revcontent.com", "mgid.com",
        "clickadu.com", "hilltopads.net", "adcash.com", "yllix.com",
        "trafficjunky.net", "adskeeper.co.uk", "smartadserver.com",
        "onclickmax.com", "adsco.re", "media.net", "criteo.com", "criteo.net",
        "casalemedia.com", "pubmatic.com", "rubiconproject.com", "openx.net",
        "moatads.com", "quantserve.com", "scorecardresearch.com",
        "bluekai.com", "demdex.net", "everesttech.net", "turn.com",
        "mathtag.com", "serving-sys.com", "bidswitch.net", "sharethrough.com",
        "teads.tv", "prebid.org", "adition.com", "adform.net",
        "amazon-adsystem.com", "aps.amazon.com", "amazonadsi.com",
        "amazon.com", "amazonaws.com", "amzn.to", "amzn.com",
        "simpli.fi",
        "yieldmo.com", "sonobi.com", "nativo.com", "connatix.com",
        "confiant-integrations.net", "geoedge.be", "doubleverify.com",
        "adsafeprotected.com", "indexww.com", "33across.com",
        "chartbeat.com", "parsely.com", "hotjar.com", "clarity.ms",
        "facebook.com", "facebook.net", "twitter.com",
        "snap.licdn.com", "bat.bing.com", "onetrust.com", "cookielaw.org",
        "popcash.net", "popmyads.com", "monetag.com",
        "trafficstars.com", "zedo.com", "infolinks.com",
        "playwire.com", "magnite.com", "triplelift.com",
        "coinimp.com", "coinhive.com", "coin-hive.com",
        "authedmine.com", "crypto-loot.com", "webminepool.com",
        "jsecoin.com", "browsermine.com",
        "ad-maven.com", "ad-shield.io", "coinnebula.com",
        "sh.st", "ouo.io", "bc.vc", "shorte.st", "adfoc.us",
        "linkbucks.com",
        "bit.ly", "t.co",
        "imasdk.googleapis.com",
        "jivox.com", "spotxchange.com",
        "stickyadstv.com", "tribalfusion.com",
        "freewheel.com", "freewheel.tv",
        "vindicosuite.com", "sociomantic.com",
        "ad4game.com",
        "minutemediapro.com",
        "richpush.com", "galaksion.com", "evadav.com",
        "bongacams.com", "livejasmin.com", "chaturbate.com",
        "crakrevenue.com", "exoticads.com", "ero-advertising.com",
        "adscendmedia.com", "content.ad", "speakol.com",
        "voluum.com", "zpushkovn.com",
        "casino", "casinoo", "bet365", "betsson", "pokerstars",
        "1xbet", "betway", "draftkings", "fanduel",
        "yahoo.com", "bing.com/search",
        "csgo", "gambling", "slot", "roulette", "blackjack",
        "pachislot", "bonos", "apostas"
    )

    private val adUrlPatterns = listOf(
        "/ads/", "/advert/", "/adverts/",
        "/sponsor/", "/sponsored/",
        "/popunder", "/pop-up",
        "/vast.xml", "/vast2.xml", "/vast-wrapper",
        "/imasdk/", "googlesyndication.com/pagead",
        "/pagead/", "/adsbygoogle",
        "doubleclick.net/adj", "doubleclick.net/ddm/",
        "pagead2.googlesyndication.com", "pagead46.googlesyndication.com",
        "adservice.google.com", "googleadservices.com",
        "stats.g.doubleclick.net", "pagead/1p/",
        "pagead_event", "ads/show", "ads/delivery",
        "/bmi/", "/banner/", "/banners/", "banner.gif", "banner.jpg",
        "/cgi-bin/ads", "/delivery/", "/genads/", "/ads/ads",
        "/dads/", "/dadserver", "/ad_layer", "/ad.layer",
        "/adbutler", "/adbutler.net", "/ad-images",
        "/partner_ads/", "/partner/ads/", "/revenue/ads",
        "/stream/ads", "/promo/",
        "/video-player-ads", "/video/ads", "/videoads/",
        "/video/overlay", "/video/interstitial",
        "vpaid.js", "/vpaid/", "/preroll", "/postroll", "/midroll",
        "/videoad", "/vast", "/vmap", "/ima",
        "/native-ad", "/nativeads", "/native-ads",
        "/recommendation/", "/related-posts", "/related-videos",
        "/widget/ads", "/widget-ads", "/popup", "/pop", "/popupads",
        "exit-intent", "/modal/ad", "/overlay/ad"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        webView = findViewById(R.id.webview)
        fullscreenContainer = findViewById(R.id.fullscreen_container)
        progressBar = findViewById(R.id.progress_bar)

        setupWebView()
        webView.loadUrl(targetUrl)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            mediaPlaybackRequiresUserGesture = false
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            useWideViewPort = true
            loadWithOverviewMode = true
            zoomDensity = WebSettings.ZoomDensity.FAR
            defaultZoom = WebSettings.ZoomDensity.FAR
            setSupportZoom(true)
            builtInZoomControls = false
            displayZoomControls = false
            setDefaultFontSize(80)
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                if (request != null && isAdRequest(request.url.toString())) {
                    return createBlockedResponse()
                }
                return super.shouldInterceptRequest(view, request)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
                if (!pilaHistorial.contains(url)) {
                    pilaHistorial.add(url!!)
                }
                injectZoom()
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                progressBar.progress = newProgress
                progressBar.visibility = if (newProgress < 100) View.VISIBLE else View.GONE
            }

            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                if (customView != null) {
                    callback?.onCustomViewHidden()
                    return
                }
                customView = view
                customViewCallback = callback
                fullscreenContainer.visibility = View.VISIBLE
                fullscreenContainer.addView(customView)
            }

            override fun onHideCustomView() {
                super.onHideCustomView()
                if (customView != null) {
                    fullscreenContainer.removeView(customView)
                    customView = null
                    fullscreenContainer.visibility = View.GONE
                    customViewCallback?.onCustomViewHidden()
                }
            }
        }

        webView.addJavascriptInterface(object : Any() {
            @JavascriptInterface
            fun log(message: String) {
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
            }
        }, "AndroidLog")
    }

    private fun injectZoom() {
        webView.evaluateJavascript("""
            (function() {
                const html = document.documentElement;
                const body = document.body;
                if (html) {
                    html.style.zoom = '80%';
                    html.style.transform = 'scale(0.8)';
                    html.style.transformOrigin = 'top left';
                }
                if (body) {
                    body.style.zoom = '80%';
                    body.style.transform = 'scale(0.8)';
                    body.style.transformOrigin = 'top left';
                    body.style.width = '100vw';
                    body.style.overflow = 'hidden';
                }
                const viewport = document.querySelector('meta[name="viewport"]');
                if (!viewport) {
                    const meta = document.createElement('meta');
                    meta.name = 'viewport';
                    meta.content = 'width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no';
                    document.head.appendChild(meta);
                }
            })();
        """.trimIndent(), null)
    }

    private fun isAdRequest(url: String): Boolean {
        val lowerUrl = url.lowercase()
        for (fragment in adHostFragments) {
            if (lowerUrl.contains(fragment)) {
                return true
            }
        }
        for (pattern in adUrlPatterns) {
            if (lowerUrl.contains(pattern)) {
                return true
            }
        }
        return false
    }

    private fun createBlockedResponse(): WebResourceResponse {
        return WebResourceResponse("text/plain", "utf-8", ByteArrayInputStream("".toByteArray()))
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                val currentTime = SystemClock.uptimeMillis()
                if (currentTime - ultimoBackPressTime > 2000) {
                    ultimoBackPressTime = currentTime
                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else if (pilaHistorial.size > 1) {
                        pilaHistorial.removeAt(pilaHistorial.size - 1)
                        webView.loadUrl(pilaHistorial[pilaHistorial.size - 1])
                    } else {
                        Toast.makeText(this, "Presiona atrás una vez más para salir", Toast.LENGTH_SHORT).show()
                    }
                    true
                } else {
                    finish()
                    true
                }
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }
}
