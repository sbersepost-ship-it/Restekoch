package de.restekoch.app;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/** Zeigt die Restekoch-App (assets/index.html) bildschirmfüllend an. */
public class MainActivity extends Activity {

    private static final int FILE_REQUEST = 42;
    private WebView web;
    private ValueCallback<Uri[]> fileCallback;
    private Uri cameraUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#1E7848"));
        getWindow().setNavigationBarColor(Color.WHITE);

        web = new WebView(this);
        setContentView(web);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);   // Kühlschrank, Einkaufsliste und Favoriten speichern
        s.setAllowFileAccess(true);
        s.setTextZoom(100);

        // Links nach außen im Browser öffnen, die App selbst bleibt in der WebView.
        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if ("file".equals(uri.getScheme())) return false;
                try { startActivity(new Intent(Intent.ACTION_VIEW, uri)); } catch (Exception ignored) { }
                return true;
            }
        });

        // Foto-Auswahl für "Kühlschrank scannen".
        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
                if (fileCallback != null) fileCallback.onReceiveValue(null);
                fileCallback = callback;
                try {
                    // Auswahl: Kamera oder Galerie
                    Intent pick = params.createIntent();
                    Intent chooser = Intent.createChooser(pick, "Foto vom Kühlschrank");
                    Intent camera = cameraIntent();
                    if (camera != null) chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{camera});
                    startActivityForResult(chooser, FILE_REQUEST);
                } catch (Exception e) {
                    fileCallback = null;
                    return false;
                }
                return true;
            }
        });

        web.loadUrl("file:///android_asset/index.html");
    }

    /** Kamera-Intent, das Foto landet in der Galerie (Bilder/Restekoch). */
    private Intent cameraIntent() {
        try {
            ContentValues v = new ContentValues();
            v.put(MediaStore.Images.Media.DISPLAY_NAME, "restekoch_" + System.currentTimeMillis() + ".jpg");
            v.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            v.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Restekoch");
            cameraUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, v);
            if (cameraUri == null) return null;
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            i.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            return i;
        } catch (Exception e) {
            cameraUri = null;
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_REQUEST && fileCallback != null) {
            Uri[] result = null;
            if (resultCode == RESULT_OK) {
                if (data != null && (data.getData() != null || data.getClipData() != null)) {
                    result = WebChromeClient.FileChooserParams.parseResult(resultCode, data);
                } else if (cameraUri != null) {
                    result = new Uri[]{cameraUri};   // Foto von der Kamera
                }
            }
            if (result == null && cameraUri != null) {
                try { getContentResolver().delete(cameraUri, null, null); } catch (Exception ignored) { }
            } else if (result != null && cameraUri != null && !result[0].equals(cameraUri)) {
                try { getContentResolver().delete(cameraUri, null, null); } catch (Exception ignored) { }
            }
            cameraUri = null;
            fileCallback.onReceiveValue(result);
            fileCallback = null;
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Zurück-Taste: erst ein offenes Fenster (Rezept, Scanner) schließen, sonst die App beenden.
    @Override
    @SuppressWarnings("deprecation")
    public void onBackPressed() {
        String js = "(function(){var b=document.querySelector('[role=dialog] button[aria-label=\"Schließen\"]');"
                + "if(b){b.click();return true;}return false;})()";
        web.evaluateJavascript(js, result -> {
            if (!"true".equals(result)) finish();
        });
    }
}
