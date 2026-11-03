package com.example.ciudadalerta

import android.app.Application
import com.example.ciudadalerta.data.local.AppDatabase
import com.example.ciudadalerta.data.local.SesionManager
import com.example.ciudadalerta.data.remote.ApiClient
import com.example.ciudadalerta.data.repository.AuthRepository
import com.example.ciudadalerta.data.repository.ReporteRepository

class CiudadAlertaApp : Application() {
    private val database by lazy { AppDatabase.crear(this) }
    private val sesion by lazy { SesionManager(this) }

    val repository by lazy {
        ReporteRepository(
            reporteDao = database.reporteDao(),
            multimediaDao = database.multimediaDao(),
            notificacionDao = database.notificacionDao(),
            historialDao = database.historialDao(),
            api = ApiClient.reporteApi,
            sesion = sesion
        )
    }

    val authRepository by lazy { AuthRepository(database.usuarioDao(), sesion) }
}