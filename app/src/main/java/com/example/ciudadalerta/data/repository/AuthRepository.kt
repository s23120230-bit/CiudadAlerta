package com.example.ciudadalerta.data.repository

import android.util.Base64
import android.util.Patterns
import com.example.ciudadalerta.data.local.SesionManager
import com.example.ciudadalerta.data.local.UsuarioDao
import com.example.ciudadalerta.data.local.UsuarioEntity
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Locale

enum class ErrorAuth {
    CAMPOS_VACIOS,
    CORREO_INVALIDO,
    PASSWORD_CORTA,
    CORREO_EXISTE,
    CREDENCIALES_INCORRECTAS
}

class AuthRepository(
    private val usuarioDao: UsuarioDao,
    private val sesion: SesionManager
) {

    // Devuelve null si todo salió bien, o el motivo del error
    suspend fun registrar(nombre: String, correo: String, password: String): ErrorAuth? {
        val n = nombre.trim()
        val c = correo.trim().lowercase()
        if (n.isEmpty() || c.isEmpty() || password.isEmpty()) return ErrorAuth.CAMPOS_VACIOS
        if (!Patterns.EMAIL_ADDRESS.matcher(c).matches()) return ErrorAuth.CORREO_INVALIDO
        if (password.length < 6) return ErrorAuth.PASSWORD_CORTA
        if (usuarioDao.obtenerPorCorreo(c) != null) return ErrorAuth.CORREO_EXISTE

        val salt = nuevaSal()
        val id = usuarioDao.insertar(
            UsuarioEntity(
                nombre = n,
                correo = c,
                idioma = Locale.getDefault().language,
                passwordHash = hash(password, salt),
                salt = salt
            )
        )
        sesion.guardar(id.toInt())
        return null
    }

    suspend fun iniciarSesion(correo: String, password: String): ErrorAuth? {
        val c = correo.trim().lowercase()
        if (c.isEmpty() || password.isEmpty()) return ErrorAuth.CAMPOS_VACIOS
        val usuario = usuarioDao.obtenerPorCorreo(c) ?: return ErrorAuth.CREDENCIALES_INCORRECTAS
        if (hash(password, usuario.salt) != usuario.passwordHash) {
            return ErrorAuth.CREDENCIALES_INCORRECTAS
        }
        sesion.guardar(usuario.id)
        return null
    }

    fun cerrarSesion() = sesion.cerrar()

    fun usuarioActualId(): Int = sesion.usuarioActual()

    suspend fun usuario(id: Int): UsuarioEntity? = usuarioDao.obtenerPorId(id)

    private fun nuevaSal(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    private fun hash(password: String, salt: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest((salt + password).toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}