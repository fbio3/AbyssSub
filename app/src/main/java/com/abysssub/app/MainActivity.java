package com.abysssub.app;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.credentials.ClearCredentialStateRequest;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.ClearCredentialException;
import androidx.credentials.exceptions.GetCredentialCancellationException;
import androidx.credentials.exceptions.GetCredentialException;

import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Source;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class MainActivity extends Activity {
    private WebView web;

    // Firebase (fica desligado se o app for compilado sem o google-services.json)
    private boolean cloudOk = false;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private CredentialManager credMan;
    private ListenerRegistration listener;
    private final Handler main = new Handler(Looper.getMainLooper());
    private final Executor mainExec = main::post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cloudOk = initFirebase();

        web = new WebView(this);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);          // dados locais (localStorage)
        s.setAllowFileAccess(true);
        s.setTextZoom(100);
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                applyTheme();
                if (cloudOk && auth.getCurrentUser() != null) startListener();
            }
        });
        web.setWebChromeClient(new WebChromeClient());
        web.addJavascriptInterface(new Bridge(), "AndroidBridge");
        setContentView(web);
        applySystemBars(isDark());
        if (savedInstanceState != null) web.restoreState(savedInstanceState);
        else web.loadUrl("file:///android_asset/index.html?theme=" + (isDark() ? "dark" : "light"));
    }

    // ---------------------------------------------------------------- Firebase

    private boolean initFirebase() {
        try {
            if (FirebaseApp.getApps(this).isEmpty() && FirebaseApp.initializeApp(this) == null) return false;
            if (webClientId() == null) return false;
            auth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
            credMan = CredentialManager.create(this);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Gerado automaticamente a partir do google-services.json. */
    private String webClientId() {
        int id = getResources().getIdentifier("default_web_client_id", "string", getPackageName());
        return id == 0 ? null : getString(id);
    }

    private DocumentReference userDoc(FirebaseUser u) {
        return db.collection("users").document(u.getUid());
    }

    private static String userJson(FirebaseUser u) {
        try {
            JSONObject o = new JSONObject();
            o.put("uid", u.getUid());
            o.put("name", u.getDisplayName() == null ? "" : u.getDisplayName());
            o.put("email", u.getEmail() == null ? "" : u.getEmail());
            o.put("photo", u.getPhotoUrl() == null ? "" : u.getPhotoUrl().toString());
            return o.toString();
        } catch (Exception e) {
            return "";
        }
    }

    /** Chama uma função global da página (sempre na thread principal). */
    private void js(final String fn, final String args) {
        main.post(() -> web.evaluateJavascript("window." + fn + "&&window." + fn + "(" + args + ")", null));
    }

    private static String q(String s) {
        return JSONObject.quote(s == null ? "" : s);
    }

    private void authError(String msg, boolean cancelled) {
        js("onAuthError", q(msg) + "," + cancelled);
    }

    private void startSignIn() {
        GetSignInWithGoogleOption option = new GetSignInWithGoogleOption.Builder(webClientId()).build();
        GetCredentialRequest request = new GetCredentialRequest.Builder().addCredentialOption(option).build();
        credMan.getCredentialAsync(this, request, new CancellationSignal(), mainExec,
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse response) {
                        handleCredential(response.getCredential());
                    }

                    @Override
                    public void onError(GetCredentialException e) {
                        authError(String.valueOf(e.getMessage()), e instanceof GetCredentialCancellationException);
                    }
                });
    }

    private void handleCredential(Credential credential) {
        if (!(credential instanceof CustomCredential)) {
            authError("Tipo de login não reconhecido", false);
            return;
        }
        try {
            GoogleIdTokenCredential google = GoogleIdTokenCredential.createFrom(credential.getData());
            AuthCredential fb = GoogleAuthProvider.getCredential(google.getIdToken(), null);
            auth.signInWithCredential(fb).addOnCompleteListener(this, task -> {
                FirebaseUser u = auth.getCurrentUser();
                if (task.isSuccessful() && u != null) {
                    js("onAuth", q(userJson(u)));
                    startListener();
                } else {
                    Exception e = task.getException();
                    authError(e != null ? e.getMessage() : "falha no login", false);
                }
            });
        } catch (Exception e) {
            authError(e.getMessage(), false);
        }
    }

    /** Escuta o documento do usuário: chega na abertura e sempre que outro aparelho salvar. */
    private void startListener() {
        FirebaseUser u = auth.getCurrentUser();
        if (u == null) return;
        stopListener();
        listener = userDoc(u).addSnapshotListener((snap, e) -> {
            if (e != null) {
                js("onCloudError", q(e.getMessage()));
                return;
            }
            if (snap == null || snap.getMetadata().hasPendingWrites()) return;
            deliver(snap);
        });
    }

    private void stopListener() {
        if (listener != null) {
            listener.remove();
            listener = null;
        }
    }

    private void deliver(DocumentSnapshot snap) {
        boolean cache = snap.getMetadata().isFromCache();
        if (!snap.exists()) {
            js("onCloudData", "null,0," + cache);
            return;
        }
        Long up = snap.getLong("updatedAt");
        js("onCloudData", q(snap.getString("data")) + "," + (up == null ? 0 : up) + "," + cache);
    }

    /** Ponte usada pela página (index.html). */
    private class Bridge {
        @JavascriptInterface
        public void setDark(final boolean dark) {
            runOnUiThread(() -> applySystemBars(dark));
        }

        @JavascriptInterface
        public void copyText(final String text) {
            runOnUiThread(() -> {
                ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                if (cm != null) cm.setPrimaryClip(ClipData.newPlainText("AbyssSub backup", text));
            });
        }

        @JavascriptInterface
        public String readClipboard() {
            try {
                ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = cm == null ? null : cm.getPrimaryClip();
                if (clip == null || clip.getItemCount() == 0) return "";
                CharSequence t = clip.getItemAt(0).coerceToText(MainActivity.this);
                return t == null ? "" : t.toString();
            } catch (Exception e) {
                return "";
            }
        }

        @JavascriptInterface
        public boolean cloudAvailable() {
            return cloudOk;
        }

        @JavascriptInterface
        public String getUser() {
            if (!cloudOk) return "";
            FirebaseUser u = auth.getCurrentUser();
            return u == null ? "" : userJson(u);
        }

        @JavascriptInterface
        public void signIn() {
            if (!cloudOk) return;
            runOnUiThread(MainActivity.this::startSignIn);
        }

        @JavascriptInterface
        public void signOut() {
            if (!cloudOk) return;
            runOnUiThread(() -> {
                stopListener();
                auth.signOut();
                credMan.clearCredentialStateAsync(new ClearCredentialStateRequest(), new CancellationSignal(), mainExec,
                        new CredentialManagerCallback<Void, ClearCredentialException>() {
                            @Override
                            public void onResult(Void unused) { }

                            @Override
                            public void onError(ClearCredentialException e) { }
                        });
            });
        }

        @JavascriptInterface
        public void cloudSave(String json, String updatedAt) {
            if (!cloudOk) return;
            FirebaseUser u = auth.getCurrentUser();
            if (u == null) return;
            final long at;
            try {
                at = Long.parseLong(updatedAt);
            } catch (NumberFormatException e) {
                return;
            }
            Map<String, Object> doc = new HashMap<>();
            doc.put("data", json);
            doc.put("updatedAt", at);
            doc.put("savedAt", FieldValue.serverTimestamp());
            doc.put("app", "AbyssSub");
            userDoc(u).set(doc)
                    .addOnSuccessListener(v -> js("onCloudSaved", String.valueOf(at)))
                    .addOnFailureListener(e -> js("onCloudError", q(e.getMessage())));
        }

        @JavascriptInterface
        public void cloudRefresh() {
            if (!cloudOk) return;
            FirebaseUser u = auth.getCurrentUser();
            if (u == null) return;
            userDoc(u).get(Source.SERVER)
                    .addOnSuccessListener(MainActivity.this::deliver)
                    .addOnFailureListener(e -> js("onCloudError", q(e.getMessage())));
        }
    }

    // ---------------------------------------------------------------- tema

    private boolean isDark() {
        int mode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return mode == Configuration.UI_MODE_NIGHT_YES;
    }

    /** Cores da barra de status / navegação de acordo com o tema em uso. */
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

    @Override
    protected void onDestroy() {
        stopListener();
        super.onDestroy();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBackPressed() {
        // Se algum painel estiver aberto, o botão voltar só fecha ele.
        web.evaluateJavascript("window.handleBack ? window.handleBack() : false", value -> {
            if (!"true".equals(value)) MainActivity.super.onBackPressed();
        });
    }
}
