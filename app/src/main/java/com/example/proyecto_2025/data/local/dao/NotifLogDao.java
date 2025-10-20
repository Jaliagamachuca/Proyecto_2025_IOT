package com.example.proyecto_2025.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.proyecto_2025.data.local.entity.NotifLogEntity;

@Dao
public interface NotifLogDao {
    @Insert
    long insert(NotifLogEntity n);
}
