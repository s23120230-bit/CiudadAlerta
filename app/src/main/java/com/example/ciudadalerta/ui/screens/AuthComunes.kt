package com.example.ciudadalerta.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.ciudadalerta.R
import com.example.ciudadalerta.data.repository.ErrorAuth

@Composable
fun mensajeError(error: ErrorAuth): String = stringResource(
    when (error) {
        ErrorAuth.CAMPOS_VACIOS -> R.string.err_campos_vacios
        ErrorAuth.CORREO_INVALIDO -> R.string.err_correo_invalido
        ErrorAuth.PASSWORD_CORTA -> R.string.err_password_corta
        ErrorAuth.CORREO_EXISTE -> R.string.err_correo_existe
        ErrorAuth.CREDENCIALES_INCORRECTAS -> R.string.err_credenciales
    }
)