package com.example.ciudadalerta.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reporte")
data class ReporteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "remote_id") val remoteId: String? = null,
    @ColumnInfo(name = "usuario_id") val usuarioId: Int = 0,
    val titulo: String,
    val descripcion: String,
    val categoria: String,
    val hora: String = "",
    val latitud: Double? = null,
    val longitud: Double? = null,
    val urgente: Boolean = false,
    val estatus: String = "RECIBIDO",
    val sincronizado: Boolean = false,
    @ColumnInfo(name = "fecha_creacion") val fechaCreacion: Long = System.currentTimeMillis()
)