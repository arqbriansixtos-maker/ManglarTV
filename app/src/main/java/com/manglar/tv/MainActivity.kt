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
