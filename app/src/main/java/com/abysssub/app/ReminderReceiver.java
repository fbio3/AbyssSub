package com.abysssub.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** Recebe o alarme do lembrete e o aviso de que o celular reiniciou / o app foi atualizado. */
public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Em todos os casos: mostra o que já venceu e agenda o próximo aviso.
        Reminders.fireDue(context);
    }
}
