package com.example.ciudadalerta.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ciudadalerta.R

sealed class Destino(
    val ruta: String,
    @StringRes val titulo: Int,
    val icono: ImageVector
) {
    data object MisReportes : Destino("mis_reportes", R.string.menu_mis_reportes, Icons.Default.Home)
    data object Nuevo : Destino("nuevo", R.string.menu_nuevo, Icons.Default.Add)
    data object Ajustes : Destino("ajustes", R.string.menu_ajustes, Icons.Default.Settings)

    companion object {
        val menu = listOf(MisReportes, Nuevo, Ajustes)
    }
}

const val RUTA_DETALLE = "detalle"