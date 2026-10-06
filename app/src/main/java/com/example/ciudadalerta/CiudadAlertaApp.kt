package com.example.ciudadalerta

import android.app.Application
import com.example.ciudadalerta.data.local.AppDatabase
import com.example.ciudadalerta.data.repository.ReporteRepository

class CiudadAlertaApp : Application() {
    private val database by lazy { AppDatabase.crear(this) }
    val repository by lazy { ReporteRepository(database.reporteDao()) }
}