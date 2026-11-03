package com.example.ciudadalerta.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "usuario", indices = [Index(value = ["correo"], unique = true)])
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val correo: String,
    val idioma: String = "es",
    @ColumnInfo(name = "password_hash") val passwordHash: String = "",
    val salt: String = "",
    @ColumnInfo(name = "fecha_registro") val fechaRegistro: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "multimedia",
    foreignKeys = [
        ForeignKey(
            entity = ReporteEntity::class,
            parentColumns = ["id"],
            childColumns = ["reporte_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["reporte_id"])]
)
data class MultimediaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "reporte_id") val reporteId: Int,
    @ColumnInfo(name = "uri_local") val uriLocal: String? = null,
    @ColumnInfo(name = "url_remota") val urlRemota: String? = null,
    val tipo: String = "FOTO",
    val fecha: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "notificacion",
    foreignKeys = [
        ForeignKey(
            entity = ReporteEntity::class,
            parentColumns = ["id"],
            childColumns = ["reporte_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["reporte_id"])]
)
data class NotificacionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "reporte_id") val reporteId: Int,
    val titulo: String,
    val mensaje: String = "",
    val leida: Boolean = false,
    val fecha: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "historial_estatus",
    foreignKeys = [
        ForeignKey(
            entity = ReporteEntity::class,
            parentColumns = ["id"],
            childColumns = ["reporte_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["reporte_id"])]
)
data class HistorialEstatusEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "reporte_id") val reporteId: Int,
    val estatus: String,
    val comentario: String = "",
    val fecha: Long = System.currentTimeMillis()
)