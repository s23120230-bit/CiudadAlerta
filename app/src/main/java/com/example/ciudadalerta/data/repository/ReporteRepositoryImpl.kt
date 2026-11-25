package com.example.ciudadalerta.data.repository

import com.example.ciudadalerta.data.local.HistorialDao
import com.example.ciudadalerta.data.local.HistorialEstatusEntity
import com.example.ciudadalerta.data.local.MultimediaDao
import com.example.ciudadalerta.data.local.NotificacionDao
import com.example.ciudadalerta.data.local.NotificacionEntity
import com.example.ciudadalerta.data.local.ReporteDao
import com.example.ciudadalerta.data.local.ReporteEntity
import com.example.ciudadalerta.data.local.MultimediaEntity
import com.example.ciudadalerta.data.remote.toMap
import com.example.ciudadalerta.data.remote.toEntity
import com.example.ciudadalerta.data.remote.actualizadoCon
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import java.io.File
import com.google.firebase.auth.FirebaseAuth

class ReporteRepositoryImpl( // <-- Nombre cambiado
    private val reporteDao: ReporteDao,
    private val multimediaDao: MultimediaDao,
    private val notificacionDao: NotificacionDao,
    private val historialDao: HistorialDao
) : ReporteRepository { // <-- Implementa la interfaz

    private val db = FirebaseFirestore.getInstance()

    override fun obtenerReportes() = reporteDao.obtenerTodos()
    override fun obtenerReportesPorUsuario(idUsuario: String) = reporteDao.obtenerPorUsuario(idUsuario)
    override fun obtenerReporte(id: Int) = reporteDao.obtenerPorId(id)
    override fun historial(reporteId: Int) = historialDao.obtenerPorReporte(reporteId)
    override fun multimedia(reporteId: Int) = multimediaDao.obtenerPorReporte(reporteId)
    override fun notificaciones() = notificacionDao.obtenerTodas()
    override fun notificacionesSinLeer() = notificacionDao.contarNoLeidas()
    override fun usuarioActualId(): String = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override suspend fun crearReporte(reporte: ReporteEntity): Long {
        val id = reporteDao.insertar(reporte.copy(usuarioId = usuarioActualId()))
        historialDao.insertar(
            HistorialEstatusEntity(reporteId = id.toInt(), estatus = reporte.estatus)
        )
        return id
    }

    override suspend fun actualizarReporte(reporte: ReporteEntity) {
        reporteDao.actualizar(reporte.copy(sincronizado = false))
    }

    override suspend fun eliminarLocal(id: Int): String? {
        val remoteId = reporteDao.obtenerUno(id)?.remoteId
        multimediaDao.obtenerLista(id).forEach { it.uriLocal?.let { ruta -> File(ruta).delete() } }
        reporteDao.eliminarPorId(id)
        return remoteId
    }

    override suspend fun marcarNotificacionLeida(id: Int) = notificacionDao.marcarLeida(id)

    override suspend fun primeraFoto(reporteId: Int): String? =
        multimediaDao.obtenerLista(reporteId).firstOrNull()?.uriLocal

    override suspend fun guardarFoto(reporteId: Int, ruta: String?) {
        multimediaDao.obtenerLista(reporteId)
            .filter { it.uriLocal != ruta }
            .forEach { it.uriLocal?.let { vieja -> File(vieja).delete() } }
        multimediaDao.eliminarPorReporte(reporteId)
        if (ruta != null) {
            multimediaDao.insertar(
                MultimediaEntity(reporteId = reporteId, uriLocal = ruta, tipo = "FOTO")
            )
        }
    }

    override suspend fun sincronizarPendientes() {
        reporteDao.obtenerPendientes().forEach { sincronizarUno(it) }
    }

    private suspend fun sincronizarUno(local: ReporteEntity): Boolean = try {
        val remoteId = local.remoteId
        if (remoteId == null) {
            val docRef = db.collection("reportes").document()
            docRef.set(local.toMap()).await()
            reporteDao.marcarSincronizado(local.id, docRef.id)
        } else {
            db.collection("reportes").document(remoteId).set(local.toMap()).await()
            reporteDao.marcarSincronizado(local.id, remoteId)
        }
        true
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        false
    }

    override suspend fun recuperarDeApi(idUsuario: String): Boolean = try {
        val snapshot = db.collection("reportes")
            .whereEqualTo("usuarioId", idUsuario)
            .get()
            .await()

        for (document in snapshot.documents) {
            val remoto = document.toEntity()
            val remoteId = remoto.remoteId ?: continue
            val existente = reporteDao.obtenerPorRemoteId(remoteId)

            if (existente == null) {
                val idLocal = reporteDao.insertar(remoto).toInt()
                historialDao.insertar(
                    HistorialEstatusEntity(reporteId = idLocal, estatus = remoto.estatus)
                )
            } else if (existente.sincronizado) {
                val actualizado = existente.actualizadoCon(remoto)
                reporteDao.actualizar(actualizado)
                if (actualizado.estatus != existente.estatus) {
                    historialDao.insertar(
                        HistorialEstatusEntity(reporteId = existente.id, estatus = actualizado.estatus)
                    )
                    notificacionDao.insertar(
                        NotificacionEntity(
                            reporteId = existente.id,
                            titulo = actualizado.estatus,
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

    override suspend fun eliminarRemoto(remoteId: String?) {
        if (remoteId == null) return
        try {
            db.collection("reportes").document(remoteId).delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Manejar error
        }
    }
}