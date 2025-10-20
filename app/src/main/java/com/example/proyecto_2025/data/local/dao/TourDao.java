package com.example.proyecto_2025.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.proyecto_2025.data.local.entity.TourEntity;

import java.util.List;

@Dao
public interface TourDao {
    @Query("SELECT * FROM tour ORDER BY inicioUtc ASC")
    List<TourEntity> getAll();

    @Query("SELECT * FROM tour WHERE id=:id")
    TourEntity find(long id);

    @Query("SELECT * FROM tour WHERE empresaId=:empresaId ORDER BY inicioUtc ASC")
    List<TourEntity> byEmpresa(long empresaId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(TourEntity t);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<TourEntity> list);

    @Query("SELECT COUNT(*) FROM tour")
    int count();
}
