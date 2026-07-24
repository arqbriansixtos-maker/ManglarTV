package com.manglar.tv

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayInputStream

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var fullscreenContainer: FrameLayout
    private lateinit var progressBar: ProgressBar

    private var customView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null

    private val targetUrl = "https://manglarpelis.manglar.fun/"

    private val allowedHostSuffixes = listOf(
        "manglar.fun",
        "cloudfront.net",
        "amazonaws.com",
        "googleapis.com",
        "gstatic.com",
        "youtube.com",
        "ytimg.com",
        "vimeo.com",
        "jwpcdn.com",
        "jwpltx.com",
        "flowplayer.org",
        "flowplayer.com",
        "cdn.flowplayer.org",
        "hls.ru",
        "akamaized.net",
        "cloudflare.com",
        "fontawesome.com",
        "fonts.googleapis.com",
        "fonts.gstatic.com",
        "cdnjs.cloudflare.com",
        "jquery.com",
        "bootstrapcdn.com",
        "tmdb.org",
        "themoviedb.org",
        "image.tmdb.org",
        "imdb.com",
        "manglarpelis.manglar.fun"
    )

    private val adHostFragments = listOf(
        "doubleclick.net",
        "googlesyndication.com",
        "google-analytics.com",
        "googletagmanager.com",
        "googletagservices.com",
        "adservice.google",
        "pagead2.googlesyndication",
        "ads.google.com",
        "propellerads.com",
        "propeller-ads.com",
        "popads.net",
        "poper.pro",
        "exoclick.com",
        "juicyads.com",
        "adsterra.com",
        "adnxs.com",
        "taboola.com",
        "outbrain.com",
        "revcontent.com",
        "mgid.com",
        "clickadu.com",
        "hilltopads.net",
        "adcash.com",
        "yllix.com",
        "trafficjunky.net",
        "adskeeper.co.uk",
        "smartadserver.com",
        "onclickmax.com",
        "adsco.re",
        "media.net",
        "criteo.com",
        "criteo.net",
        "casalemedia.com",
        "pubmatic.com",
        "rubiconproject.com",
        "openx.net",
        "moatads.com",
        "quantserve.com",
        "scorecardresearch.com",
        "bluekai.com",
        "demdex.net",
        "everesttech.net",
        "turn.com",
        "mathtag.com",
        "serving-sys.com",
        "bidswitch.net",
        "sharethrough.com",
        "teads.tv",
        "spotxchange.com",
        "prebid.org",
        "spotx.tv",
        "adition.com",
        "adform.net",
        "adobedtm.com",
        "amazon-adsystem.com",
        "aps.amazon.com",
        "aps.amazon-adsystem.com",
        "contextual.media.net",
        "simpli.fi",
        "lijit.com",
        "tapad.com",
        "brealtime.com",
        "emxdgt.com",
        "adsymptotic.com",
        "corra.com",
        "bidgear.com",
        "vindicosuite.com",
        "tribalfusion.com",
        "stickyadstv.com",
        "yieldmo.com",
        "sonobi.com",
        "nativo.com",
        "nativo.net",
        "connatix.com",
        "connatix.com.cdn",
        "confiant-integrations.net",
        "geoedge.be",
        "iasds01.com",
        "doubleverify.com",
        "adsafeprotected.com",
        "izatcloud.net",
        "mookie1.com",
        "tribalfusion.com",
        "undertone.com",
        "synacor.com",
        "indexww.com",
        "33across.com",
        "krxd.net",
        "blueconic.net",
        "chartbeat.com",
        "chartbeat.net",
        "parsely.com",
        "hotjar.com",
        "fullstory.com",
        "mouseflow.com",
        "crazyegg.com",
        "optimizely.com",
        "segment.io",
        "segment.com",
        "amplitude.com",
        "mixpanel.com",
        "branch.io",
        "adjust.com",
        "appsflyer.com",
        "kochava.com",
        "singular.net",
        "hasoffers.com",
        "impact.com",
        "partnerstack.com",
        "clickmeter.com",
        "bit.ly",
        "t.co",
        "adobe.com",
        "omtrdc.net",
        "2o7.net",
        "demdex.net",
        "everesttech.net",
        "licdn.com",
        "ads.linkedin.com",
        "facebook.com/tr",
        "facebook.net",
        "fbcdn.net",
        "twitter.com/i/adsct",
        "snap.licdn.com",
        "bing.com/bat.js",
        "clarity.ms",
        "bat.bing.com",
        "newrelic.com",
        "nr-data.net",
        "sentry.io",
        "bugsnag.com",
        "rollbar.com",
        "cookieselection.com",
        "onetrust.com",
        "cookielaw.org",
        "trustarc.com",
        "evidon.com",
        "sourcepoint.mgr.consensu.org",
        "privacymanager.io",
        "kampyle.com",
        "medallia.com",
        "surveymonkey.com",
        "qualaroo.com",
        "optinmonster.com",
        "sumo.com",
        "sumome.com",
        "sumo.ly",
        "adcash.com",
        "popcash.net",
        "popmyads.com",
        "monetizer101.com",
        "exoclick.com",
        "trafficjunky.com",
        "trafficjunky.net",
        "juicyads.com",
        "exoticads.com",
        "ero-advertising.com",
        "adscendmedia.com",
        "content.ad",
        "speakol.com",
        "newspepper.com",
        "dafunkid.com",
        "voluum.com",
        "zpushkovn.com",
        "bongacams.com",
        "livejasmin.com",
        "chaturbate.com",
        "crakrevenue.com",
        "hilltopads.net",
        "onclickadu.com",
        "ad-maven.com",
        "monetag.com",
        "richpush.com",
        "galaksion.com",
        "losrados.com",
        "evadav.com",
        "crakRevenue.com",
        "trafficstars.com",
        "benzinga.com",
        "zedo.com",
        "serving-sys.com",
        "sociomantic.com",
        "ad4game.com",
        "infolinks.com",
        "viglink.com",
        "skimlinks.com",
        "nativo.com",
        "teads.tv",
        "connatix.com",
        "minutemediapro.com",
        "jwplayer.com",
        "playwire.com",
        "freewheel.com",
        "freewheel.com",
        "magnite.com",
        "pubmatic.com",
        "triplelift.com",
        "sharethrough.com",
        "smartadserver.com",
        "adingo.jp",
        "a]dfly.com",
        "sh.st",
        "ouo.io",
        "bluemedium.com",
        "bc.vc",
        "shorte.st",
        "adfoc.us",
        "linkbucks.com",
        "cutUrls.com",
        "coinimp.com",
        "coinhive.com",
        "coin-hive.com",
        "authedmine.com",
        "crypto-loot.com",
        "webminepool.com",
        "minero.pw",
        "minr.pw",
        "ppoi.org",
        "jsecoin.com",
        "browsermine.com",
        "coin-service.com",
        "monerominer.rocks",
        "gridcash.net",
        "ad-miner.com",
        "coinlab.biz",
        "webmine.cz",
        "minero.cc",
        "coinnebula.com"
    )

    private val adCssSelectors = listOf(
        ".ad", ".ads", ".adv", ".advert", ".advertisement",
        ".ad-container", ".ad-wrapper", ".ad-banner", ".ad-slot",
        ".ad-unit", ".ad-box", ".ad-block", ".ad-section",
        ".adsbox", ".ads-container", ".ads-wrapper",
        "[data-ad]", "[data-ads]", "[data-adunit]", "[data-adunit-id]",
        "[data-dfp]", "[data-ad-slot]",
        ".banner-ad", ".sidebar-ad", ".footer-ad", ".header-ad",
        ".popup-ad", ".overlay-ad", ".interstitial-ad",
        ".ad-overlay", ".ad-modal", ".ad-popup",
        "#ad", "#ads", "#advertisement",
        "#ad-container", "#ad-wrapper", "#ad-banner",
        ".sponsored", ".promo", ".promotion",
        ".taboola", ".outbrain", ".revcontent", ".mgid",
        ".nativo", ".teads", ".connatix",
        ".popunder", ".pop-under", ".poper",
        ".social-toolbar", ".share-bar-floating",
        ".crypto-miner", ".coin-miner", ".miner-container"
    )

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        fullscreenContainer = findViewById(R.id.fullscreenContainer)
        progressBar = findViewById(R.id.progressBar)

        configurarWebView()
        webView.loadUrl(targetUrl)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configurarWebView() {
        val settings: WebSettings = webView.settings

        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.databaseEnabled = true
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        settings.mediaPlaybackRequiresUserGesture = false
        settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.userAgentString = settings.userAgentString + " ManglarTV/1.0"

        settings.javaScriptCanOpenWindowsAutomatically = false
        settings.setSupportMultipleWindows(false)

        webView.isFocusable = true
        webView.isFocusableInTouchMode = true

        webView.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                progressBar.visibility = View.GONE
                inyectarBloqueoAds()
                inyectarNavegacionTV()
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString()?.lowercase() ?: return false
                val host = request.url?.host?.lowercase() ?: return false

                if (url.startsWith("javascript:")) return false

                val esConfiable = allowedHostSuffixes.any { host == it || host.endsWith(".$it") }
                if (esConfiable) return false

                val esAd = adHostFragments.any { host.contains(it) }
                if (esAd) return true

                val esRedirecSospechoso = url.contains("/redirect") ||
                    url.contains("/click") ||
                    url.contains("/track") ||
                    url.contains("/pop") ||
                    url.contains("/go/") ||
                    url.contains("/out/") ||
                    url.contains("utm_source") && !host.contains("manglar")
                if (esRedirecSospechoso && !esConfiable) return true

                return false
            }

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                val url = request?.url?.toString()?.lowercase()
                    ?: return super.shouldInterceptRequest(view, request)
                val host = request.url?.host?.lowercase() ?: ""

                val esAd = adHostFragments.any { host.contains(it) }
                if (esAd) {
                    return WebResourceResponse("text/plain", "utf-8", ByteArrayInputStream(ByteArray(0)))
                }

                val esTracker = url.contains("analytics") ||
                    url.contains("/tracking") ||
                    url.contains("/pixel.gif") ||
                    url.contains("/beacon") ||
                    url.contains("collect?v=") ||
                    url.contains("/collect") && host.contains("google") ||
                    url.contains("facebook.com/tr") ||
                    url.contains("/gtm.js") ||
                    url.contains("/gtag/") ||
                    url.contains("adsbygoogle")

                if (esTracker) {
                    return WebResourceResponse("text/plain", "utf-8", ByteArrayInputStream(ByteArray(0)))
                }

                return super.shouldInterceptRequest(view, request)
            }
        }

        webView.webChromeClient = object : WebChromeClient() {

            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                if (customView != null) {
                    callback?.onCustomViewHidden()
                    return
                }
                customView = view
                customViewCallback = callback
                fullscreenContainer.addView(view)
                fullscreenContainer.visibility = View.VISIBLE
                webView.visibility = View.GONE
            }

            override fun onHideCustomView() {
                fullscreenContainer.visibility = View.GONE
                fullscreenContainer.removeView(customView)
                customView = null
                webView.visibility = View.VISIBLE
                customViewCallback?.onCustomViewHidden()
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress >= 100) {
                    progressBar.visibility = View.GONE
                }
            }

            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: android.os.Message?
            ): Boolean {
                return false
            }
        }
    }

    private fun inyectarBloqueoAds() {
        val selectorStr = adCssSelectors.joinToString(", ") { it }
        val hostListStr = adHostFragments.take(60).joinToString(",") { "\"$it\"" }

        val js = """
            (function() {
                if (window.__adBlockInstalado) return;
                window.__adBlockInstalado = true;

                var adHosts = [$hostListStr];

                var style = document.createElement('style');
                style.id = '__manglar_adblock';
                style.textContent = '$selectorStr { display: none !important; visibility: hidden !important; height: 0 !important; max-height: 0 !important; overflow: hidden !important; opacity: 0 !important; pointer-events: none !important; margin: 0 !important; padding: 0 !important; }';
                document.head.appendChild(style);

                function isAdUrl(src) {
                    if (!src) return false;
                    var s = src.toLowerCase();
                    for (var i = 0; i < adHosts.length; i++) {
                        if (s.indexOf(adHosts[i]) !== -1) return true;
                    }
                    return false;
                }

                function eliminarAds() {
                    try {
                        var ads = document.querySelectorAll('$selectorStr');
                        for (var i = 0; i < ads.length; i++) {
                            if (ads[i] && ads[i].parentNode) ads[i].parentNode.removeChild(ads[i]);
                        }

                        var iframes = document.querySelectorAll('iframe');
                        for (var i = 0; i < iframes.length; i++) {
                            if (isAdUrl(iframes[i].src) && iframes[i].parentNode) {
                                iframes[i].parentNode.removeChild(iframes[i]);
                            }
                        }

                        var scripts = document.querySelectorAll('script[src]');
                        for (var i = 0; i < scripts.length; i++) {
                            if (isAdUrl(scripts[i].src) && scripts[i].parentNode) {
                                scripts[i].parentNode.removeChild(scripts[i]);
                            }
                        }
                    } catch(e) {}
                }

                eliminarAds();
                var observer = new MutationObserver(function() { eliminarAds(); });
                observer.observe(document.body || document.documentElement, {
                    childList: true,
                    subtree: true
                });
                setTimeout(eliminarAds, 500);
                setTimeout(eliminarAds, 1500);
                setTimeout(eliminarAds, 3000);
            })();
        """.trimIndent()

        webView.evaluateJavascript(js, null)
    }

    private fun inyectarNavegacionTV() {
        val js = """
            (function() {
                if (window.__tvNavInstalado) return;
                window.__tvNavInstalado = true;

                var style = document.createElement('style');
                style.innerHTML = '.tv-focus{outline:4px solid #00e5ff !important;outline-offset:2px !important;}';
                document.head.appendChild(style);

                function elementosFocables() {
                    return Array.prototype.slice.call(
                        document.querySelectorAll('a, button, input, [tabindex], [onclick], .card, .item, .poster')
                    ).filter(function(el) {
                        var r = el.getBoundingClientRect();
                        return r.width > 0 && r.height > 0;
                    });
                }

                var actual = null;

                function marcarFoco(el) {
                    if (actual) actual.classList.remove('tv-focus');
                    actual = el;
                    if (actual) {
                        actual.classList.add('tv-focus');
                        actual.scrollIntoView({block: 'center', behavior: 'smooth'});
                    }
                }

                function centro(el) {
                    var r = el.getBoundingClientRect();
                    return {x: r.left + r.width / 2, y: r.top + r.height / 2};
                }

                function moverFoco(direccion) {
                    var candidatos = elementosFocables();
                    if (!candidatos.length) return;
                    if (!actual || candidatos.indexOf(actual) === -1) {
                        marcarFoco(candidatos[0]);
                        return;
                    }
                    var origen = centro(actual);
                    var mejor = null, mejorDist = Infinity;

                    candidatos.forEach(function(el) {
                        if (el === actual) return;
                        var p = centro(el);
                        var dx = p.x - origen.x, dy = p.y - origen.y;
                        var valido = false;
                        if (direccion === 'up' && dy < -5) valido = true;
                        if (direccion === 'down' && dy > 5) valido = true;
                        if (direccion === 'left' && dx < -5) valido = true;
                        if (direccion === 'right' && dx > 5) valido = true;
                        if (!valido) return;
                        var dist = Math.sqrt(dx * dx + dy * dy);
                        if (dist < mejorDist) { mejorDist = dist; mejor = el; }
                    });

                    if (mejor) marcarFoco(mejor);
                }

                window.__tvNav = {
                    move: moverFoco,
                    click: function() {
                        if (actual) actual.click();
                    }
                };

                marcarFoco(elementosFocables()[0]);
            })();
        """.trimIndent()
        webView.evaluateJavascript(js, null)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                if (customView != null) {
                    webView.webChromeClient?.onHideCustomView()
                    return true
                }
                if (webView.canGoBack()) {
                    webView.goBack()
                    return true
                }
            }
            KeyEvent.KEYCODE_DPAD_UP -> {
                webView.evaluateJavascript("window.__tvNav && window.__tvNav.move('up')", null)
                return true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                webView.evaluateJavascript("window.__tvNav && window.__tvNav.move('down')", null)
                return true
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                webView.evaluateJavascript("window.__tvNav && window.__tvNav.move('left')", null)
                return true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                webView.evaluateJavascript("window.__tvNav && window.__tvNav.move('right')", null)
                return true
            }
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                webView.evaluateJavascript("window.__tvNav && window.__tvNav.click()", null)
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}
