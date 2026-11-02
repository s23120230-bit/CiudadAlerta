package com.example.ciudadalerta.data.remote

import com.example.ciudadalerta.data.local.ReporteEntity

fun ReporteEntity.toDto() = ReporteDto(
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
)