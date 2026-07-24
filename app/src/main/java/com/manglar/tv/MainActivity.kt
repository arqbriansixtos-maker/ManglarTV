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

    // 1) Página principal: sin el parámetro ?title=..., directo a la home
    private val targetUrl = "https://manglarpelis.manglar.fun/"

    // Dominio(s) que sí consideramos "de confianza" para navegar dentro del WebView.
    // Todo lo que no coincida con esto se trata como posible redirect/pop-under y se bloquea.
    private val allowedHostSuffixes = listOf(
        "manglar.fun"
        // agrega aquí, si hace falta, dominios de CDNs de video legítimos que use el sitio,
        // ej: "some-video-cdn.com". Si el video no carga tras esto, revisa el log de
        // shouldOverrideUrlLoading para ver qué host está siendo bloqueado.
    )

    // 2) Lista negra de dominios de ads/tracking conocidos (capa extra, además del bloqueo estructural)
    private val adHostFragments = listOf(
        "doubleclick.net", "googlesyndication.com", "google-analytics.com",
        "googletagmanager.com", "googletagservices.com", "adservice.google",
        "propellerads.com", "propeller-ads.com", "popads.net", "poper.pro",
        "exoclick.com", "juicyads.com", "adsterra.com", "adnxs.com",
        "taboola.com", "outbrain.com", "revcontent.com", "mgid.com",
        "clickadu.com", "hilltopads.net", "adcash.com", "yllix.com",
        "trafficjunky.net", "adskeeper.co.uk", "smartadserver.com",
        "onclickmax.com", "adsco.re", "media.net"
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

        // --- BLOQUEO ESTRUCTURAL DE POP-UNDERS (enfoque B) ---
        // Evita que JS abra ventanas/pestañas nuevas automáticamente (el mecanismo #1 de pop-under ads)
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
                inyectarNavegacionTV()
            }

            // Bloquea navegación a dominios fuera de la lista blanca (redirects de ads)
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val host = request?.url?.host ?: return false
                val esConfiable = allowedHostSuffixes.any { host == it || host.endsWith(".$it") }
                return if (esConfiable) {
                    false // permite navegar (retorno false = "no lo intercepto")
                } else {
                    true // bloquea: no carga esa URL en el WebView
                }
            }

            // Bloqueo por lista negra de recursos (banners, scripts de tracking, etc.)
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                val host = request?.url?.host?.lowercase() ?: return super.shouldInterceptRequest(view, request)
                val esAd = adHostFragments.any { host.contains(it) }
                return if (esAd) {
                    WebResourceResponse("text/plain", "utf-8", ByteArrayInputStream(ByteArray(0)))
                } else {
                    super.shouldInterceptRequest(view, request)
                }
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

            // Refuerzo: si algo intenta abrir una "ventana nueva" (típico de pop-unders),
            // no la creamos.
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

    // 3) Navegación con control remoto (D-Pad): inyecta un pequeño script de
    // "spatial navigation" para que arriba/abajo/izquierda/derecha muevan el foco
    // entre elementos clicables de la página, y OK/Enter haga click en el enfocado.
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
