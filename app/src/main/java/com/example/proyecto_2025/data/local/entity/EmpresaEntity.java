package com.example.proyecto_2025.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "empresa")
public class EmpresaEntity {
    @PrimaryKey(autoGenerate = true) public long id;
    public String nombre;
    public String ciudad;
    public String pais;
    public String logoKey; // opcional (drawable/uri futuro)
}
