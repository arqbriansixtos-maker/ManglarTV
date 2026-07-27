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
            }
        }

        return super.dispatchKeyEvent(event)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configurarWebView() {
        val settings = webView.settings
        settings.apply {
            javaScriptEnabled = true
            databaseEnabled = true
            domStorageEnabled = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            useWideViewPort = true
            loadWithOverviewMode = true
            cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            mediaPlaybackRequiresUserGesture = false
            allowFileAccess = true
            allowContentAccess = true
        }

        webView.addJavascriptInterface(PlayerBridge(), "AndroidBridge")
        webView.addJavascriptInterface(HistorialBridge(), "HistorialBridge")

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
                progressBar.visibility = View.GONE
            }

            override fun onHideCustomView() {
                if (customView == null) return
                customView!!.visibility = View.GONE
                fullscreenContainer.removeView(customView)
                fullscreenContainer.visibility = View.GONE
                customViewCallback?.onCustomViewHidden()
                customView = null
                progressBar.visibility = View.VISIBLE
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progressBar.progress = newProgress
                if (newProgress == 100) {
                    progressBar.visibility = View.GONE
                    inyectarNavegacionTV()
                    inyectarAjusteReproductor()
                    inyectarBloqueadorAnuncios()
                } else {
                    progressBar.visibility = View.VISIBLE
                }
            }
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                if (request == null) return null

                val url = request.url.toString().lowercase()

                // Bloquear por host
                for (adHost in adHostFragments) {
                    if (url.contains(adHost)) {
                        return WebResourceResponse("text/plain", "utf-8", ByteArrayInputStream("".toByteArray()))
                    }
                }

                // Bloquear por patrón de URL
                for (pattern in adUrlPatterns) {
                    if (url.contains(pattern)) {
                        return WebResourceResponse("text/plain", "utf-8", ByteArrayInputStream("".toByteArray()))
                    }
                }

                return null
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                registrarUrlEnHistorial(url ?: "")
            }
        }
    }

    private fun inyectarAjusteReproductor() {
        val js = """
            (function() {
                // Aplicar zoom del 80% a toda la página para que entre en pantalla
                document.documentElement.style.zoom = '80%';
                document.body.style.zoom = '80%';
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
                    // Compensar el zoom del 80% para obtener coordenadas correctas
                    var zoomLevel = window.devicePixelRatio || 1;
                    var bodyZoom = window.getComputedStyle(document.body).zoom || 1;
                    var totalZoom = zoomLevel * bodyZoom;
                    
                    var adjustedCx = cx / totalZoom;
                    var adjustedCy = cy / totalZoom;

                    cursor.style.display = 'none';
                    var el = document.elementFromPoint(adjustedCx, adjustedCy);
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
                            var opts = {bubbles: true, clientX: adjustedCx, clientY: adjustedCy, cancelable: true};
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

    private fun inyectarBloqueadorAnuncios() {
        val js = """
            (function() {
                var removed = 0;
                var selectors = ${adCssSelectors.joinToString(",", "[", "]") { "\"$it\"" }};
                selectors.forEach(function(sel) {
                    try {
                        var els = document.querySelectorAll(sel);
                        els.forEach(function(el) {
                            if (el && el.parentNode) {
                                el.parentNode.removeChild(el);
                                removed++;
                            }
                        });
                    } catch(e) {}
                });
            })();
        """.trimIndent()
        webView.evaluateJavascript(js, null)
    }
}
