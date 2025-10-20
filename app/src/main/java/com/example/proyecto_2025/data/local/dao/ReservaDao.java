package com.example.proyecto_2025.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.proyecto_2025.data.local.entity.ReservaEntity;

import java.util.List;

@Dao
public interface ReservaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ReservaEntity r);

    @Update
    void update(ReservaEntity r);

    @Query("SELECT * FROM reserva WHERE id=:id")
    ReservaEntity findById(long id);

    @Query("SELECT * FROM reserva WHERE userEmail=:email AND estado IN ('PROXIMA','EN_CURSO') ORDER BY creadaUtc DESC")
    List<ReservaEntity> proximas(String email);

    @Query("SELECT * FROM reserva WHERE userEmail=:email AND estado='COMPLETADA' ORDER BY checkOutUtc DESC")
    List<ReservaEntity> completadas(String email);

    @Query("SELECT COUNT(*) FROM reserva WHERE userEmail=:email AND estado='COMPLETADA'")
    int countCompletadas(String email);

    @Query("SELECT IFNULL(SUM(total),0) FROM reserva WHERE userEmail=:email AND estado='COMPLETADA'")
    double totalGastado(String email);
}
