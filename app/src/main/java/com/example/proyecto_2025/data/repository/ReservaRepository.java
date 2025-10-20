package com.example.proyecto_2025.data.repository;

import android.content.Context;

import com.example.proyecto_2025.data.local.db.AppDatabase;
import com.example.proyecto_2025.data.local.entity.ReservaEntity;
import com.example.proyecto_2025.data.local.entity.TourEntity;
import com.example.proyecto_2025.notif.NotificationHelper;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ReservaRepository {

    private final Context ctx;
    private final AppDatabase db;

    public ReservaRepository(Context ctx) {
        this.ctx = ctx.getApplicationContext();
        this.db = AppDatabase.get(this.ctx);
    }

    public long crearReserva(long tourId, String email) {
        try {
            Future<Long> f = Executors.newSingleThreadExecutor().submit((Callable<Long>) () -> {
                TourEntity t = db.tourDao().find(tourId);
                if (t == null) return -1L;

                ReservaEntity r = new ReservaEntity();
                r.tourId = t.id;
                r.userEmail = email;
                r.estado = "PROXIMA";
                r.creadaUtc = System.currentTimeMillis();
                r.total = t.precio;
                r.qrInicio = "QR-" + email + "-" + t.id + "-INICIO";
                r.qrFin = "QR-" + email + "-" + t.id + "-FIN";
                r.syncState = "PENDING";
                long id = db.reservaDao().insert(r);

                // Notificación inmediata
                NotificationHelper.pushNow(ctx, "Reserva confirmada",
                        "Tu tour " + t.nombre + " ha sido confirmado.", id, "CONFIRMACION");

                // Recordatorios -24h y -2h
                NotificationHelper.scheduleReminder(ctx, id, t.inicioUtc, 24 * 60, "REMINDER_24H");
                NotificationHelper.scheduleReminder(ctx, id, t.inicioUtc, 2 * 60, "REMINDER_2H");
                return id;
            });
            return f.get();
        } catch (ExecutionException | InterruptedException e) {
            return -1L;
        }
    }

    public void registrarCheckIn(long reservaId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            ReservaEntity r = db.reservaDao().findById(reservaId);
            if (r == null) return;
            r.estado = "EN_CURSO";
            r.checkInUtc = System.currentTimeMillis();
            r.syncState = "DIRTY";
            db.reservaDao().update(r);
            NotificationHelper.pushNow(ctx, "Check-in registrado", "¡Buen viaje!", reservaId, "CHECKIN");
        });
    }

    public void registrarCheckOut(long reservaId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            ReservaEntity r = db.reservaDao().findById(reservaId);
            if (r == null) return;
            r.estado = "COMPLETADA";
            r.checkOutUtc = System.currentTimeMillis();
            r.syncState = "DIRTY";
            db.reservaDao().update(r);
            NotificationHelper.pushNow(ctx, "Check-out registrado", "Gracias por viajar con nosotros.", reservaId, "CHECKOUT");
            NotificationHelper.pushNow(ctx, "Cobro realizado", "Se procesó el pago de S/ " + r.total, reservaId, "COBRO");
        });
    }

    // Lecturas sincronas simples (para pantalla). En producción úsalo con ViewModel/LiveData.
    public List<ReservaEntity> proximas(String email) {
        try {
            return Executors.newSingleThreadExecutor().submit(() ->
                    db.reservaDao().proximas(email)).get();
        } catch (Exception e) { return java.util.Collections.emptyList(); }
    }

    public List<ReservaEntity> completadas(String email) {
        try {
            return Executors.newSingleThreadExecutor().submit(() ->
                    db.reservaDao().completadas(email)).get();
        } catch (Exception e) { return java.util.Collections.emptyList(); }
    }

    public int countCompletadas(String email) {
        try {
            return Executors.newSingleThreadExecutor().submit(() ->
                    db.reservaDao().countCompletadas(email)).get();
        } catch (Exception e) { return 0; }
    }

    public double totalGastado(String email) {
        try {
            return Executors.newSingleThreadExecutor().submit(() ->
                    db.reservaDao().totalGastado(email)).get();
        } catch (Exception e) { return 0; }
    }
}
