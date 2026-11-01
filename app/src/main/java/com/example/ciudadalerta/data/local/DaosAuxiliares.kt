package com.example.ciudadalerta.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(usuario: UsuarioEntity): Long

    @Query("SELECT * FROM usuario WHERE correo = :correo LIMIT 1")
    suspend fun obtenerPorCorreo(correo: String): UsuarioEntity?
}

@Dao
interface MultimediaDao {
    @Insert
    suspend fun insertar(multimedia: MultimediaEntity): Long

    @Query("SELECT * FROM multimedia WHERE reporte_id = :reporteId")
    fun obtenerPorReporte(reporteId: Int): Flow<List<MultimediaEntity>>

    @Query("DELETE FROM multimedia WHERE id = :id")
    suspend fun eliminar(id: Int)
}

@Dao
interface NotificacionDao {
    @Insert
    suspend fun insertar(notificacion: NotificacionEntity): Long

    @Query("SELECT * FROM notificacion ORDER BY fecha DESC")
    fun obtenerTodas(): Flow<List<NotificacionEntity>>

    @Query("SELECT COUNT(*) FROM notificacion WHERE leida = 0")
    fun contarNoLeidas(): Flow<Int>

    @Query("UPDATE notificacion SET leida = 1 WHERE id = :id")
    suspend fun marcarLeida(id: Int)
}

@Dao
interface HistorialDao {
    @Insert
    suspend fun insertar(historial: HistorialEstatusEntity): Long

    @Query("SELECT * FROM historial_estatus WHERE reporte_id = :reporteId ORDER BY fecha DESC")
    fun obtenerPorReporte(reporteId: Int): Flow<List<HistorialEstatusEntity>>
}