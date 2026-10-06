package com.example.ciudadalerta.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReporteDao {
    @Query("SELECT * FROM reportes ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<ReporteEntity>>

    @Query("SELECT * FROM reportes WHERE id = :id")
    fun obtenerPorId(id: Int): Flow<ReporteEntity?>

    @Insert
    suspend fun insertar(reporte: ReporteEntity)
}