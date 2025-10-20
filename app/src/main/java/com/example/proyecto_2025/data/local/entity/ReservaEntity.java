package com.example.proyecto_2025.data.local.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "reserva",
        foreignKeys = @ForeignKey(entity = TourEntity.class,
                parentColumns = "id", childColumns = "tourId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("tourId"), @Index("estado"), @Index("userEmail")})
public class ReservaEntity {
    @PrimaryKey(autoGenerate = true) public long id;
    public long tourId;
    public String userEmail;

    // Estados: PROXIMA | EN_CURSO | COMPLETADA | CANCELADA
    public String estado;

    public long creadaUtc;
    public Long checkInUtc;   // nullable
    public Long checkOutUtc;  // nullable
    public double total;

    // Futuro (Firebase)
    public String remoteId;    // nullable
    public String syncState;   // PENDING | SYNCED | DIRTY

    // QRs simulados
    public String qrInicio;
    public String qrFin;
}
