package com.example.proyecto_2025.notif;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class ReminderWorker extends Worker {
    public ReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull @Override
    public Result doWork() {
        Data d = getInputData();
        String title = d.getString("title");
        String body  = d.getString("body");
        long reservaId = d.getLong("reservaId", -1);
        String type = d.getString("type");
        NotificationHelper.pushNow(getApplicationContext(), title, body, reservaId, type);
        return Result.success();
    }
}
