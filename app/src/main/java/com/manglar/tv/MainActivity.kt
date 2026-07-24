package com.manglar.tv

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayInputStream

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var fullscreenContainer: FrameLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var tvControls: LinearLayout
    private lateinit var btnPlayPause: Button
    private lateinit var btnFullscreen: Button

    private var customView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null
    private var controlsVisible = false

    private val targetUrl = "https://manglarpelis.manglar.fun/"

    private val allowedHostSuffixes = listOf(
        "manglar.fun",
        "manglarpelis.manglar.fun",
        "fonts.googleapis.com",
        "fonts.gstatic.com",
        "cdnjs.cloudflare.com",
        "cdn.jsdelivr.net"
    )

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
        "amazon-adsystem.com", "aps.amazon.com", "simpli.fi",
        "yieldmo.com", "sonobi.com", "nativo.com", "connatix.com",
        "confiant-integrations.net", "geoedge.be", "doubleverify.com",
        "adsafeprotected.com", "indexww.com", "33across.com",
        "chartbeat.com", "parsely.com", "hotjar.com", "clarity.ms",
        "facebook.com/tr", "facebook.net", "twitter.com/i/adsct",
        "snap.licdn.com", "bat.bing.com", "onetrust.com", "cookielaw.org",
        "popcash.net", "popmyads.com", "monetag.com",
        "trafficstars.com", "zedo.com", "infolinks.com",
        "playwire.com", "magnite.com", "triplelift.com",
        "coinimp.com", "coinhive.com", "coin-hive.com",
        "authedmine.com", "crypto-loot.com", "webminepool.com",
        "jsecoin.com", "browsermine.com",
        "ad-maven.com", "ad-shield.io", "coinnebula.com",
        "sh.st", "ouo.io", "bc.vc", "shorte.st", "adfoc.us",
        "linkbucks.com", "adition.com",
        "bit.ly", "t.co",
        "googletagmanager.com/gtm.js",
        "googlesyndication.com/pagead",
        "amazon-adsystem.com/aax2",
        "amazon-adsystem.com",
        "imasdk.googleapis.com",
        "jivox.com", "spotxchange.com",
        "stickyadstv.com", "tribalfusion.com",
        "freewheel.com", "freewheel.tv",
        "vindicosuite.com", "sociomantic.com",
        "ad4game.com", "doubleclick.net",
        "yieldmo.com", "sharethrough.com",
        "teads.tv", "connatix.com",
        "minutemediapro.com", "playwire.com",
        "ad-maven.com", "monetag.com",
        "richpush.com", "galaksion.com", "evadav.com",
        "trafficstars.com", "benzinga.com",
        "bongacams.com", "livejasmin.com", "chaturbate.com",
        "crakrevenue.com", "exoticads.com", "ero-advertising.com",
        "adscendmedia.com", "content.ad", "speakol.com",
        "voluum.com", "zpushkovn.com",
        "casino", "casinoo", "bet365", "betsson", "pokerstars",
        "1xbet", "betway", "draftkings", "fanduel",
        "yahoo.com", "bing.com/search"
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
        "[class*=\"ad-true\"]", "[class*=\"ad-false\"]"
    )

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        fullscreenContainer = findViewById(R.id.fullscreenContainer)
        progressBar = findViewById(R.id.progressBar)
        tvControls = findViewById(R.id.tvControls)
        btnPlayPause = findViewById(R.id.btnPlayPause)
        btnFullscreen = findViewById(R.id.btnFullscreen)

        btnPlayPause.setOnClickListener {
            webView.evaluateJavascript(
                """
                (function() {
                    var v = document.querySelector('video');
                    if (v) { if (v.paused) v.play(); else v.pause(); }
                })();
                """.trimIndent(), null
            )
            updatePlayPauseButton()
        }

        btnFullscreen.setOnClickListener {
            toggleFullscreen()
        }

        configurarWebView()
        webView.loadUrl(targetUrl)
    }

    private fun updatePlayPauseButton() {
        webView.evaluateJavascript(
            "(function(){ var v=document.querySelector('video'); return v && !v.paused; })();"
        ) { result ->
            val playing = result?.contains("true") == true
            btnPlayPause.text = if (playing) "⏸" else "▶"
        }
    }

    private fun toggleFullscreen() {
        webView.evaluateJavascript(
            """
            (function() {
                if (document.fullscreenElement || document.webkitFullscreenElement) {
                    if (document.exitFullscreen) document.exitFullscreen();
                    else if (document.webkitExitFullscreen) document.webkitExitFullscreen();
                } else {
                    var v = document.querySelector('video');
                    var el = v ? (v.closest('.player, .video-player, [class*="player"], [id*="player"]') || v.parentElement || document.documentElement) : document.documentElement;
                    if (el.requestFullscreen) el.requestFullscreen();
                    else if (el.webkitRequestFullscreen) el.webkitRequestFullscreen();
                }
            })();
            """.trimIndent(), null
        )
    }

    private fun showControls() {
        controlsVisible = true
        tvControls.visibility = View.VISIBLE
        updatePlayPauseButton()
    }

    private fun hideControls() {
        controlsVisible = false
        tvControls.visibility = View.GONE
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
                inyectarAutoPlay()
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
                    url.endsWith(".css") ||
                    url.endsWith(".js") && !url.contains("ads") ||
                    url.endsWith(".png") ||
                    url.endsWith(".jpg") ||
                    url.endsWith(".svg") ||
                    url.endsWith(".woff") ||
                    url.endsWith(".woff2")
                if (esRecursoEstatico) return false

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

                val esAd = adHostFragments.any { host.contains(it) }
                if (esAd) {
                    return WebResourceResponse("text/plain", "utf-8", ByteArrayInputStream(ByteArray(0)))
                }

                val esAdUrl = adUrlPatterns.any { url.contains(it) }
                if (esAdUrl) {
                    return WebResourceResponse("text/plain", "utf-8", ByteArrayInputStream(ByteArray(0)))
                }

                val esTracker = url.contains("facebook.com/tr") ||
                    url.contains("adsbygoogle") ||
                    url.contains("imasdk") ||
                    url.contains("googlesyndication") ||
                    url.contains("/vast.xml") ||
                    url.contains("/vast2.xml") ||
                    url.contains("doubleclick.net") ||
                    url.contains("/preroll") ||
                    url.contains("/midroll") ||
                    url.contains("/postroll") ||
                    url.contains("prebid") ||
                    url.contains("/ad_break") ||
                    url.contains("/vast") ||
                    url.contains("/vpaid")

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
                style.textContent = '$selectorStr { display: none !important; }';
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
                    } catch(e) {}
                }

                function bloquearVideoAds() {
                    try {
                        if (window.videojs) {
                            window.videojs.VAST = window.videojs.VAST || {};
                        }
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

                        if (window.DM && window.DM.player) {
                            var origPlay2 = window.DM.player.prototype.play;
                            window.DM.player.prototype.play = function() {
                                if (this._adPlaying) return;
                                return origPlay2.apply(this, arguments);
                            };
                        }

                        if (window.jwplayer) {
                            try {
                                var players = document.querySelectorAll('.jwplayer, [id*="player"]');
                                for (var i = 0; i < players.length; i++) {
                                    var p = window.jwplayer(players[i].id);
                                    if (p && p.on) {
                                        p.on('adClick', function(e) { e.preventDefault && e.preventDefault(); });
                                    }
                                }
                            } catch(e) {}
                        }
                    } catch(e) {}
                }

                function bloquearPopunders() {
                    try {
                        window.open = function() { return null; };
                        var origTarget = window.HTMLAnchorElement.prototype.__lookupSetter__('target');
                        if (origTarget) {
                            Object.defineProperty(window.HTMLAnchorElement.prototype, 'target', {
                                set: function(v) {
                                    if (v === '_blank') v = '_self';
                                    origTarget.call(this, v);
                                },
                                get: function() {
                                    return origTarget ? origTarget.call(this) : '_self';
                                }
                            });
                        }
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
                setTimeout(eliminarAds, 2000);
                setTimeout(eliminarAds, 5000);
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
                style.id = '__tv_nav_style';
                style.textContent = '\
                    .tv-focus{outline:4px solid #00e5ff !important;outline-offset:3px !important;border-radius:4px !important;box-shadow:0 0 12px rgba(0,229,255,0.5) !important;}\
                ';
                document.head.appendChild(style);

                var SEL = 'a[href], button, input, select, textarea, [tabindex], [onclick], [role="button"], [role="link"], [role="tab"], [role="menuitem"], .card, .item, .poster, .movie, .film, .episode, .server, .btn, .play-btn, .play-button, .source-btn, .video-btn, [class*="play"], [class*="btn"], [class*="card"], [class*="poster"], [class*="movie"], [class*="episode"], [class*="server"], [class*="item"], li a, nav a, .nav-link, .menu-item, .dropdown-item, [class*="nav"] a, [class*="menu"] a';

                function elementosFocables() {
                    return Array.prototype.slice.call(
                        document.querySelectorAll(SEL)
                    ).filter(function(el) {
                        var r = el.getBoundingClientRect();
                        var s = window.getComputedStyle(el);
                        return r.width > 0 && r.height > 0 && s.display !== 'none' && s.visibility !== 'hidden' && s.opacity !== '0';
                    });
                }

                var actual = null;

                function marcarFoco(el) {
                    if (actual) actual.classList.remove('tv-focus');
                    actual = el;
                    if (actual) {
                        actual.classList.add('tv-focus');
                        actual.scrollIntoView({block: 'center', inline: 'center', behavior: 'smooth'});
                        actual.focus();
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
                    var mejor = null, mejorScore = -Infinity;

                    candidatos.forEach(function(el) {
                        if (el === actual) return;
                        var p = centro(el);
                        var dx = p.x - origen.x;
                        var dy = p.y - origen.y;
                        var dist = Math.sqrt(dx * dx + dy * dy);

                        var valido = false;
                        if (direccion === 'up' && dy < -10) valido = true;
                        if (direccion === 'down' && dy > 10) valido = true;
                        if (direccion === 'left' && dx < -10) valido = true;
                        if (direccion === 'right' && dx > 10) valido = true;

                        if (!valido) return;

                        var score = -dist;
                        if (score > mejorScore) { mejorScore = score; mejor = el; }
                    });

                    if (mejor) marcarFoco(mejor);
                }

                function autoFullscreen() {
                    var videos = document.querySelectorAll('video');
                    for (var i = 0; i < videos.length; i++) {
                        var v = videos[i];
                        if (v._tvFullscreenBinded) continue;
                        v._tvFullscreenBinded = true;

                        v.addEventListener('playing', function() {
                            try {
                                var container = this.closest('.player, .video-player, [class*="player"], [id*="player"]') || this.parentElement;
                                if (container && !document.fullscreenElement && !document.webkitFullscreenElement) {
                                    if (container.requestFullscreen) container.requestFullscreen();
                                    else if (container.webkitRequestFullscreen) container.webkitRequestFullscreen();
                                }
                            } catch(e) {}
                        });
                    }
                }

                window.showTvControls = function() {};

                window.__tvNav = {
                    move: moverFoco,
                    click: function() {
                        if (actual) actual.click();
                    }
                };

                autoFullscreen();
                marcarFoco(elementosFocables()[0]);

                var obs = new MutationObserver(function() {
                    setTimeout(autoFullscreen, 1000);
                    setTimeout(function() {
                        var nuevo = elementosFocables();
                        if (actual && nuevo.indexOf(actual) === -1 && nuevo.length > 0) {
                            marcarFoco(nuevo[0]);
                        }
                    }, 500);
                });
                obs.observe(document.body || document.documentElement, {childList: true, subtree: true});

                setInterval(autoFullscreen, 3000);
            })();
        """.trimIndent()
        webView.evaluateJavascript(js, null)
    }

    private fun inyectarAutoPlay() {
        val js = """
            (function() {
                if (window.__autoPlayInstalado) return;
                window.__autoPlayInstalado = true;

                function autoSeleccionarServidor() {
                    try {
                        var url = window.location.href.toLowerCase();
                        var esPaginaServidores = url.includes('/peliculas/') || url.includes('/movie/') ||
                            url.includes('/ver/') || url.includes('/watch/');

                        if (!esPaginaServidores) return;

                        var botones = document.querySelectorAll('button, a, .btn, .server, .source, [class*="server"], [class*="source"], [class*="player"]');
                        var mejorBtn = null;

                        for (var i = 0; i < botones.length; i++) {
                            var texto = botones[i].textContent.toLowerCase();
                            var esSpanish = texto.includes('spanish') || texto.includes('español') ||
                                texto.includes('latino') || texto.includes('span') ||
                                texto.includes('main') || texto.includes('es-');
                            var esRecomendado = texto.includes('recomendado') || texto.includes('recommended') ||
                                texto.includes('mejor') || texto.includes('best') ||
                                texto.includes('1') || texto.includes('hd');

                            if (esSpanish || esRecomendado) {
                                mejorBtn = botones[i];
                                break;
                            }
                        }

                        if (!mejorBtn) {
                            var servidores = document.querySelectorAll('[class*="server"], [class*="source"], [class*="opt"]');
                            if (servidores.length > 0) mejorBtn = servidores[0];
                        }

                        if (mejorBtn && !mejorBtn._autoClicked) {
                            mejorBtn._autoClicked = true;
                            setTimeout(function() { mejorBtn.click(); }, 500);
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

                            v.addEventListener('loadeddata', function() {
                                var self = this;
                                setTimeout(function() {
                                    if (self.paused && self.readyState >= 2) {
                                        self.play().catch(function(){});
                                    }
                                }, 800);
                            });

                            if (v.readyState >= 2 && v.paused) {
                                v.play().catch(function(){});
                            }
                        }

                        var iframes = document.querySelectorAll('iframe');
                        for (var i = 0; i < iframes.length; i++) {
                            var iframe = iframes[i];
                            if (iframe._autoPlayBinded) continue;

                            var src = (iframe.src || '').toLowerCase();
                            var esPlayer = src.includes('player') || src.includes('embed') ||
                                src.includes('server') || src.includes('stream') ||
                                src.includes('vimeo') || src.includes('voe') ||
                                src.includes('goodstream') || src.includes('dood');

                            if (esPlayer && iframe.contentWindow) {
                                iframe._autoPlayBinded = true;
                                try {
                                    iframe.contentWindow.postMessage(JSON.stringify({action: 'play'}), '*');
                                } catch(e) {}
                            }
                        }
                    } catch(e) {}
                }

                function cerrarPopupsConfirmacion() {
                    try {
                        var dialogs = document.querySelectorAll('.modal, .popup, .dialog, [class*="modal"], [class*="popup"], [class*="dialog"], [role="dialog"]');
                        for (var i = dialogs.length - 1; i >= 0; i--) {
                            var d = dialogs[i];
                            var texto = d.textContent.toLowerCase();
                            var esConfirmacion = texto.includes('¿desea') || texto.includes('do you want') ||
                                texto.includes('ver ahora') || texto.includes('watch now') ||
                                texto.includes('close') || texto.includes('cerrar') ||
                                texto.includes('accept') || texto.includes('aceptar');
                            if (esConfirmacion) {
                                var btnCerrar = d.querySelector('button[class*="close"], .close, [class*="cerrar"], [aria-label="Close"]');
                                if (btnCerrar) btnCerrar.click();
                                else d.style.display = 'none';
                            }
                        }

                        var overlays = document.querySelectorAll('[class*="overlay"], [class*="backdrop"]');
                        for (var i = overlays.length - 1; i >= 0; i--) {
                            var o = overlays[i];
                            var tieneModal = o.querySelector('.modal, .popup, .dialog, [class*="modal"]');
                            if (tieneModal) {
                                var btnClose = o.querySelector('.close, button');
                                if (btnClose) btnClose.click();
                                else o.style.display = 'none';
                            }
                        }
                    } catch(e) {}
                }

                autoSeleccionarServidor();
                autoReproducir();
                cerrarPopupsConfirmacion();

                var obs = new MutationObserver(function() {
                    setTimeout(autoSeleccionarServidor, 300);
                    setTimeout(autoReproducir, 500);
                    setTimeout(cerrarPopupsConfirmacion, 200);
                });
                obs.observe(document.body || document.documentElement, {childList: true, subtree: true});

                setTimeout(autoSeleccionarServidor, 1000);
                setTimeout(autoReproducir, 1500);
                setTimeout(autoSeleccionarServidor, 3000);
                setTimeout(autoReproducir, 4000);
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
                if (controlsVisible) {
                    hideControls()
                    return true
                }
                if (webView.canGoBack()) {
                    webView.goBack()
                    return true
                }
            }
            KeyEvent.KEYCODE_DPAD_UP -> {
                if (controlsVisible) {
                    btnPlayPause.requestFocus()
                    return true
                }
                webView.evaluateJavascript("window.__tvNav && window.__tvNav.move('up')", null)
                return true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                webView.evaluateJavascript(
                    """
                    (function() {
                        var v = document.querySelector('video');
                        if (v && !v.paused && !controlsVisible) {
                            showTvControls();
                        } else {
                            window.__tvNav && window.__tvNav.move('down');
                        }
                    })();
                    """.trimIndent(), null
                )
                showControls()
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
                webView.evaluateJavascript(
                    """
                    (function() {
                        var v = document.querySelector('video');
                        if (v && !v.paused) { v.pause(); }
                        else if (v && v.paused) { v.play(); }
                        else { window.__tvNav && window.__tvNav.click(); }
                    })();
                    """.trimIndent(), null
                )
                updatePlayPauseButton()
                return true
            }
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                webView.evaluateJavascript(
                    "var v=document.querySelector('video');if(v){if(v.paused)v.play();else v.pause();}", null
                )
                updatePlayPauseButton()
                return true
            }
            KeyEvent.KEYCODE_MEDIA_PLAY -> {
                webView.evaluateJavascript("var v=document.querySelector('video');if(v)v.play();", null)
                return true
            }
            KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                webView.evaluateJavascript("var v=document.querySelector('video');if(v)v.pause();", null)
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
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}
