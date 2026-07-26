package com.manglar.tv

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
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
        "/prebid/", "/header-bidding/",
        "/interstitial-ad",
        "/preroll", "/midroll", "/postroll",
        "/companionad",
        "imasdk.googleapis.com", "/ad_break",
        "/ad-serve", "/adserve",
        "/adrequest", "/ad_request", "/getad",
        "/showad", "/show_ads", "/display-ad"
    )

    private val adCssSelectors = listOf(
        ".ad-container", ".ad-wrapper", ".ad-banner", ".ad-slot",
        ".ad-unit", ".ad-box", ".ad-section",
        ".adsbox", ".ads-container", ".ads-wrapper",
        "[data-ad]", "[data-adunit]", "[data-adunit-id]",
        "[data-dfp]", "[data-ad-slot]", "[data-ad-id]",
        ".banner-ad", ".sidebar-ad", ".footer-ad", ".header-ad",
        ".popup-ad", ".overlay-ad", ".interstitial-ad",
        ".ad-overlay", ".ad-modal", ".ad-popup", ".ad-fullscreen",
        "#ad-container", "#ad-wrapper", "#ad-banner", "#ad-overlay",
        ".sponsored-content", ".sponsored-post",
        ".promo-banner",
        ".taboola", ".outbrain", ".revcontent", ".mgid",
        ".nativo", ".teads", ".connatix",
        ".popunder", ".pop-under",
        ".adblock", ".adblock-overlay",
        ".social-toolbar", ".share-bar-floating",
        ".crypto-miner", ".coin-miner", ".miner-container",
        ".video-ad", ".video-ads", ".preroll-ad",
        ".ima-ad-container", ".google-ad-container",
        ".dfp-ad", ".ad-video", ".player-ad",
        ".ad-dfp", ".ad-google", ".ad-block-wrapper",
        ".adLayer", ".ad-layer", ".adZone",
        ".adElement", ".adv-container", ".adv-banner",
        "ins.adsbygoogle", "amp-ad",
        "[id*=\"google_ads\"]",
        "[class*=\"ad-true\"]", "[class*=\"ad-false\"]",
        ".jw-icon-notice", ".jw-overlay", ".jw-click-handler",
        "[class*=\"overlay-player\"]", "[class*=\"player-overlay\"]",
        "[class*=\"vast\"]", "[class*=\"preroll\"]",
        "[class*=\"float-banner\"]", "[class*=\"sticky-banner\"]",
        "[class*=\"click-overlay\"]", "[class*=\"click-blocker\"]",
        "[class*=\"tap-overlay\"]", "[class*=\"tap-block\"]",
        "[class*=\"anti-adblock\"]", "[class*=\"adblock-detect\"]",
        "[id*=\"preroll\"]", "[id*=\"midroll\"]", "[id*=\"overlay-ad\"]"
    )

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        webView = findViewById(R.id.webView)
        fullscreenContainer = findViewById(R.id.fullscreenContainer)
        progressBar = findViewById(R.id.progressBar)

        configurarWebView()
        webView.loadUrl(targetUrl)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val code = event.keyCode

        if (code == KeyEvent.KEYCODE_DPAD_UP ||
            code == KeyEvent.KEYCODE_DPAD_DOWN ||
            code == KeyEvent.KEYCODE_DPAD_LEFT ||
            code == KeyEvent.KEYCODE_DPAD_RIGHT
        ) {
            if (event.action == KeyEvent.ACTION_DOWN) {
                val dir = when (code) {
                    KeyEvent.KEYCODE_DPAD_UP -> "up"
                    KeyEvent.KEYCODE_DPAD_DOWN -> "down"
                    KeyEvent.KEYCODE_DPAD_LEFT -> "left"
                    KeyEvent.KEYCODE_DPAD_RIGHT -> "right"
                    else -> return false
                }
                webView.evaluateJavascript("window.__tvNav.move('$dir')", null)
                return true
            }
            if (event.action == KeyEvent.ACTION_UP) {
                val dir = when (code) {
                    KeyEvent.KEYCODE_DPAD_UP -> "up"
                    KeyEvent.KEYCODE_DPAD_DOWN -> "down"
                    KeyEvent.KEYCODE_DPAD_LEFT -> "left"
                    KeyEvent.KEYCODE_DPAD_RIGHT -> "right"
                    else -> return false
                }
                webView.evaluateJavascript("window.__tvNav.stop('$dir')", null)
                return true
            }
        }

        if (code == KeyEvent.KEYCODE_DPAD_CENTER || code == KeyEvent.KEYCODE_ENTER) {
            if (event.action == KeyEvent.ACTION_DOWN) {
                webView.evaluateJavascript("window.__tvNav && window.__tvNav.click()", null)
                return true
            }
        }

        if (event.action == KeyEvent.ACTION_DOWN) {
            when (code) {
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
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                    webView.evaluateJavascript(
                        """(function(){
                            var v=document.querySelector('video');
                            if(v){if(v.paused)v.play();else v.pause();return;}
                            var iframes=document.querySelectorAll('iframe');
                            for(var i=0;i<iframes.length;i++){
                                var s=(iframes[i].src||'').toLowerCase();
                                if(s.indexOf('vimeo')!==-1||s.indexOf('player')!==-1||s.indexOf('vidhide')!==-1||s.indexOf('streamwish')!==-1||s.indexOf('voe')!==-1){
                                    try{iframes[i].contentWindow.postMessage(JSON.stringify({method:'play'}),'*');}catch(e){}
                                }
                            }
                        })();""", null
                    )
                    return true
                }
                KeyEvent.KEYCODE_MEDIA_PLAY -> {
                    webView.evaluateJavascript(
                        """(function(){
                            var v=document.querySelector('video');
                            if(v){v.play();return;}
                            var iframes=document.querySelectorAll('iframe');
                            for(var i=0;i<iframes.length;i++){
                                var s=(iframes[i].src||'').toLowerCase();
                                if(s.indexOf('vimeo')!==-1||s.indexOf('player')!==-1||s.indexOf('vidhide')!==-1||s.indexOf('streamwish')!==-1||s.indexOf('voe')!==-1){
                                    try{iframes[i].contentWindow.postMessage(JSON.stringify({method:'play'}),'*');}catch(e){}
                                }
                            }
                        })();""", null
                    )
                    return true
                }
                KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                    webView.evaluateJavascript(
                        """(function(){
                            var v=document.querySelector('video');
                            if(v){v.pause();return;}
                            var iframes=document.querySelectorAll('iframe');
                            for(var i=0;i<iframes.length;i++){
                                var s=(iframes[i].src||'').toLowerCase();
                                if(s.indexOf('vimeo')!==-1||s.indexOf('player')!==-1||s.indexOf('vidhide')!==-1||s.indexOf('streamwish')!==-1||s.indexOf('voe')!==-1){
                                    try{iframes[i].contentWindow.postMessage(JSON.stringify({method:'pause'}),'*');}catch(e){}
                                }
                            }
                        })();""", null
                    )
                    return true
                }
                KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                    webView.evaluateJavascript("var v=document.querySelector('video');if(v)v.currentTime+=10;", null)
                    return true
                }
                KeyEvent.KEYCODE_MEDIA_REWIND -> {
                    webView.evaluateJavascript("var v=document.querySelector('video');if(v)v.currentTime-=10;", null)
                    return true
                }
                KeyEvent.KEYCODE_MENU -> {
                    toggleFullscreen()
                    return true
                }
                KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_MUTE -> {
                    return false
                }
            }
        }

        return super.dispatchKeyEvent(event)
    }

    private fun toggleFullscreen() {
        if (customView != null) {
            webView.webChromeClient?.onHideCustomView()
        } else {
            webView.webChromeClient?.onShowCustomView(webView, object : WebChromeClient.CustomViewCallback {
                override fun onCustomViewHidden() {}
            })
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configurarWebView() {
        val settings: WebSettings = webView.settings

        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.databaseEnabled = true
        settings.loadWithOverviewMode = false
        settings.useWideViewPort = true
        settings.mediaPlaybackRequiresUserGesture = false
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
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
                inyectarAutoPlay()
                webView.requestFocus()
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString()?.lowercase() ?: return false
                val host = request.url?.host?.lowercase() ?: return false

                if (url.startsWith("javascript:")) return false

                val esManglar = host.endsWith("manglar.fun")
                if (esManglar) return false

                val esRecursoEstatico = url.contains("fonts.googleapis.com") ||
                    url.contains("fonts.gstatic.com") ||
                    url.contains("cdnjs.cloudflare.com") ||
                    url.contains("cdn.jsdelivr.net") ||
                    url.contains("vimeo.com") ||
                    url.contains("vimeocdn.com") ||
                    url.contains("vidhide") ||
                    url.contains("streamwish") ||
                    url.contains("voe.sx") ||
                    url.contains("voeunblock") ||
                    url.endsWith(".css") ||
                    url.endsWith(".png") ||
                    url.endsWith(".jpg") ||
                    url.endsWith(".svg") ||
                    url.endsWith(".woff") ||
                    url.endsWith(".woff2")
                if (esRecursoEstatico) return false

                val esAd = adHostFragments.any { host.contains(it) }
                if (esAd) return true

                return true
            }

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                val url = request?.url?.toString()?.lowercase()
                    ?: return super.shouldInterceptRequest(view, request)
                val host = request.url?.host?.lowercase() ?: ""

                if (host.endsWith("manglar.fun")) {
                    val esAdEnManglar = url.contains("/ads/") ||
                        url.contains("/advert/") ||
                        url.contains("pagead") ||
                        url.contains("adsbygoogle")
                    if (esAdEnManglar) {
                        return WebResourceResponse("text/plain", "utf-8", ByteArrayInputStream(ByteArray(0)))
                    }
                    return super.shouldInterceptRequest(view, request)
                }

                val esRecursoOk = url.contains("vimeo.com") ||
                    url.contains("vimeocdn.com") ||
                    url.contains("vidhide") ||
                    url.contains("streamwish") ||
                    url.contains("voe.sx") ||
                    url.contains("voeunblock") ||
                    url.contains("fonts.googleapis.com") ||
                    url.contains("fonts.gstatic.com")
                if (esRecursoOk) {
                    return super.shouldInterceptRequest(view, request)
                }

                val esAd = adHostFragments.any { host.contains(it) }
                if (esAd) {
                    return WebResourceResponse("text/plain", "utf-8", ByteArrayInputStream(ByteArray(0)))
                }

                val esAdUrl = adUrlPatterns.any { url.contains(it) }
                if (esAdUrl) {
                    return WebResourceResponse("text/plain", "utf-8", ByteArrayInputStream(ByteArray(0)))
                }

                val esTracker = url.contains("facebook.com") ||
                    url.contains("adsbygoogle") ||
                    url.contains("imasdk") ||
                    url.contains("googlesyndication") ||
                    url.contains("/vast.xml") ||
                    url.contains("doubleclick.net") ||
                    url.contains("/preroll") ||
                    url.contains("/midroll") ||
                    url.contains("/postroll") ||
                    url.contains("prebid") ||
                    url.contains("/ad_break") ||
                    url.contains("/vast") ||
                    url.contains("/vpaid") ||
                    url.contains("amazon") ||
                    url.contains("casino") ||
                    url.contains("bet") ||
                    url.contains("slot") ||
                    url.contains("poker")

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
        val hostListStr = adHostFragments.take(80).joinToString(",") { "\"$it\"" }

        val js = """
            (function() {
                if (window.__adBlockInstalado) return;
                window.__adBlockInstalado = true;

                var adHosts = [$hostListStr];

                var style = document.createElement('style');
                style.id = '__manglar_adblock';
                style.textContent = '$selectorStr { display: none !important; } ' +
                    '.video-container *, .player-container *, #player * { cursor: default !important; }';
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
                        for (var i = ads.length - 1; i >= 0; i--) {
                            if (ads[i] && ads[i].parentNode) {
                                ads[i].parentNode.removeChild(ads[i]);
                            }
                        }

                        var iframes = document.querySelectorAll('iframe');
                        for (var i = iframes.length - 1; i >= 0; i--) {
                            var src = (iframes[i].src || '').toLowerCase();
                            if (isAdUrl(src) && iframes[i].parentNode) {
                                iframes[i].parentNode.removeChild(iframes[i]);
                            }
                        }

                        var scripts = document.querySelectorAll('script[src]');
                        for (var i = scripts.length - 1; i >= 0; i--) {
                            var src = (scripts[i].src || '').toLowerCase();
                            if (isAdUrl(src) && scripts[i].parentNode) {
                                scripts[i].parentNode.removeChild(scripts[i]);
                            }
                        }

                        var bigFixed = document.querySelectorAll('div[style*="z-index: 9999"], div[style*="z-index:9999"], div[style*="z-index: 99999"], div[style*="z-index:99999"]');
                        for (var i = bigFixed.length - 1; i >= 0; i--) {
                            if (bigFixed[i].querySelector('video') === null) {
                                bigFixed[i].style.display = 'none';
                            }
                        }

                        var overlaysSobreVideo = document.querySelectorAll(
                            'div[style*="position: fixed"], div[style*="position:fixed"], ' +
                            'div[style*="position: absolute"][style*="z-index"], ' +
                            'div[class*="overlay"], div[class*="modal"], div[class*="popup"]'
                        );
                        for (var i = overlaysSobreVideo.length - 1; i >= 0; i--) {
                            var el = overlaysSobreVideo[i];
                            var tieneVideo = el.querySelector('video');
                            if (!tieneVideo && el.className.toString().match(/overlay|modal|popup|bell|subscribe/i)) {
                                el.style.display = 'none';
                                el.remove();
                            }
                        }

                        var clickBlockers = document.querySelectorAll('[class*="click-overlay"], [class*="click-blocker"], [class*="tap-overlay"], [class*="tap-block"], [class*="anti-adblock"]');
                        for (var i = clickBlockers.length - 1; i >= 0; i--) {
                            clickBlockers[i].remove();
                        }
                    } catch(e) {}
                }

                function bloquearVideoAds() {
                    try {
                        var videos = document.querySelectorAll('video');
                        for (var i = 0; i < videos.length; i++) {
                            var v = videos[i];
                            if (v._adBlocked) continue;
                            v._adBlocked = true;
                            var origPlay = v.play;
                            v.play = function() {
                                if (this.dataset && this.dataset.adPlaying === 'true') return Promise.resolve();
                                return origPlay.apply(this, arguments);
                            };
                        }
                    } catch(e) {}
                }

                function bloquearPopunders() {
                    try {
                        window.open = function() { return null; };

                        var origAssign = window.location.assign;
                        window.location.assign = function(url) {
                            var s = (url || '').toLowerCase();
                            if (s.indexOf('amazon') !== -1 || s.indexOf('casino') !== -1 ||
                                s.indexOf('bet') !== -1 || s.indexOf('poker') !== -1 ||
                                s.indexOf('slot') !== -1) return;
                            origAssign.call(window.location, url);
                        };

                        var origReplace = window.location.replace;
                        window.location.replace = function(url) {
                            var s = (url || '').toLowerCase();
                            if (s.indexOf('amazon') !== -1 || s.indexOf('casino') !== -1 ||
                                s.indexOf('bet') !== -1 || s.indexOf('poker') !== -1 ||
                                s.indexOf('slot') !== -1) return;
                            origReplace.call(window.location, url);
                        };

                        document.addEventListener('click', function(e) {
                            var el = e.target;
                            while (el && el !== document) {
                                if (el.tagName === 'A') {
                                    var href = (el.href || '').toLowerCase();
                                    if (href.indexOf('amazon') !== -1 || href.indexOf('casino') !== -1 ||
                                        href.indexOf('bet') !== -1 || href.indexOf('poker') !== -1 ||
                                        href.indexOf('slot') !== -1 || el.target === '_blank') {
                                        e.preventDefault();
                                        e.stopPropagation();
                                        return false;
                                    }
                                }
                                el = el.parentNode;
                            }
                        }, true);
                    } catch(e) {}
                }

                eliminarAds();
                bloquearVideoAds();
                bloquearPopunders();

                var observer = new MutationObserver(function(mutations) {
                    for (var m = 0; m < mutations.length; m++) {
                        if (mutations[m].addedNodes.length > 0) {
                            eliminarAds();
                            bloquearVideoAds();
                        }
                    }
                });
                observer.observe(document.body || document.documentElement, {
                    childList: true,
                    subtree: true
                });

                setTimeout(eliminarAds, 500);
                setTimeout(eliminarAds, 1500);
                setTimeout(eliminarAds, 2000);
                setTimeout(eliminarAds, 3000);
                setTimeout(eliminarAds, 5000);
                setTimeout(eliminarAds, 8000);
                setInterval(eliminarAds, 2000);
            })();
        """.trimIndent()

        webView.evaluateJavascript(js, null)
    }

    private fun inyectarNavegacionTV() {
        val js = """
            (function() {
                if (window.__tvNav) return;

                var SPEED = 10;
                var SCROLL_ZONE = 80;
                var cx = window.innerWidth / 2;
                var cy = window.innerHeight / 2;
                var timers = {};

                var cursor = document.createElement('div');
                cursor.id = '__tv_cursor';
                cursor.style.cssText = 'position:fixed !important;z-index:2147483647 !important;pointer-events:none !important;width:26px;height:26px;border:3px solid #00e5ff;border-radius:50%;transform:translate(-50%,-50%);box-shadow:0 0 8px rgba(0,229,255,0.6);background:rgba(0,229,255,0.1);left:50%;top:50%;';
                document.documentElement.appendChild(cursor);

                function draw() {
                    cursor.style.left = cx + 'px';
                    cursor.style.top = cy + 'px';
                    focusElement();
                }

                function focusElement() {
                    try {
                        cursor.style.pointerEvents = 'none';
                        var el = document.elementFromPoint(cx, cy);
                        if (!el) return;
                        var c = el;
                        for (var i = 0; i < 15; i++) {
                            if (!c || c === document.body || c === document.documentElement) break;
                            if (c.tagName === 'IFRAME') { c.focus(); return; }
                            if (c.tagName === 'A' || c.tagName === 'BUTTON' || c.tagName === 'INPUT' ||
                                c.tagName === 'SELECT' || c.tagName === 'TEXTAREA' ||
                                c.getAttribute('role') === 'button' || c.getAttribute('role') === 'link' ||
                                c.getAttribute('role') === 'tab' || c.getAttribute('role') === 'menuitem' ||
                                (c.getAttribute('tabindex') !== null && c.getAttribute('tabindex') !== '-1') ||
                                c.tabIndex > 0 ||
                                window.getComputedStyle(c).cursor === 'pointer') {
                                c.focus();
                                return;
                            }
                            c = c.parentElement;
                        }
                        if (el.tabIndex >= 0) el.focus();
                    } catch(e) {}
                }

                function autoScroll() {
                    var vh = window.innerHeight;
                    if (cy > vh - SCROLL_ZONE) {
                        window.scrollBy(0, 6);
                    }
                    if (cy < SCROLL_ZONE) {
                        window.scrollBy(0, -6);
                    }
                }

                function move(dir) {
                    if (timers[dir]) return;
                    timers[dir] = setInterval(function() {
                        if (dir === 'up') cy -= SPEED;
                        if (dir === 'down') cy += SPEED;
                        if (dir === 'left') cx -= SPEED;
                        if (dir === 'right') cx += SPEED;
                        if (cx < 5) cx = 5;
                        if (cy < 5) cy = 5;
                        var vw = window.innerWidth;
                        var vh = window.innerHeight;
                        if (cx > vw - 5) cx = vw - 5;
                        if (cy > vh - 5) cy = vh - 5;
                        draw();
                        autoScroll();
                    }, 25);
                }

                function stop(dir) {
                    if (timers[dir]) { clearInterval(timers[dir]); timers[dir] = null; }
                    focusElement();
                }

                function doClick() {
                    try {
                        cursor.style.display = 'none';
                        var el = document.elementFromPoint(cx, cy);
                        cursor.style.display = 'block';
                        if (!el) return;
                        var c = el;
                        var target = el;
                        for (var i = 0; i < 10; i++) {
                            if (!c || c === document.body || c === document.documentElement) break;
                            if (c.tagName === 'A' || c.tagName === 'BUTTON' || c.tagName === 'INPUT' ||
                                c.tagName === 'SELECT' || c.tagName === 'TEXTAREA' ||
                                c.getAttribute('role') === 'button' || c.getAttribute('role') === 'link' ||
                                c.getAttribute('role') === 'tab' || c.getAttribute('role') === 'menuitem' ||
                                c.onclick || window.getComputedStyle(c).cursor === 'pointer') {
                                target = c;
                                break;
                            }
                            c = c.parentElement;
                        }
                        var opts = {bubbles: true, clientX: cx, clientY: cy, cancelable: true};
                        target.dispatchEvent(new MouseEvent('mousedown', opts));
                        target.dispatchEvent(new MouseEvent('mouseup', opts));
                        target.dispatchEvent(new MouseEvent('click', opts));
                        target.click();
                        if (target.tagName === 'INPUT' || target.tagName === 'TEXTAREA' || target.tagName === 'SELECT') {
                            target.focus();
                            target.dispatchEvent(new FocusEvent('focus', {bubbles: true}));
                            target.dispatchEvent(new FocusEvent('focusin', {bubbles: true}));
                        }
                    } catch(e) {}
                }

                window.__tvNav = {
                    move: move,
                    stop: stop,
                    click: doClick,
                    getX: function() { return cx; },
                    getY: function() { return cy; }
                };

                draw();
            })();
        """.trimIndent()
        webView.evaluateJavascript(js, null)
    }

    private fun inyectarAutoPlay() {
        val js = """
            (function() {
                if (window.__autoPlayInstalado) return;
                window.__autoPlayInstalado = true;

                var url = window.location.href.toLowerCase();
                var esPaginaPelicula = url.includes('/peliculas/') || url.includes('/movie/') ||
                    url.includes('/ver/') || url.includes('/watch/') ||
                    url.includes('/genero/') || url.includes('/genre/') ||
                    url.includes('/pelicula/');

                if (!esPaginaPelicula) return;

                function autoSeleccionarServidor() {
                    try {
                        var todos = document.querySelectorAll('button, a, div, span, li, [class*="server"], [class*="source"], [class*="opt"], [class*="btn"]');
                        var mejorBtn = null;

                        for (var i = 0; i < todos.length; i++) {
                            var el = todos[i];
                            var texto = el.textContent.toLowerCase().trim();
                            var clase = (el.className || '').toLowerCase();

                            var esSpanishMain = (texto.includes('spanish') && texto.includes('main')) ||
                                (texto.includes('español') && texto.includes('main')) ||
                                texto.includes('\u26A1') && texto.includes('spanish') ||
                                clase.includes('spanish') && clase.includes('main') ||
                                (texto.includes('span') && texto.includes('main'));

                            if (esSpanishMain && !el._autoClicked) {
                                mejorBtn = el;
                                break;
                            }
                        }

                        if (!mejorBtn) {
                            for (var i = 0; i < todos.length; i++) {
                                var el = todos[i];
                                var texto = el.textContent.toLowerCase().trim();
                                var esSpanish = texto.includes('spanish') || texto.includes('español') ||
                                    texto.includes('latino') || texto.includes('\u26A1');
                                if (esSpanish && !el._autoClicked) {
                                    mejorBtn = el;
                                    break;
                                }
                            }
                        }

                        if (!mejorBtn) {
                            var servidores = document.querySelectorAll('[class*="server"], [class*="source"], [class*="option"]');
                            for (var i = 0; i < servidores.length; i++) {
                                if (!servidores[i]._autoClicked) {
                                    mejorBtn = servidores[i];
                                    break;
                                }
                            }
                        }

                        if (mejorBtn && !mejorBtn._autoClicked) {
                            mejorBtn._autoClicked = true;
                            setTimeout(function() {
                                mejorBtn.click();
                                var enlace = mejorBtn.querySelector('a');
                                if (enlace) enlace.click();
                                var boton = mejorBtn.querySelector('button');
                                if (boton) boton.click();
                            }, 300);
                        }
                    } catch(e) {}
                }

                function autoReproducir() {
                    try {
                        var videos = document.querySelectorAll('video');
                        for (var i = 0; i < videos.length; i++) {
                            var v = videos[i];
                            if (v._autoPlayBinded) continue;
                            v._autoPlayBinded = true;

                            v.muted = false;
                            v.autoplay = true;
                            v.removeAttribute('preload');
                            v.preload = 'auto';

                            v.addEventListener('loadeddata', function() {
                                var self = this;
                                setTimeout(function() {
                                    if (self.paused) self.play().catch(function(){});
                                }, 200);
                            });

                            v.addEventListener('canplay', function() {
                                var self = this;
                                setTimeout(function() {
                                    if (self.paused) self.play().catch(function(){});
                                }, 150);
                            });

                            v.addEventListener('canplaythrough', function() {
                                var self = this;
                                setTimeout(function() {
                                    if (self.paused) self.play().catch(function(){});
                                }, 100);
                            });

                            if (v.readyState >= 1) {
                                v.play().catch(function(){});
                            }
                        }

                        var playBtns = document.querySelectorAll(
                            '.vjs-big-play-button, .jw-icon-display, [aria-label*="Play"], [aria-label*="play"], ' +
                            '[title*="Play"], [title*="play"], [title*="Reproducir"], [aria-label*="Reproducir"], ' +
                            '.plyr__control, .plyr-play, .play-btn, .player-play, ' +
                            '[class*="play-button"], [class*="play-btn"], [class*="playBtn"], ' +
                            'button[aria-label*="Play"], button[title*="Play"]'
                        );
                        for (var i = 0; i < playBtns.length; i++) {
                            if (!playBtns[i]._autoClicked) {
                                playBtns[i]._autoClicked = true;
                                playBtns[i].click();
                            }
                        }

                        var bigPlayContainers = document.querySelectorAll(
                            '.vjs-poster, .jw-display-icon-container, [class*="big-play"], ' +
                            '[class*="play-overlay"], [class*="play-poster"]'
                        );
                        for (var i = 0; i < bigPlayContainers.length; i++) {
                            if (!bigPlayContainers[i]._autoClicked) {
                                bigPlayContainers[i]._autoClicked = true;
                                bigPlayContainers[i].click();
                            }
                        }

                        var iframes = document.querySelectorAll('iframe');
                        for (var i = 0; i < iframes.length; i++) {
                            var ifr = iframes[i];
                            var src = (ifr.src || '').toLowerCase();
                            if (src.indexOf('vimeo') !== -1) {
                                ifr.contentWindow.postMessage(JSON.stringify({method:'play'}), '*');
                            }
                            if (src.indexOf('vidhide') !== -1 || src.indexOf('streamwish') !== -1 || src.indexOf('voe') !== -1) {
                                ifr.contentWindow.postMessage('{"event":"play"}', '*');
                                ifr.contentWindow.postMessage(JSON.stringify({type:'play'}), '*');
                            }
                        }
                    } catch(e) {}
                }

                function cerrarPopups() {
                    try {
                        var modals = document.querySelectorAll('.modal.show, .modal[style*="display: block"], .popup, [role="dialog"], [role="alertdialog"]');
                        for (var i = modals.length - 1; i >= 0; i--) {
                            var btn = modals[i].querySelector('.close, [class*="close"], [aria-label="Close"], [aria-label="Cerrar"]');
                            if (btn) btn.click();
                            else modals[i].style.display = 'none';
                        }
                    } catch(e) {}
                }

                autoSeleccionarServidor();
                autoReproducir();
                cerrarPopups();

                var obs = new MutationObserver(function() {
                    setTimeout(autoSeleccionarServidor, 200);
                    setTimeout(autoReproducir, 300);
                    setTimeout(autoReproducir, 800);
                    setTimeout(autoReproducir, 1500);
                    setTimeout(cerrarPopups, 100);
                });
                obs.observe(document.body || document.documentElement, {childList: true, subtree: true});

                setTimeout(autoSeleccionarServidor, 500);
                setTimeout(autoReproducir, 600);
                setTimeout(autoReproducir, 1000);
                setTimeout(autoSeleccionarServidor, 2000);
                setTimeout(autoReproducir, 2500);
                setTimeout(autoReproducir, 4000);
            })();
        """.trimIndent()
        webView.evaluateJavascript(js, null)
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}
