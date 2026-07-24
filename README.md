# Manglar TV — App WebView para Android TV

App mínima que, al abrirse, carga directamente:
`https://manglarpelis.manglar.fun/?title=tv-60059`

## Qué incluye

- `MainActivity.kt`: WebView configurado para TV (JavaScript, autoplay de video,
  soporte de pantalla completa HTML5 que usan la mayoría de reproductores web,
  navegación con el D-Pad, botón atrás funcional).
- Aparece en la fila de apps de Android TV (categoría `LEANBACK_LAUNCHER`).
- Banner e ícono de ejemplo (reemplázalos por los tuyos en
  `app/src/main/res/drawable/banner.png` y `app/src/main/res/mipmap-xhdpi/ic_launcher.png`).

## Cómo compilarla

1. Abre la carpeta `ManglarTV` en **Android Studio** (Archivo → Abrir).
2. Deja que Gradle sincronice (descargará las dependencias automáticamente,
   necesitas internet la primera vez).
3. Conecta tu Android TV por ADB o usa un emulador de Android TV:
   ```
   adb connect <ip-de-tu-tv>:5555
   ```
4. Ejecuta con el botón ▶ (Run) o genera el APK:
   `Build → Build Bundle(s) / APK(s) → Build APK(s)`
5. Instala el APK generado en tu TV:
   ```
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

## Para que se abra automáticamente al encender la TV

Esto la app en sí no lo puede forzar por políticas de Android (Google no permite
que apps normales se auto-abran al encender, salvo que reemplaces el *launcher*
del sistema). Tienes 3 opciones reales:

1. **Más simple**: deja la app instalada y dale "Abrir automáticamente" usando
   una app de terceros como *Google Play* → ninguna lo permite de forma nativa,
   pero puedes usar la app **"AutoStart"** o configurarla como app por defecto
   del control remoto (algunos TV Box con Android TV Box genérico sí lo permiten
   en Ajustes → Apps → Inicio automático).
2. **Reemplazar el Home/Launcher**: convierte esta actividad en el *launcher*
   por defecto agregando en el manifest la categoría `HOME` además de
   `LEANBACK_LAUNCHER`, y luego el usuario selecciona esta app como "Inicio" en
   Ajustes del sistema. (Puedo agregártelo si me confirmas que quieres
   reemplazar el launcher completo de la TV, ya que eso oculta la interfaz
   normal de Android TV).
3. **Cajas Android genéricas (no certificadas por Google)**: en Ajustes suele
   haber una opción "Boot app" / "Startup app" donde eliges directamente esta
   app — no requiere cambios de código.

## Notas técnicas

- `usesCleartextTraffic="true"` y `mixedContentMode = MIXED_CONTENT_ALWAYS_ALLOW`
  están activados porque muchos reproductores embebidos en sitios de streaming
  mezclan recursos http/https; si tu sitio es 100% https puedes quitarlos por
  seguridad.
- Si el sitio usa el elemento `<video>` de HTML5 en modo pantalla completa,
  el `WebChromeClient.onShowCustomView` ya está implementado para que se vea
  correctamente en pantalla completa en la TV.
- Si el reproductor usa Flash o formatos muy antiguos, no funcionará (WebView
  moderno no soporta Flash); pero la gran mayoría de sitios actuales usan
  HTML5 video o iframes de terceros, que sí funcionan.
