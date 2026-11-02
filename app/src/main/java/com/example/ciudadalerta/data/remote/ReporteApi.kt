package com.example.ciudadalerta.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ReporteApi {
    @GET("reportes")
    suspend fun listar(): List<ReporteDto>

    @GET("reportes/{id}")
    suspend fun obtener(@Path("id") id: String): ReporteDto

    @POST("reportes")
    suspend fun crear(@Body reporte: ReporteDto): ReporteDto

    @PUT("reportes/{id}")
    suspend fun actualizar(@Path("id") id: String, @Body reporte: ReporteDto): ReporteDto

    @DELETE("reportes/{id}")
    suspend fun eliminar(@Path("id") id: String)
}