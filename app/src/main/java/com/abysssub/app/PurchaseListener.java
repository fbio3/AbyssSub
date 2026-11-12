package com.abysssub.app;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Lê as notificações de compra dos apps de banco (só com a permissão do usuário) e guarda
 * apenas as que parecem uma compra/cobrança com valor em R$. Nada é enviado para fora do celular:
 * o app abre essa lista e sugere as assinaturas para o usuário confirmar.
 */
public class PurchaseListener extends NotificationListenerService {
    private static final String PREFS = "abysssub_purchases";
    private static final String KEY = "list";
    private static final int MAX = 400;

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        try {
            if (sbn == null || getPackageName().equals(sbn.getPackageName())) return;
            Notification n = sbn.getNotification();
            Bundle ex = n == null ? null : n.extras;
            if (ex == null) return;
            CharSequence title = ex.getCharSequence(Notification.EXTRA_TITLE);
            CharSequence text = ex.getCharSequence(Notification.EXTRA_TEXT);
            CharSequence big = ex.getCharSequence(Notification.EXTRA_BIG_TEXT);
            String body = (title == null ? "" : title + " · ") + (big != null ? big : (text == null ? "" : text));
            if (!looksLikePurchase(body)) return;
            String app = sbn.getPackageName();
            try {
                Object ai = ex.get("android.appInfo");
                if (ai instanceof ApplicationInfo) app = String.valueOf(getPackageManager().getApplicationLabel((ApplicationInfo) ai));
            } catch (Exception ignored) { }
            store(this, app, sbn.getPostTime(), body);
        } catch (Exception ignored) { }
    }

    static boolean looksLikePurchase(String s) {
        if (s == null || !s.matches("(?s).*R\\$\\s?-?\\s?\\d.*")) return false;
        String l = s.toLowerCase(Locale.ROOT);
        if (l.contains("recebid") || l.contains("recebeu") || l.contains("estorn") || l.contains("reembols")) return false;
        return l.contains("compra") || l.contains("aprovad") || l.contains("débito") || l.contains("debito")
                || l.contains("pagamento") || l.contains("cobran") || l.contains("assinatura") || l.contains("pix enviado")
                || l.contains("transfer");
    }

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    static synchronized JSONArray load(Context c) {
        try {
            return new JSONArray(prefs(c).getString(KEY, "[]"));
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    static synchronized void store(Context c, String app, long at, String text) {
        try {
            JSONArray arr = load(c);
            String t = text.length() > 300 ? text.substring(0, 300) : text;
            for (int i = Math.max(0, arr.length() - 15); i < arr.length(); i++) {      // ignora a mesma notificação repetida
                JSONObject o = arr.optJSONObject(i);
                if (o != null && t.equals(o.optString("text")) && Math.abs(at - o.optLong("at")) < 180_000) return;
            }
            JSONObject o = new JSONObject();
            o.put("at", at);
            o.put("app", app);
            o.put("text", t);
            arr.put(o);
            while (arr.length() > MAX) arr.remove(0);
            prefs(c).edit().putString(KEY, arr.toString()).apply();
        } catch (Exception ignored) { }
    }

    static synchronized void clear(Context c) {
        prefs(c).edit().remove(KEY).apply();
    }
}
