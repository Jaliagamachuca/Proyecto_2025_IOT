package com.example.proyecto_2025.data.local.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.proyecto_2025.data.local.dao.EmpresaDao;
import com.example.proyecto_2025.data.local.dao.NotifLogDao;
import com.example.proyecto_2025.data.local.dao.ReservaDao;
import com.example.proyecto_2025.data.local.dao.TourDao;
import com.example.proyecto_2025.data.local.entity.EmpresaEntity;
import com.example.proyecto_2025.data.local.entity.NotifLogEntity;
import com.example.proyecto_2025.data.local.entity.ReservaEntity;
import com.example.proyecto_2025.data.local.entity.TourEntity;

@Database(entities = {
        EmpresaEntity.class, TourEntity.class, ReservaEntity.class, NotifLogEntity.class
}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract EmpresaDao empresaDao();
    public abstract TourDao tourDao();
    public abstract ReservaDao reservaDao();
    public abstract NotifLogDao notifLogDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase get(Context c) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(c.getApplicationContext(),
                                    AppDatabase.class, "toursapp.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
