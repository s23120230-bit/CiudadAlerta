package com.example.ciudadalerta.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.ciudadalerta.R

@Composable
fun etiquetaCategoria(c: String): String = stringResource(
    when (c) {
        "BACHE" -> R.string.cat_bache
        "LUZ" -> R.string.cat_luz
        else -> R.string.cat_basura
    }
)

@Composable
fun etiquetaEstatus(e: String): String = stringResource(
    when (e) {
        "EN_PROCESO" -> R.string.est_proceso
        "RESUELTO" -> R.string.est_resuelto
        else -> R.string.est_recibido
    }
)