package com.example.proyecto_2025.notif;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.proyecto_2025.R;
import com.example.proyecto_2025.data.local.db.AppDatabase;
import com.example.proyecto_2025.data.local.entity.NotifLogEntity;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class NotificationHelper {

    private NotificationHelper() {} // util class

    public static final String CHANNEL_ID = "tour_channel";

    /** Crear canal y pedir permiso (Android 13+) */
    public static void ensureChannel(Activity a) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(
                    CHANNEL_ID, "Notificaciones de Tours", NotificationManager.IMPORTANCE_HIGH);
            ((NotificationManager) a.getSystemService(Context.NOTIFICATION_SERVICE))
                    .createNotificationChannel(ch);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(a, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(a,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }
    }

    /** Enviar notificación inmediata + registrar en log */
    public static void pushNow(Context c, String title, String body, Long reservaId, String type) {
        // Si no hay permiso en Android 13+, salir silenciosamente
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(c, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        NotificationCompat.Builder b = new NotificationCompat.Builder(c, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat.from(c).notify((int) System.currentTimeMillis(), b.build());

        // Guardar en log (opcional)
        Executors.newSingleThreadExecutor().execute(() -> {
            NotifLogEntity n = new NotifLogEntity();
            n.reservaId = reservaId;
            n.type = type;
            n.title = title;
            n.body = body;
            n.createdUtc = System.currentTimeMillis();
            n.delivered = true;
            AppDatabase.get(c).notifLogDao().insert(n);
        });
    }

    /** Programar recordatorio minutosAntes del inicio */
    public static void scheduleReminder(Context c, long reservaId, long inicioUtc, int minutosAntes, String type) {
        long triggerAt = inicioUtc - minutosAntes * 60L * 1000L;
        long delay = Math.max(0, triggerAt - System.currentTimeMillis());

        Data data = new Data.Builder()
                .putLong("reservaId", reservaId)
                .putString("title", "Recordatorio de tour")
                .putString("body", "Faltan " + (minutosAntes >= 60 ? (minutosAntes / 60 + " h")
                        : minutosAntes + " min") + " para tu tour.")
                .putString("type", type)
                .build();

        OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(ReminderWorker.class)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build();

        WorkManager.getInstance(c).enqueue(req);
    }
}
