package com.fabio.mensalidades;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        web = new WebView(this);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);          // salva as assinaturas (localStorage)
        s.setAllowFileAccess(true);
        s.setTextZoom(100);
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                applyTheme();
            }
        });
        web.setWebChromeClient(new WebChromeClient());
        // A página avisa qual tema está em uso (automático, claro ou escuro escolhido nas configurações)
        web.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void setDark(final boolean dark) {
                runOnUiThread(() -> applySystemBars(dark));
            }
        }, "AndroidBridge");
        setContentView(web);
        applySystemBars(isDark());
        if (savedInstanceState != null) web.restoreState(savedInstanceState);
        else web.loadUrl("file:///android_asset/index.html?theme=" + (isDark() ? "dark" : "light"));
    }

    private boolean isDark() {
        int mode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return mode == Configuration.UI_MODE_NIGHT_YES;
    }

    /** Cores da barra de status / navegação de acordo com o tema do sistema. */
    @SuppressWarnings("deprecation")
    private void applySystemBars(boolean dark) {
        Window w = getWindow();
        w.setStatusBarColor(Color.parseColor(dark ? "#0A1538" : "#0E1C4A"));
        w.setNavigationBarColor(Color.parseColor(dark ? "#0F1838" : "#FFFFFF"));
        web.setBackgroundColor(Color.parseColor(dark ? "#070D22" : "#F3F5FB"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            View decor = w.getDecorView();
            int flags = decor.getSystemUiVisibility();
            if (dark) flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            else flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            decor.setSystemUiVisibility(flags);
        }
    }

    private void applyTheme() {
        web.evaluateJavascript("window.setAppTheme && window.setAppTheme('" + (isDark() ? "dark" : "light") + "')", null);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        applyTheme();   // a página decide o tema final e chama setDark() de volta
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        web.saveState(outState);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBackPressed() {
        // Se o formulário estiver aberto, o botão voltar só fecha ele.
        web.evaluateJavascript("window.handleBack ? window.handleBack() : false", value -> {
            if (!"true".equals(value)) MainActivity.super.onBackPressed();
        });
    }
}
