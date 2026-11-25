package com.example.ciudadalerta.data.repository

enum class ErrorAuth {
    CAMPOS_VACIOS,
    CORREO_INVALIDO,
    PASSWORD_CORTA,
    CORREO_EXISTE,
    CREDENCIALES_INCORRECTAS,
    ERROR_DESCONOCIDO
}

// Esta es la clase abstracta / interfaz
interface AuthRepository {
    suspend fun registrar(nombre: String, correo: String, password: String): ErrorAuth?
    suspend fun iniciarSesion(correo: String, password: String): ErrorAuth?
    fun cerrarSesion()
    fun usuarioActualId(): String
    fun usuarioActualNombre(): String
}