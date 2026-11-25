package com.example.ciudadalerta.data.repository

import com.example.ciudadalerta.data.local.HistorialEstatusEntity
import com.example.ciudadalerta.data.local.MultimediaEntity
import com.example.ciudadalerta.data.local.NotificacionEntity
import com.example.ciudadalerta.data.local.ReporteEntity
import kotlinx.coroutines.flow.Flow

interface ReporteRepository {
    fun obtenerReportes(): Flow<List<ReporteEntity>>
    fun obtenerReportesPorUsuario(idUsuario: String): Flow<List<ReporteEntity>>
    fun obtenerReporte(id: Int): Flow<ReporteEntity?>
    fun historial(reporteId: Int): Flow<List<HistorialEstatusEntity>>
    fun multimedia(reporteId: Int): Flow<List<MultimediaEntity>>
    fun notificaciones(): Flow<List<NotificacionEntity>>
    fun notificacionesSinLeer(): Flow<Int>
    fun usuarioActualId(): String

    suspend fun crearReporte(reporte: ReporteEntity): Long
    suspend fun actualizarReporte(reporte: ReporteEntity)
    suspend fun eliminarLocal(id: Int): String?
    suspend fun marcarNotificacionLeida(id: Int)
    suspend fun primeraFoto(reporteId: Int): String?
    suspend fun guardarFoto(reporteId: Int, ruta: String?)
    suspend fun sincronizarPendientes()
    suspend fun recuperarDeApi(idUsuario: String): Boolean
    suspend fun eliminarRemoto(remoteId: String?)
}