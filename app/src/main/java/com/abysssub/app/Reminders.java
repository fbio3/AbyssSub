package com.abysssub.app;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Lembretes de cobrança.
 * A página calcula a lista de avisos (horário, título, texto) e manda para cá.
 * Fica sempre UM alarme agendado: o do próximo aviso. Quando ele toca, mostra os avisos
 * vencidos e agenda o seguinte. Depois de reiniciar o celular, o receiver agenda de novo.
 */
public final class Reminders {
    static final String ACTION_FIRE = "com.abysssub.app.REMINDER";
    private static final String PREFS = "abysssub_reminders";
    private static final String KEY = "list";
    private static final String CHANNEL = "cobrancas";
    private static final long LATE_LIMIT = 36L * 60 * 60 * 1000;   // não avisa coisas muito antigas

    private Reminders() { }

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    private static JSONArray load(Context c) {
        try {
            return new JSONArray(prefs(c).getString(KEY, "[]"));
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    static void save(Context c, String json) {
        JSONArray arr;
        try {
            arr = new JSONArray(json);
        } catch (Exception e) {
            arr = new JSONArray();
        }
        prefs(c).edit().putString(KEY, arr.toString()).apply();
        scheduleNext(c);
    }

    private static PendingIntent alarmIntent(Context c) {
        Intent i = new Intent(c, ReminderReceiver.class).setAction(ACTION_FIRE);
        return PendingIntent.getBroadcast(c, 0, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    static void scheduleNext(Context c) {
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;
        PendingIntent pi = alarmIntent(c);
        am.cancel(pi);
        JSONArray arr = load(c);
        long now = System.currentTimeMillis(), next = Long.MAX_VALUE;
        for (int i = 0; i < arr.length(); i++) {
            long at = arr.optJSONObject(i) == null ? 0 : arr.optJSONObject(i).optLong("at");
            if (at > 0 && at < next) next = at;
        }
        if (next == Long.MAX_VALUE) return;
        long when = Math.max(next, now + 2000);
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, when, pi);
    }

    /** Mostra os avisos que já chegaram na hora e agenda o próximo. */
    static void fireDue(Context c) {
        JSONArray arr = load(c), keep = new JSONArray();
        long now = System.currentTimeMillis();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject o = arr.optJSONObject(i);
            if (o == null) continue;
            long at = o.optLong("at");
            if (at <= now + 60_000) {
                if (now - at < LATE_LIMIT) show(c, o);
            } else {
                keep.put(o);
            }
        }
        prefs(c).edit().putString(KEY, keep.toString()).apply();
        scheduleNext(c);
    }

    private static void show(Context c, JSONObject o) {
        if (Build.VERSION.SDK_INT >= 33
                && c.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return;
        NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;
        if (Build.VERSION.SDK_INT >= 26 && nm.getNotificationChannel(CHANNEL) == null) {
            NotificationChannel ch = new NotificationChannel(CHANNEL, "Lembretes de cobrança", NotificationManager.IMPORTANCE_DEFAULT);
            ch.setDescription("Avisos antes do dia de cada assinatura");
            nm.createNotificationChannel(ch);
        }
        int id = o.optString("id", String.valueOf(o.optLong("at"))).hashCode();
        Intent open = new Intent(c, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(c, id, open, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Notification.Builder b = Build.VERSION.SDK_INT >= 26 ? new Notification.Builder(c, CHANNEL) : new Notification.Builder(c);
        b.setSmallIcon(R.drawable.ic_stat_notify)
                .setContentTitle(o.optString("title", "Cobrança chegando"))
                .setContentText(o.optString("body", ""))
                .setStyle(new Notification.BigTextStyle().bigText(o.optString("body", "")))
                .setColor(0xFFE3AD58)
                .setAutoCancel(true)
                .setContentIntent(pi);
        nm.notify(id, b.build());
    }
}
