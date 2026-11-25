package com.example.ciudadalerta.data.remote

import com.example.ciudadalerta.data.local.ReporteEntity
import com.google.firebase.firestore.DocumentSnapshot

/*fun ReporteEntity.toDto() = ReporteDto(
    id = remoteId,
    titulo = titulo,
    descripcion = descripcion,
    categoria = categoria,
    hora = hora,
    latitud = latitud,
    longitud = longitud,
    urgente = urgente,
    estatus = estatus,
    fechaCreacion = fechaCreacion
)

fun ReporteDto.toEntity() = ReporteEntity(
    remoteId = id,
    titulo = titulo.orEmpty(),
    descripcion = descripcion.orEmpty(),
    categoria = categoria ?: "BACHE",
    hora = hora.orEmpty(),
    latitud = latitud,
    longitud = longitud,
    urgente = urgente ?: false,
    estatus = estatus ?: "RECIBIDO",
    sincronizado = true,
    fechaCreacion = fechaCreacion ?: System.currentTimeMillis()
)

// Actualiza un reporte local con los datos del servidor, conservando su id local.
fun ReporteEntity.actualizadoCon(dto: ReporteDto) = copy(
    titulo = dto.titulo ?: titulo,
    descripcion = dto.descripcion ?: descripcion,
    categoria = dto.categoria ?: categoria,
    hora = dto.hora ?: hora,
    latitud = dto.latitud ?: latitud,
    longitud = dto.longitud ?: longitud,
    urgente = dto.urgente ?: urgente,
    estatus = dto.estatus ?: estatus,
    sincronizado = true
)*/


// 1. Convierte la entidad local a un Mapa para enviarlo a Firebase
fun ReporteEntity.toMap(): Map<String, Any?> {
    return mapOf(
        "usuarioId" to usuarioId,
        "titulo" to titulo,
        "descripcion" to descripcion,
        "categoria" to categoria,
        "hora" to hora,
        "latitud" to latitud,
        "longitud" to longitud,
        "urgente" to urgente,
        "estatus" to estatus,
        "fechaCreacion" to fechaCreacion
        // Omite 'id' (local), 'remoteId' y 'sincronizado' porque Firebase no los necesita.
    )
}

// 2. Convierte un documento de Firebase a la entidad local
fun DocumentSnapshot.toEntity(): ReporteEntity {
    return ReporteEntity(
        remoteId = id, // El ID del documento en Firebase
        usuarioId = getString("usuarioId").orEmpty(),
        titulo = getString("titulo").orEmpty(),
        descripcion = getString("descripcion").orEmpty(),
        categoria = getString("categoria") ?: "BACHE",
        hora = getString("hora").orEmpty(),
        latitud = getDouble("latitud"),
        longitud = getDouble("longitud"),
        urgente = getBoolean("urgente") ?: false,
        estatus = getString("estatus") ?: "RECIBIDO",
        sincronizado = true,
        fechaCreacion = getLong("fechaCreacion") ?: System.currentTimeMillis()
    )
}

// 3. Actualiza un reporte local con los datos del servidor, conservando su id local.
fun ReporteEntity.actualizadoCon(remoto: ReporteEntity) = copy(
    titulo = remoto.titulo,
    descripcion = remoto.descripcion,
    categoria = remoto.categoria,
    hora = remoto.hora,
    latitud = remoto.latitud,
    longitud = remoto.longitud,
    urgente = remoto.urgente,
    estatus = remoto.estatus,
    sincronizado = true
)
