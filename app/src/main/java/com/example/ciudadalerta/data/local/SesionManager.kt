package com.example.ciudadalerta.data.local

import android.content.Context

// Recuerda qué usuario inició sesión (sobrevive al cerrar la app)
class SesionManager(context: Context) {

    private val prefs = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)

    fun guardar(usuarioId: Int) {
        prefs.edit().putInt(CLAVE, usuarioId).apply()
    }

    // Devuelve -1 si no hay sesión
    fun usuarioActual(): Int = prefs.getInt(CLAVE, -1)

    fun cerrar() {
        prefs.edit().remove(CLAVE).apply()
    }

    private companion object {
        const val CLAVE = "usuario_id"
    }
}