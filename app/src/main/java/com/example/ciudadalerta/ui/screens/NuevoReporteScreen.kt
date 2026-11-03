package com.example.ciudadalerta.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ciudadalerta.R
import com.example.ciudadalerta.data.model.Categoria
import com.example.ciudadalerta.ui.theme.UrgenteRed
import com.example.ciudadalerta.ui.theme.UrgenteRedBg
import com.example.ciudadalerta.ui.viewmodel.ReporteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoReporteScreen(vm: ReporteViewModel, onGuardado: () -> Unit) {
    val form = vm.form
    var mostrarReloj by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Selector visual de categorías
        Text(
            text = stringResource(R.string.campo_categoria),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Categoria.entries.forEach { cat ->
                val seleccionado = form.categoria == cat
                val catColor = colorCategoria(cat.name)
                val catFondo = colorFondoCategoria(cat.name)
                val catIcono = iconoCategoria(cat.name)
                val catTexto = etiquetaCategoria(cat.name)

                OutlinedCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { vm.onCategoria(cat) },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(
                        width = if (seleccionado) 2.dp else 1.dp,
                        color = if (seleccionado) catColor else MaterialTheme.colorScheme.outlineVariant
                    ),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = if (seleccionado) catFondo else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (seleccionado) catColor else catFondo),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = catIcono,
                                contentDescription = null,
                                tint = if (seleccionado) MaterialTheme.colorScheme.surface else catColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = catTexto,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium,
                            color = if (seleccionado) catColor else MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Campos de texto para título y descripción
        OutlinedTextField(
            value = form.titulo,
            onValueChange = vm::onTitulo,
            label = { Text(stringResource(R.string.campo_titulo)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Title,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = form.descripcion,
            onValueChange = vm::onDescripcion,
            label = { Text(stringResource(R.string.campo_descripcion)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            minLines = 3,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        // Selector interactivo de urgencia
        val tarjetaUrgenteBg by animateColorAsState(
            targetValue = if (form.urgente) UrgenteRedBg else MaterialTheme.colorScheme.surface,
            label = "urgenteBg"
        )
        val tarjetaUrgenteBorder by animateColorAsState(
            targetValue = if (form.urgente) UrgenteRed else MaterialTheme.colorScheme.outlineVariant,
            label = "urgenteBorder"
        )

        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { vm.onUrgente(!form.urgente) },
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, tarjetaUrgenteBorder),
            colors = CardDefaults.outlinedCardColors(containerColor = tarjetaUrgenteBg)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (form.urgente) UrgenteRed.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (form.urgente) UrgenteRed else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.urgente),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = if (form.urgente) UrgenteRed else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.marcar_urgente_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Switch(
                    checked = form.urgente,
                    onCheckedChange = vm::onUrgente,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = UrgenteRed,
                        checkedTrackColor = UrgenteRed.copy(alpha = 0.3f)
                    )
                )
            }
        }

        // Selector de hora del incidente
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { mostrarReloj = true },
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.campo_hora),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (form.hora.isEmpty()) stringResource(R.string.elegir_hora) else form.hora,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (form.hora.isEmpty()) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary,
                            fontWeight = if (form.hora.isEmpty()) FontWeight.Normal else FontWeight.Bold
                        )
                    }
                }

                if (form.hora.isNotEmpty()) {
                    IconButton(
                        onClick = { vm.onHora("") },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else {
                    TextButton(onClick = { mostrarReloj = true }) {
                        Text(stringResource(R.string.elegir_hora))
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Botón principal de guardado
        Button(
            onClick = { vm.guardar(onGuardado) },
            enabled = form.titulo.isNotBlank(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(if (form.editando) R.string.guardar_cambios else R.string.guardar),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }

    if (mostrarReloj) {
        val estado = rememberTimePickerState(is24Hour = true)
        AlertDialog(
            onDismissRequest = { mostrarReloj = false },
            confirmButton = {
                TextButton(onClick = {
                    vm.onHora("%02d:%02d".format(estado.hour, estado.minute))
                    mostrarReloj = false
                }) { Text(stringResource(R.string.aceptar)) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarReloj = false }) {
                    Text(stringResource(R.string.cancelar))
                }
            },
            text = { TimePicker(state = estado) }
        )
    }
}
