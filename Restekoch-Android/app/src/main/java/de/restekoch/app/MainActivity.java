package de.restekoch.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
                    startActivityForResult(params.createIntent(), FILE_REQUEST);
                } catch (Exception e) {
                    fileCallback = null;
                    return false;
                }
                return true;
            }
        });

        web.loadUrl("file:///android_asset/index.html");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_REQUEST && fileCallback != null) {
            fileCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
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
