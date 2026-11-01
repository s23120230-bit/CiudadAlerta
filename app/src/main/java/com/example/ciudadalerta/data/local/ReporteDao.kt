package com.example.ciudadalerta.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReporteDao {
    @Query("SELECT * FROM reporte ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<ReporteEntity>>

    @Query("SELECT * FROM reporte WHERE id = :id")
    fun obtenerPorId(id: Int): Flow<ReporteEntity?>

    @Query("SELECT * FROM reporte WHERE sincronizado = 0")
    suspend fun obtenerPendientes(): List<ReporteEntity>

    @Insert
    suspend fun insertar(reporte: ReporteEntity): Long

    @Update
    suspend fun actualizar(reporte: ReporteEntity)

    @Query("DELETE FROM reporte WHERE id = :id")
    suspend fun eliminarPorId(id: Int)

    @Query("UPDATE reporte SET sincronizado = 1, remote_id = :remoteId WHERE id = :id")
    suspend fun marcarSincronizado(id: Int, remoteId: String)
}