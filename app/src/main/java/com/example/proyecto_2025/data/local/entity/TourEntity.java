package com.example.proyecto_2025.data.local.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "tour",
        foreignKeys = @ForeignKey(entity = EmpresaEntity.class,
                parentColumns = "id", childColumns = "empresaId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("empresaId"), @Index("inicioUtc")})
public class TourEntity {
    @PrimaryKey(autoGenerate = true) public long id;
    public long empresaId;
    public String nombre;
    public String descripcion;
    public long inicioUtc;
    public long finUtc;
    public double precio;
    public float rating;
    public String portadaKey; // drawable/uri
}
