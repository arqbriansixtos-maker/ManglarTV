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

    // Puente JS -> Android: cuando el botón de play está DENTRO de un iframe de otro
    // dominio (el reproductor externo), JS de la página principal no puede hacer click
    // "de adentro" por la política de mismo origen. Este puente recibe coordenadas y
    // simula un toque físico real en la pantalla, que sí llega al contenido del iframe.
    inner class PlayerBridge {
        @JavascriptInterface
        fun tapAt(x: Float, y: Float) {
            runOnUiThread { simularToqueReal(x, y) }
        }
    }

    private fun simularToqueReal(cssX: Float, cssY: Float) {
        // Las coordenadas llegan en píxeles CSS del viewport; se convierten a píxeles
        // reales de pantalla con la densidad del dispositivo.
        val densidad = resources.displayMetrics.density
        val x = cssX * densidad
        val y = cssY * densidad

        val downTime = SystemClock.uptimeMillis()
        val down = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, x, y, 0)
        val up = MotionEvent.obtain(downTime, downTime + 80, MotionEvent.ACTION_UP, x, y, 0)
        webView.dispatchTouchEvent(down)
        webView.dispatchTouchEvent(up)
        down.recycle()
        up.recycle()
    }

    // Puente JS -> Android: nos avisa cada vez que la URL cambia dentro del sitio,
    // incluso cuando el sitio usa history.replaceState() (que NO genera una entrada
    // nueva en el historial nativo del WebView, y por eso canGoBack() no lo detecta).
    inner class HistorialBridge {
        @JavascriptInterface
        fun reportarUrl(url: String) {
            runOnUiThread { registrarUrlEnHistorial(url) }
        }
    }

    private fun registrarUrlEnHistorial(url: String) {
        if (pilaHistorial.isEmpty() || pilaHistorial.last() != url) {
            pilaHistorial.add(url)
            if (pilaHistorial.size > 50) pilaHistorial.removeAt(0)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Evita que el protector de pantalla / suspensión del TV se active mientras
        // la app está al frente (independiente de la configuración del sistema).
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        activarPantallaCompleta()
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        fullscreenContainer = findViewById(R.id.fullscreenContainer)
        progressBar = findViewById(R.id.progressBar)

        configurarWebView()
        webView.loadUrl(targetUrl)
    }

    // El modo inmersivo a veces se "suelta" solo cuando la ventana recupera el foco
    // (por ejemplo al volver de un diálogo del sistema); lo reforzamos aquí.
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) activarPantallaCompleta()
    }

    @Suppress("DEPRECATION")
    private fun activarPantallaCompleta() {
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )
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
                webView.evaluateJavascript("window.__tvNav && window.__tvNav.move('$dir')", null)
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
                webView.evaluateJavascript("window.__tvNav && window.__tvNav.stop('$dir')", null)
                return true
            }
        }

        // FIX: antes este bloque solo hacía .focus() sobre el elemento bajo el cursor
        // y nunca llamaba a window.__tvNav.click() (la función que sí dispara el click real).
        // Por eso el cursor se movía pero OK/Enter no hacía nada.
        if (code == KeyEvent.KEYCODE_DPAD_CENTER || code == KeyEvent.KEYCODE_ENTER) {
            if (event.action == KeyEvent.ACTION_DOWN) {
                webView.evaluateJavascript("window.__tvNav && window.__tvNav.click()", null)
                return true
            }
            if (event.action == KeyEvent.ACTION_UP) {
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
                    // Respaldo para sitios tipo SPA (replaceState): usamos nuestro
                    // propio historial de URLs para volver a la página anterior.
                    if (pilaHistorial.size > 1) {
                        pilaHistorial.removeAt(pilaHistorial.size - 1) // quita la URL actual
                        val anterior = pilaHistorial.last()
                        webView.loadUrl(anterior)
                        return true
                    }
                    // Ya estamos en la raíz de la navegación: exige doble back para
                    // salir de verdad, así se evitan salidas accidentales de la app.
                    val ahora = System.currentTimeMillis()
                    if (ahora - ultimoBackPressTime < 2000) {
                        // segundo back dentro de 2s: deja pasar el evento (sale de la app)
                    } else {
                        ultimoBackPressTime = ahora
                        Toast.makeText(this, "Presiona atrás de nuevo para salir", Toast.LENGTH_SHORT).show()
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
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        settings.mediaPlaybackRequiresUserGesture = false
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.userAgentString = settings.userAgentString + " ManglarTV/1.0"

        settings.javaScriptCanOpenWindowsAutomatically = false
        settings.setSupportMultipleWindows(false)

        // Puente para simular toques reales cuando el play esté dentro de un iframe externo.
        webView.addJavascriptInterface(PlayerBridge(), "AndroidBridge")
        // Puente para llevar nuestro propio historial de navegación (ver HistorialBridge).
        webView.addJavascriptInterface(HistorialBridge(), "HistorialBridge")

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
                inyectarSeguimientoDeHistorial()
                inyectarAjusteDeAnchoHorizontal()
                inyectarCorreccionDeBarrasConScroll()
                inyectarAjusteReproductor()
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

    private fun inyectarSeguimientoDeHistorial() {
        val js = """
            (function() {
                if (window.__historialInstalado) return;
                window.__historialInstalado = true;

                function reportar() {
                    if (window.HistorialBridge) {
                        window.HistorialBridge.reportarUrl(location.href);
                    }
                }

                var origPush = history.pushState;
                history.pushState = function() {
                    origPush.apply(history, arguments);
                    reportar();
                };

                var origReplace = history.replaceState;
                history.replaceState = function() {
                    origReplace.apply(history, arguments);
                    reportar();
                };

                window.addEventListener('popstate', reportar);
                window.addEventListener('hashchange', reportar);

                reportar();
            })();
        """.trimIndent()
        webView.evaluateJavascript(js, null)
    }

    private fun inyectarCorreccionDeBarrasConScroll() {
        val js = """
            (function() {
                if (window.__correccionBarrasInstalada) return;
                window.__correccionBarrasInstalada = true;

                var ajustando = false;

                function corregir() {
                    try {
                        var candidatos = document.querySelectorAll('body, body *');
                        for (var i = 0; i < candidatos.length; i++) {
                            var el = candidatos[i];
                            if (el._anchoLocalAjustado) continue;
                            if (el.clientWidth < 50) continue;
                            if (el.scrollWidth <= el.clientWidth + 3) continue;

                            var estilo = window.getComputedStyle(el);
                            var desbordaX = estilo.overflowX === 'auto' || estilo.overflowX === 'scroll';
                            if (!desbordaX) continue;

                            el._anchoLocalAjustado = true;

                            if (estilo.display.indexOf('flex') !== -1) {
                                el.style.flexWrap = 'wrap';
                                el.style.overflowX = 'visible';
                                el.style.overflowY = 'visible';
                            } else {
                                el.style.whiteSpace = 'normal';
                                el.style.overflowX = 'visible';
                            }
                        }
                    } catch (e) {}
                }

                function solicitarCorreccion() {
                    if (ajustando) return;
                    ajustando = true;
                    requestAnimationFrame(function() {
                        corregir();
                        ajustando = false;
                    });
                }

                solicitarCorreccion();

                var observer = new MutationObserver(solicitarCorreccion);
                observer.observe(document.body || document.documentElement, {
                    childList: true,
                    subtree: true
                });

                setTimeout(solicitarCorreccion, 500);
                setTimeout(solicitarCorreccion, 1500);
                setTimeout(solicitarCorreccion, 3000);
                setTimeout(solicitarCorreccion, 6000);
            })();
        """.trimIndent()
        webView.evaluateJavascript(js, null)
    }

    private fun inyectarAjusteDeAnchoHorizontal() {
        val js = """
            (function() {
                if (window.__ajusteEscalaInstalado) return;
                window.__ajusteEscalaInstalado = true;

                var ajustando = false;

                function ajustarEscala() {
                    try {
                        // Reiniciar antes de medir, para no arrastrar una escala previa
                        // y terminar encogiendo el contenido cada vez más.
                        document.body.style.transformOrigin = '0 0';
                        document.body.style.transform = 'none';
                        document.body.style.width = '';
                        document.documentElement.style.overflowX = 'hidden';

                        var anchoContenido = document.documentElement.scrollWidth;
                        var anchoPantalla = window.innerWidth;

                        if (anchoContenido > anchoPantalla + 3) {
                            var escala = anchoPantalla / anchoContenido;
                            document.body.style.transform = 'scale(' + escala + ')';
                            document.body.style.width = anchoContenido + 'px';
                        }
                    } catch (e) {}
                }

                function solicitarAjuste() {
                    if (ajustando) return;
                    ajustando = true;
                    requestAnimationFrame(function() {
                        ajustarEscala();
                        ajustando = false;
                    });
                }

                solicitarAjuste();
                window.addEventListener('resize', solicitarAjuste);

                var observer = new MutationObserver(solicitarAjuste);
                observer.observe(document.body || document.documentElement, {
                    childList: true,
                    subtree: true
                });

                setTimeout(solicitarAjuste, 500);
                setTimeout(solicitarAjuste, 1500);
                setTimeout(solicitarAjuste, 3000);
                setTimeout(solicitarAjuste, 6000);
            })();
        """.trimIndent()
        webView.evaluateJavascript(js, null)
    }

    private fun inyectarAjusteReproductor() {
        val js = """
            (function() {
                if (window.__ajusteReproductorInstalado) return;
                window.__ajusteReproductorInstalado = true;

                var ajustando = false;

                function ajustarReproductor() {
                    try {
                        var iframes = document.querySelectorAll('iframe');
                        for (var i = 0; i < iframes.length; i++) {
                            var ifr = iframes[i];
                            if (ifr._reproductorAjustado) continue;
                            
                            var src = (ifr.src || '').toLowerCase();
                            var esReproductor = src.indexOf('vimeo') !== -1 || 
                                src.indexOf('vidhide') !== -1 || 
                                src.indexOf('streamwish') !== -1 || 
                                src.indexOf('voe') !== -1 ||
                                src.indexOf('player') !== -1;
                            
                            if (esReproductor) {
                                ifr._reproductorAjustado = true;
                                ifr.style.width = '100%';
                                ifr.style.height = 'auto';
                                ifr.style.minHeight = '400px';
                                ifr.style.display = 'block';
                                ifr.style.maxWidth = '100vw';
                                if (ifr.parentNode) {
                                    ifr.parentNode.style.width = '100%';
                                    ifr.parentNode.style.overflowX = 'hidden';
                                }
                            }
                        }

                        var videos = document.querySelectorAll('video');
                        for (var i = 0; i < videos.length; i++) {
                            var v = videos[i];
                            if (v._videoAjustado) continue;
                            v._videoAjustado = true;
                            v.style.width = '100%';
                            v.style.height = 'auto';
                            v.style.display = 'block';
                            v.style.maxWidth = '100vw';
                            if (v.parentNode) {
                                v.parentNode.style.width = '100%';
                                v.parentNode.style.overflowX = 'hidden';
                            }
                        }

                        var playerContainers = document.querySelectorAll(
                            '[class*="player"], [class*="video"], [id*="player"], [id*="video"], ' +
                            '.video-container, .player-container, #player'
                        );
                        for (var i = 0; i < playerContainers.length; i++) {
                            var container = playerContainers[i];
                            if (container._containerAjustado) continue;
                            container._containerAjustado = true;
                            container.style.width = '100%';
                            container.style.maxWidth = '100vw';
                            container.style.overflowX = 'hidden';
                            container.style.overflowY = 'auto';
                        }
                    } catch(e) {}
                }

                function solicitarAjuste() {
                    if (ajustando) return;
                    ajustando = true;
                    requestAnimationFrame(function() {
                        ajustarReproductor();
                        ajustando = false;
                    });
                }

                solicitarAjuste();

                var observer = new MutationObserver(solicitarAjuste);
                observer.observe(document.body || document.documentElement, {
                    childList: true,
                    subtree: true
                });

                setTimeout(solicitarAjuste, 300);
                setTimeout(solicitarAjuste, 800);
                setTimeout(solicitarAjuste, 1500);
                setTimeout(solicitarAjuste, 2500);
                setTimeout(solicitarAjuste, 4000);
                setInterval(solicitarAjuste, 3000);
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
                }

                function doClick() {
                    cursor.style.display = 'none';
                    var el = document.elementFromPoint(cx, cy);
                    cursor.style.display = '';
                    if (!el) return;

                    // Si lo que hay bajo el cursor es directamente un IFRAME (reproductor
                    // externo), el click de JS no puede llegar "adentro" por seguridad del
                    // navegador. En ese caso usamos un toque real simulado por Android.
                    if (el.tagName === 'IFRAME') {
                        if (window.AndroidBridge) {
                            window.AndroidBridge.tapAt(cx, cy);
                        }
                        return;
                    }

                    var target = null;
                    var c = el;
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

                    if (target) {
                        // Los campos de texto (buscador, etc.) necesitan un toque FÍSICO real
                        // para que el navegador abra el teclado en pantalla; un click simulado
                        // por JS enfoca el campo pero no dispara el teclado (restricción de
                        // seguridad de los navegadores contra popups de teclado no solicitados).
                        var esCampoDeTexto = (target.tagName === 'INPUT' &&
                                ['text','search','email','tel','password','url','number'].indexOf((target.type || 'text').toLowerCase()) !== -1) ||
                            target.tagName === 'TEXTAREA' ||
                            target.isContentEditable;

                        if (esCampoDeTexto && window.AndroidBridge) {
                            window.AndroidBridge.tapAt(cx, cy);
                        } else {
                            var opts = {bubbles: true, clientX: cx, clientY: cy, cancelable: true};
                            target.dispatchEvent(new MouseEvent('mousedown', opts));
                            target.dispatchEvent(new MouseEvent('mouseup', opts));
                            target.dispatchEvent(new MouseEvent('click', opts));
                        }
                    } else if (window.AndroidBridge) {
                        // Sin ancestro clicable identificable: probablemente es contenido
                        // dibujado dentro de un iframe cross-origin que no detectamos como
                        // tal directamente (a veces el iframe está debajo de una capa
                        // transparente). Toque real como último recurso.
                        window.AndroidBridge.tapAt(cx, cy);
                    }

                    var videos = document.querySelectorAll('video');
                    for (var i = 0; i < videos.length; i++) {
                        if (videos[i].paused) {
                            videos[i].play().catch(function(){});
                        }
                    }

                    var iframes = document.querySelectorAll('iframe');
                    for (var i = 0; i < iframes.length; i++) {
                        var src = (iframes[i].src || '').toLowerCase();
                        if (src.indexOf('vimeo') !== -1 || src.indexOf('player') !== -1 ||
                            src.indexOf('vidhide') !== -1 || src.indexOf('streamwish') !== -1 ||
                            src.indexOf('voe') !== -1) {
                            try {
                                iframes[i].contentWindow.postMessage(JSON.stringify({method:'play'}), '*');
                                iframes[i].contentWindow.postMessage('{"event":"play"}', '*');
                            } catch(e) {}
                        }
                    }

                    var spaceEvt = new KeyboardEvent('keydown', {key:' ', code:'Space', keyCode:32, which:32, bubbles:true});
                    document.dispatchEvent(spaceEvt);

                    var r = document.createElement('div');
                    r.style.cssText = 'position:fixed !important;z-index:2147483647 !important;pointer-events:none;width:50px;height:50px;border:2px solid #fff;border-radius:50%;transform:translate(-50%,-50%);left:' + cx + 'px;top:' + cy + 'px;opacity:1;transition:opacity 0.3s;';
                    document.documentElement.appendChild(r);
                    setTimeout(function() { r.style.opacity = '0'; }, 10);
                    setTimeout(function() { r.remove(); }, 350);
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

                            // Respaldo: el postMessage solo funciona si el reproductor de adentro
                            // lo escucha. Muchos NO lo hacen y solo reaccionan a un click/touch real
                            // sobre su botón de play. Como JS no puede "ver" adentro de un iframe de
                            // otro dominio, le pedimos a Android un toque físico real sobre el centro
                            // del iframe (una sola vez por iframe, con un pequeño retraso para no
                            // pausar un video que ya esté reproduciéndose).
                            if (!ifr._autoTapIntentado) {
                                var r = ifr.getBoundingClientRect();
                                if (r.width > 100 && r.height > 100) {
                                    ifr._autoTapIntentado = true;
                                    var cx2 = r.left + r.width / 2;
                                    var cy2 = r.top + r.height / 2;
                                    setTimeout(function(x, y) {
                                        return function() {
                                            if (window.AndroidBridge) window.AndroidBridge.tapAt(x, y);
                                        };
                                    }(cx2, cy2), 1400);
                                }
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
