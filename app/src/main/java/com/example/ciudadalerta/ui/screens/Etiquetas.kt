package com.example.ciudadalerta.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ciudadalerta.R
import com.example.ciudadalerta.ui.theme.CatBache
import com.example.ciudadalerta.ui.theme.CatBacheBg
import com.example.ciudadalerta.ui.theme.CatBasura
import com.example.ciudadalerta.ui.theme.CatBasuraBg
import com.example.ciudadalerta.ui.theme.CatLuz
import com.example.ciudadalerta.ui.theme.CatLuzBg
import com.example.ciudadalerta.ui.theme.StatusEnProceso
import com.example.ciudadalerta.ui.theme.StatusEnProcesoBg
import com.example.ciudadalerta.ui.theme.StatusRecibido
import com.example.ciudadalerta.ui.theme.StatusRecibidoBg
import com.example.ciudadalerta.ui.theme.StatusResuelto
import com.example.ciudadalerta.ui.theme.StatusResueltoBg

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

fun iconoCategoria(c: String): ImageVector = when (c) {
    "BACHE" -> Icons.Default.Build
    "LUZ" -> Icons.Default.Lightbulb
    else -> Icons.Default.Delete
}

fun colorCategoria(c: String): Color = when (c) {
    "BACHE" -> CatBache
    "LUZ" -> CatLuz
    else -> CatBasura
}

fun colorFondoCategoria(c: String): Color = when (c) {
    "BACHE" -> CatBacheBg
    "LUZ" -> CatLuzBg
    else -> CatBasuraBg
}

fun iconoEstatus(e: String): ImageVector = when (e) {
    "EN_PROCESO" -> Icons.Default.Pending
    "RESUELTO" -> Icons.Default.CheckCircle
    else -> Icons.Default.Inbox
}

fun colorEstatus(e: String): Color = when (e) {
    "EN_PROCESO" -> StatusEnProceso
    "RESUELTO" -> StatusResuelto
    else -> StatusRecibido
}

fun colorFondoEstatus(e: String): Color = when (e) {
    "EN_PROCESO" -> StatusEnProcesoBg
    "RESUELTO" -> StatusResueltoBg
    else -> StatusRecibidoBg
}

@Composable
fun BadgeCategoria(categoria: String, modifier: Modifier = Modifier) {
    val color = colorCategoria(categoria)
    val fondo = colorFondoCategoria(categoria)
    val icono = iconoCategoria(categoria)
    val texto = etiquetaCategoria(categoria)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(fondo)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = texto,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}

@Composable
fun BadgeEstatus(estatus: String, modifier: Modifier = Modifier) {
    val color = colorEstatus(estatus)
    val fondo = colorFondoEstatus(estatus)
    val icono = iconoEstatus(estatus)
    val texto = etiquetaEstatus(estatus)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(fondo)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = texto,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}
