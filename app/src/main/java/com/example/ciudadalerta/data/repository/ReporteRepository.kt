package com.example.ciudadalerta.data.repository

import com.example.ciudadalerta.data.local.ReporteDao
import com.example.ciudadalerta.data.local.ReporteEntity
import kotlinx.coroutines.flow.Flow

class ReporteRepository(private val dao: ReporteDao) {

    fun obtenerReportes(): Flow<List<ReporteEntity>> = dao.obtenerTodos()

    fun obtenerReporte(id: Int): Flow<ReporteEntity?> = dao.obtenerPorId(id)

    suspend fun crearReporte(reporte: ReporteEntity) {
        dao.insertar(reporte)
        // TODO etapa siguiente: enviar a la nube y marcar sincronizado = true
    }
}