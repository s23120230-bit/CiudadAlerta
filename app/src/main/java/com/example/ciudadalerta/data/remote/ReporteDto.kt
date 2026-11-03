package com.example.ciudadalerta.data.remote

// Modelo JSON de un reporte
data class ReporteDto(
    val id: String? = null,
    val titulo: String? = null,
    val descripcion: String? = null,
    val categoria: String? = null,
    val hora: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val urgente: Boolean? = null,
    val estatus: String? = null,
    val fechaCreacion: Long? = null
)