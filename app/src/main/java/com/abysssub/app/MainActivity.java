package com.abysssub.app;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
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

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
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
import androidx.fragment.app.FragmentActivity;

import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthException;
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

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class MainActivity extends FragmentActivity {
    private static final int REQ_NOTIF = 77;
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

    private static String errCode(Exception e) {
        if (e instanceof FirebaseAuthException) return ((FirebaseAuthException) e).getErrorCode();
        if (e instanceof FirebaseNetworkException) return "ERROR_NETWORK";
        return e == null ? "ERROR_UNKNOWN" : String.valueOf(e.getMessage());
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

        // ------------------------------------------------ e-mail e senha
        @JavascriptInterface
        public void emailAuth(final String mode, final String email, final String password) {
            if (!cloudOk) return;
            runOnUiThread(() -> {
                Task<AuthResult> t = "signup".equals(mode)
                        ? auth.createUserWithEmailAndPassword(email, password)
                        : auth.signInWithEmailAndPassword(email, password);
                t.addOnCompleteListener(MainActivity.this, task -> {
                    FirebaseUser u = auth.getCurrentUser();
                    if (task.isSuccessful() && u != null) {
                        js("onAuth", q(userJson(u)));
                        startListener();
                    } else {
                        js("onEmailError", q(errCode(task.getException())));
                    }
                });
            });
        }

        @JavascriptInterface
        public void resetPassword(final String email) {
            if (!cloudOk) return;
            auth.sendPasswordResetEmail(email).addOnCompleteListener(task ->
                    js("onResetSent", task.isSuccessful() + "," + q(task.isSuccessful() ? "" : errCode(task.getException()))));
        }

        // ------------------------------------------------ bloqueio (digital / bloqueio do celular)
        @JavascriptInterface
        public String lockAvailable(String mode) {
            int r = BiometricManager.from(MainActivity.this).canAuthenticate(authenticators(mode));
            if (r == BiometricManager.BIOMETRIC_SUCCESS) return "ok";
            if (r == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) return "none";
            return "nohw";
        }

        @JavascriptInterface
        public void authenticate(final String mode) {
            runOnUiThread(() -> startAuth(mode));
        }

        @JavascriptInterface
        public String hashPin(String text) {
            try {
                byte[] d = MessageDigest.getInstance("SHA-256").digest(text.getBytes("UTF-8"));
                StringBuilder sb = new StringBuilder();
                for (byte b : d) sb.append(String.format("%02x", b));
                return sb.toString();
            } catch (Exception e) {
                return "";
            }
        }

        // ------------------------------------------------ notificações
        @JavascriptInterface
        public String notifPermission() {
            if (Build.VERSION.SDK_INT >= 33
                    && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return "denied";
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            return nm != null && nm.areNotificationsEnabled() ? "granted" : "blocked";
        }

        @JavascriptInterface
        public void requestNotifPermission() {
            runOnUiThread(() -> {
                if (Build.VERSION.SDK_INT >= 33
                        && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQ_NOTIF);
                } else {
                    js("onNotifPermission", q(notifPermission()));
                }
            });
        }

        @JavascriptInterface
        public void openAppSettings() {
            runOnUiThread(() -> startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", getPackageName(), null))));
        }

        @JavascriptInterface
        public void setReminders(String json) {
            Reminders.save(getApplicationContext(), json);
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

    // ---------------------------------------------------------------- bloqueio

    private static int authenticators(String mode) {
        return "bio".equals(mode)
                ? BiometricManager.Authenticators.BIOMETRIC_STRONG
                : BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL;
    }

    private void startAuth(String mode) {
        boolean bio = "bio".equals(mode);
        BiometricPrompt.PromptInfo.Builder info = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Desbloquear AbyssSub")
                .setSubtitle(bio ? "Use sua digital" : "Use o bloqueio do celular")
                .setAllowedAuthenticators(authenticators(mode));
        if (bio) info.setNegativeButtonText("Cancelar");
        BiometricPrompt prompt = new BiometricPrompt(this, mainExec, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                js("onUnlock", "true,\"\",0");
            }

            @Override
            public void onAuthenticationError(int code, @NonNull CharSequence msg) {
                js("onUnlock", "false," + q(msg.toString()) + "," + code);
            }
        });
        try {
            prompt.authenticate(info.build());
        } catch (Exception e) {
            js("onUnlock", "false," + q(e.getMessage()) + ",-1");
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (requestCode == REQ_NOTIF) {
            boolean ok = results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED;
            js("onNotifPermission", q(ok ? "granted" : "denied"));
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
            if (!"true".equals(value)) defaultBack();
        });
    }

    @SuppressWarnings("deprecation")
    private void defaultBack() {
        super.onBackPressed();
    }
}
