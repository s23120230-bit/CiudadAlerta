package com.example.ciudadalerta

import android.app.Application
import com.example.ciudadalerta.data.local.AppDatabase
import com.example.ciudadalerta.data.repository.AuthRepository
import com.example.ciudadalerta.data.repository.AuthRepositoryImpl
import com.example.ciudadalerta.data.repository.ReporteRepository
import com.example.ciudadalerta.data.repository.ReporteRepositoryImpl

class CiudadAlertaApp : Application() {
    private val database by lazy { AppDatabase.crear(this) }

    // El tipo es la INTERFAZ, pero instanciamos la implementación
    val repository: ReporteRepository by lazy {
        ReporteRepositoryImpl(
            reporteDao = database.reporteDao(),
            multimediaDao = database.multimediaDao(),
            notificacionDao = database.notificacionDao(),
            historialDao = database.historialDao()
        )
    }

    // El tipo es la INTERFAZ, pero instanciamos la implementación
    val authRepository: AuthRepository by lazy { AuthRepositoryImpl() }
}