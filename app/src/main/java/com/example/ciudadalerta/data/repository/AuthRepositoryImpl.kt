package com.example.ciudadalerta.data.repository

import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl : AuthRepository {

    // Instancia de Firebase Auth
    private val auth = FirebaseAuth.getInstance()

    override suspend fun registrar(nombre: String, correo: String, password: String): ErrorAuth? {
        val n = nombre.trim()
        val c = correo.trim().lowercase()

        // Validaciones locales rápidas
        if (n.isEmpty() || c.isEmpty() || password.isEmpty()) return ErrorAuth.CAMPOS_VACIOS
        if (!Patterns.EMAIL_ADDRESS.matcher(c).matches()) return ErrorAuth.CORREO_INVALIDO
        if (password.length < 6) return ErrorAuth.PASSWORD_CORTA

        return try {
            // 1. Creamos el usuario en Firebase
            val result = auth.createUserWithEmailAndPassword(c, password).await()
            val user = result.user

            // 2. Le asignamos el nombre al perfil del usuario
            if (user != null) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(n)
                    .build()
                user.updateProfile(profileUpdates).await()
            }
            null // Todo salió bien
        } catch (e: FirebaseAuthUserCollisionException) {
            ErrorAuth.CORREO_EXISTE // El correo ya está registrado
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Error al registrar en Firebase", e)
            ErrorAuth.ERROR_DESCONOCIDO
        }
    }

    override suspend fun iniciarSesion(correo: String, password: String): ErrorAuth? {
        val c = correo.trim().lowercase()
        if (c.isEmpty() || password.isEmpty()) return ErrorAuth.CAMPOS_VACIOS

        return try {
            // Firebase verifica las credenciales
            auth.signInWithEmailAndPassword(c, password).await()
            null // Todo salió bien
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Error al iniciar sesión en Firebase", e)
            ErrorAuth.CREDENCIALES_INCORRECTAS
        }
    }

    override fun cerrarSesion() {
        auth.signOut()
    }

    // Ahora devuelve un String en lugar de un Int
    override fun usuarioActualId(): String = auth.currentUser?.uid ?: ""

    // Obtenemos el nombre directamente de Firebase
    override fun usuarioActualNombre(): String = auth.currentUser?.displayName ?: "Usuario"
}