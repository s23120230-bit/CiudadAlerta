package com.example.ciudadalerta.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reportes")
data class ReporteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val categoria: String,
    val hora: String = "",
    val urgente: Boolean = false,
    val estatus: String = "RECIBIDO",
    val sincronizado: Boolean = false
)