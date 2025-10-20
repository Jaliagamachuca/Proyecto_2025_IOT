package com.example.proyecto_2025.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.proyecto_2025.data.local.entity.EmpresaEntity;

import java.util.List;

@Dao
public interface EmpresaDao {
    @Query("SELECT * FROM empresa ORDER BY nombre")
    List<EmpresaEntity> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(EmpresaEntity e);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<EmpresaEntity> list);

    @Query("SELECT COUNT(*) FROM empresa")
    int count();
}
