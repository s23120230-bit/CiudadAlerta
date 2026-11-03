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
import kotlinx.coroutines.launch

class AuthViewModel(private val repo: AuthRepository) : ViewModel() {

    var sesionIniciada by mutableStateOf(repo.usuarioActualId() != -1)
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
        val id = repo.usuarioActualId()
        if (id == -1) {
            nombreUsuario = ""
            return
        }
        viewModelScope.launch {
            val usuario = repo.usuario(id)
            if (usuario == null) {

                repo.cerrarSesion()
                sesionIniciada = false
            } else {
                nombreUsuario = usuario.nombre
            }
        }
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
        repo.cerrarSesion()
        nombreUsuario = ""
        sesionIniciada = false
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as CiudadAlertaApp
                AuthViewModel(app.authRepository)
            }
        }
    }
}