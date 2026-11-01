package com.example.ciudadalerta.data.repository

import com.example.ciudadalerta.data.local.HistorialDao
import com.example.ciudadalerta.data.local.HistorialEstatusEntity
import com.example.ciudadalerta.data.local.MultimediaDao
import com.example.ciudadalerta.data.local.NotificacionDao
import com.example.ciudadalerta.data.local.ReporteDao
import com.example.ciudadalerta.data.local.ReporteEntity

class ReporteRepository(
    private val reporteDao: ReporteDao,
    private val multimediaDao: MultimediaDao,
    private val notificacionDao: NotificacionDao,
    private val historialDao: HistorialDao
) {

    // Lecturas (la interfaz siempre lee de la base local)
    fun obtenerReportes() = reporteDao.obtenerTodos()
    fun obtenerReporte(id: Int) = reporteDao.obtenerPorId(id)
    fun historial(reporteId: Int) = historialDao.obtenerPorReporte(reporteId)
    fun multimedia(reporteId: Int) = multimediaDao.obtenerPorReporte(reporteId)
    fun notificaciones() = notificacionDao.obtenerTodas()
    fun notificacionesSinLeer() = notificacionDao.contarNoLeidas()

    // Escrituras
    suspend fun crearReporte(reporte: ReporteEntity): Long {
        val id = reporteDao.insertar(reporte)
        historialDao.insertar(
            HistorialEstatusEntity(reporteId = id.toInt(), estatus = reporte.estatus)
        )
        // TODO (Parte 2): enviar a la API con Retrofit y marcar como sincronizado
        return id
    }

    suspend fun actualizarReporte(reporte: ReporteEntity) {
        reporteDao.actualizar(reporte.copy(sincronizado = false))
        // TODO (Parte 2): enviar el cambio a la API
    }

    suspend fun eliminarReporte(id: Int) {
        reporteDao.eliminarPorId(id) // multimedia, notificaciones e historial se borran en cascada
        // TODO (Parte 2): eliminar también en la API
    }

    suspend fun marcarNotificacionLeida(id: Int) = notificacionDao.marcarLeida(id)
}