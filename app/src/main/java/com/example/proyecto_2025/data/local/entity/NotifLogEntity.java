package com.example.proyecto_2025.data.local.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "notif_log", indices = @Index("reservaId"))
public class NotifLogEntity {
    @PrimaryKey(autoGenerate = true) public long id;
    public Long reservaId;  // nullable
    public String type;     // CONFIRMACION, REMINDER_24H, etc.
    public String title;
    public String body;
    public long createdUtc;
    public boolean delivered;
}
