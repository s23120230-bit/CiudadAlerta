package com.example.ciudadalerta.data.repository

import com.example.ciudadalerta.data.local.HistorialDao
import com.example.ciudadalerta.data.local.HistorialEstatusEntity
import com.example.ciudadalerta.data.local.MultimediaDao
import com.example.ciudadalerta.data.local.NotificacionDao
import com.example.ciudadalerta.data.local.NotificacionEntity
import com.example.ciudadalerta.data.local.ReporteDao
import com.example.ciudadalerta.data.local.ReporteEntity
import com.example.ciudadalerta.data.local.SesionManager
import com.example.ciudadalerta.data.remote.ReporteApi
import com.example.ciudadalerta.data.remote.actualizadoCon
import com.example.ciudadalerta.data.remote.toDto
import com.example.ciudadalerta.data.remote.toEntity
import kotlinx.coroutines.CancellationException

class ReporteRepository(
    private val reporteDao: ReporteDao,
    private val multimediaDao: MultimediaDao,
    private val notificacionDao: NotificacionDao,
    private val historialDao: HistorialDao,
    private val api: ReporteApi,
    private val sesion: SesionManager
) {

    // ---------- Lecturas (la interfaz siempre lee de la base local) ----------
    fun obtenerReportes() = reporteDao.obtenerTodos()
    fun obtenerReporte(id: Int) = reporteDao.obtenerPorId(id)
    fun historial(reporteId: Int) = historialDao.obtenerPorReporte(reporteId)
    fun multimedia(reporteId: Int) = multimediaDao.obtenerPorReporte(reporteId)
    fun notificaciones() = notificacionDao.obtenerTodas()
    fun notificacionesSinLeer() = notificacionDao.contarNoLeidas()

    // ---------- Escrituras locales ----------
    suspend fun crearReporte(reporte: ReporteEntity): Long {
        val id = reporteDao.insertar(reporte.copy(usuarioId = sesion.usuarioActual()))
        historialDao.insertar(
            HistorialEstatusEntity(reporteId = id.toInt(), estatus = reporte.estatus)
        )
        return id
    }

    suspend fun actualizarReporte(reporte: ReporteEntity) {
        reporteDao.actualizar(reporte.copy(sincronizado = false))
    }

    // Borra en local y devuelve el id remoto (si lo tenía) para borrarlo también en la API
    suspend fun eliminarLocal(id: Int): String? {
        val remoteId = reporteDao.obtenerUno(id)?.remoteId
        reporteDao.eliminarPorId(id) // multimedia, notificaciones e historial se borran en cascada
        return remoteId
    }

    suspend fun eliminarRemoto(remoteId: String?) {
        if (remoteId == null) return
        try {
            api.eliminar(remoteId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Sin conexión: el reporte se borró local, pero seguirá en el servidor
            // (mejora pendiente: guardar las eliminaciones pendientes)
        }
    }

    suspend fun marcarNotificacionLeida(id: Int) = notificacionDao.marcarLeida(id)

    // ---------- Red (Retrofit) ----------

    // Envía a la API todos los reportes pendientes (nuevos o editados)
    suspend fun sincronizarPendientes() {
        reporteDao.obtenerPendientes().forEach { sincronizarUno(it) }
    }

    private suspend fun sincronizarUno(local: ReporteEntity): Boolean = try {
        val remoteId = local.remoteId
        val remoto = if (remoteId == null) {
            api.crear(local.toDto())
        } else {
            api.actualizar(remoteId, local.toDto())
        }
        val idFinal = remoto.id ?: remoteId
        if (idFinal == null) {
            false
        } else {
            reporteDao.marcarSincronizado(local.id, idFinal)
            true
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        false
    }

    // Descarga los reportes de la API y los guarda en Room (recuperar)
    suspend fun recuperarDeApi(): Boolean = try {
        for (dto in api.listar()) {
            val remoteId = dto.id ?: continue
            val existente = reporteDao.obtenerPorRemoteId(remoteId)
            if (existente == null) {
                val nuevo = dto.toEntity()
                val idLocal = reporteDao.insertar(nuevo).toInt()
                historialDao.insertar(
                    HistorialEstatusEntity(reporteId = idLocal, estatus = nuevo.estatus)
                )
            } else if (existente.sincronizado) {
                val actualizado = existente.actualizadoCon(dto)
                reporteDao.actualizar(actualizado)
                if (actualizado.estatus != existente.estatus) {
                    historialDao.insertar(
                        HistorialEstatusEntity(reporteId = existente.id, estatus = actualizado.estatus)
                    )
                    notificacionDao.insertar(
                        NotificacionEntity(
                            reporteId = existente.id,
                            titulo = actualizado.estatus, // clave de estatus; la UI la traduce
                            mensaje = actualizado.titulo
                        )
                    )
                }
            }
        }
        true
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        false
    }
}