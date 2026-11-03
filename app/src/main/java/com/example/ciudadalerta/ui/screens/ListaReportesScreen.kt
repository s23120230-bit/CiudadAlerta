package com.example.ciudadalerta.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ciudadalerta.R
import com.example.ciudadalerta.ui.theme.UrgenteRed
import com.example.ciudadalerta.ui.theme.UrgenteRedBg
import com.example.ciudadalerta.ui.viewmodel.EstadoSync
import com.example.ciudadalerta.ui.viewmodel.ReporteViewModel
import java.text.DateFormat
import java.util.Date

@Composable
fun ListaReportesScreen(
    vm: ReporteViewModel,
    onAbrir: (Int) -> Unit,
    onCrearNuevo: () -> Unit = {}
) {
    val reportes by vm.reportes.collectAsStateWithLifecycle()
    var filtroSeleccionado by rememberSaveable { mutableStateOf("TODOS") }
    val formatoFecha = remember { DateFormat.getDateInstance(DateFormat.SHORT) }

    val reportesFiltrados = remember(reportes, filtroSeleccionado) {
        when (filtroSeleccionado) {
            "URGENTES" -> reportes.filter { it.urgente }
            "BACHE" -> reportes.filter { it.categoria == "BACHE" }
            "LUZ" -> reportes.filter { it.categoria == "LUZ" }
            "BASURA" -> reportes.filter { it.categoria == "BASURA" }
            else -> reportes
        }
    }

    Column(Modifier.fillMaxSize()) {
        // Barra informativa de estado de sincronización
        AnimatedVisibility(
            visible = vm.estadoSync != EstadoSync.INACTIVO,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Surface(
                color = when (vm.estadoSync) {
                    EstadoSync.ERROR -> MaterialTheme.colorScheme.errorContainer
                    EstadoSync.SINCRONIZANDO -> MaterialTheme.colorScheme.secondaryContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        when (vm.estadoSync) {
                            EstadoSync.SINCRONIZANDO -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.sincronizando),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                            EstadoSync.OK -> {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.sync_ok),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            EstadoSync.ERROR -> {
                                Icon(
                                    imageVector = Icons.Default.CloudOff,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.sync_error),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                            EstadoSync.INACTIVO -> {}
                        }
                    }

                    if (vm.estadoSync == EstadoSync.ERROR) {
                        IconButton(
                            onClick = { vm.sincronizar() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = stringResource(R.string.sincronizar),
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Filtros horizontales interactivos
        if (reportes.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filtroSeleccionado == "TODOS",
                    onClick = { filtroSeleccionado = "TODOS" },
                    label = { Text("${stringResource(R.string.filtro_todos)} (${reportes.size})") }
                )
                val urgentesCount = reportes.count { it.urgente }
                if (urgentesCount > 0) {
                    FilterChip(
                        selected = filtroSeleccionado == "URGENTES",
                        onClick = { filtroSeleccionado = "URGENTES" },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = UrgenteRed,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        label = { Text("${stringResource(R.string.urgente)} ($urgentesCount)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = UrgenteRedBg,
                            selectedLabelColor = UrgenteRed
                        )
                    )
                }
                FilterChip(
                    selected = filtroSeleccionado == "BACHE",
                    onClick = { filtroSeleccionado = "BACHE" },
                    label = { Text(stringResource(R.string.cat_bache)) }
                )
                FilterChip(
                    selected = filtroSeleccionado == "LUZ",
                    onClick = { filtroSeleccionado = "LUZ" },
                    label = { Text(stringResource(R.string.cat_luz)) }
                )
                FilterChip(
                    selected = filtroSeleccionado == "BASURA",
                    onClick = { filtroSeleccionado = "BASURA" },
                    label = { Text(stringResource(R.string.cat_basura)) }
                )
            }
        }

        // Lista de reportes o estado vacío
        if (reportes.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationCity,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                    Text(
                        text = stringResource(R.string.sin_reportes),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.sin_reportes_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Button(
                        onClick = onCrearNuevo,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.crear_reporte))
                    }
                }
            }
        } else if (reportesFiltrados.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.sin_reportes),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Button(onClick = { filtroSeleccionado = "TODOS" }) {
                        Text(stringResource(R.string.filtro_todos))
                    }
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(reportesFiltrados, key = { it.id }) { r ->
                    ElevatedCard(
                        onClick = { onAbrir(r.id) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            // Cabecera de la tarjeta con categoría, urgencia y estatus
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    BadgeCategoria(r.categoria)
                                    if (r.urgente) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(UrgenteRedBg)
                                                .padding(horizontal = 6.dp, vertical = 4.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Warning,
                                                    contentDescription = null,
                                                    tint = UrgenteRed,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(Modifier.width(3.dp))
                                                Text(
                                                    text = stringResource(R.string.urgente),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = UrgenteRed
                                                )
                                            }
                                        }
                                    }
                                }

                                BadgeEstatus(r.estatus)
                            }

                            Spacer(Modifier.height(10.dp))

                            // Título del reporte
                            Text(
                                text = r.titulo,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            // Descripción breve
                            if (r.descripcion.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = r.descripcion,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(Modifier.height(12.dp))

                            // Pie de la tarjeta con fecha/hora y sincronización
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (r.hora.isNotEmpty()) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.AccessTime,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.outline,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                text = r.hora,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }
                                    }

                                    Text(
                                        text = formatoFecha.format(Date(r.fechaCreacion)),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (!r.sincronizado) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(end = 4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CloudUpload,
                                                contentDescription = stringResource(R.string.pendiente_sync),
                                                tint = MaterialTheme.colorScheme.tertiary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(Modifier.width(3.dp))
                                            Text(
                                                text = stringResource(R.string.pendiente_sync),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.tertiary
                                            )
                                        }
                                    }

                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.outlineVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
