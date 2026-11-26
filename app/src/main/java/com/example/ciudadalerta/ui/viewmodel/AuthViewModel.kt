package com.example.ciudadalerta.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ciudadalerta.CiudadAlertaApp
import com.example.ciudadalerta.data.repository.AuthRepository
import com.example.ciudadalerta.data.repository.ErrorAuth
import com.example.ciudadalerta.data.repository.ReporteRepository
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repo: AuthRepository,
    private val reporteRepo: ReporteRepository
) : ViewModel() {

    // Si el ID no está vacío, la sesión está iniciada
    var sesionIniciada by mutableStateOf(repo.usuarioActualId().isNotEmpty())
        private set

    var nombreUsuario by mutableStateOf("")
        private set

    var error by mutableStateOf<ErrorAuth?>(null)
        private set

    var cargando by mutableStateOf(false)
        private set

    init {
        cargarNombre()
    }

    private fun cargarNombre() {
        if (repo.usuarioActualId().isEmpty()) {
            nombreUsuario = ""
            return
        }
        nombreUsuario = repo.usuarioActualNombre()
    }

    fun limpiarError() {
        error = null
    }

    fun registrar(nombre: String, correo: String, password: String) {
        viewModelScope.launch {
            cargando = true
            error = repo.registrar(nombre, correo, password)
            if (error == null) {
                cargarNombre()
                sesionIniciada = true
            }
            cargando = false
        }
    }

    fun iniciarSesion(correo: String, password: String) {
        viewModelScope.launch {
            cargando = true
            error = repo.iniciarSesion(correo, password)
            if (error == null) {
                cargarNombre()
                sesionIniciada = true
            }
            cargando = false
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            reporteRepo.limpiarBaseDeDatosLocal() // Borra la caché local
            repo.cerrarSesion()
            nombreUsuario = ""
            sesionIniciada = false
            error = null
            cargando = false
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as CiudadAlertaApp
                AuthViewModel(app.authRepository, app.repository)
            }
        }
    }
}