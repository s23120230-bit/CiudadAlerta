package com.example.ciudadalerta.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ciudadalerta.R
import com.example.ciudadalerta.ui.viewmodel.ReporteViewModel
import java.text.DateFormat
import java.util.Date

@Composable
fun DetalleScreen(
    vm: ReporteViewModel,
    id: Int,
    onVolver: () -> Unit,
    onEditar: () -> Unit
) {
    val reporte by remember(id) { vm.reporte(id) }
        .collectAsStateWithLifecycle(initialValue = null)
    val historial by remember(id) { vm.historial(id) }
        .collectAsStateWithLifecycle(initialValue = emptyList())
    var confirmar by remember { mutableStateOf(false) }
    val formato = remember { DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT) }

    reporte?.let { r ->
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(r.titulo, style = MaterialTheme.typography.headlineSmall)
            Text(r.descripcion)
            Text("${stringResource(R.string.campo_categoria)}: ${etiquetaCategoria(r.categoria)}")
            Text("${stringResource(R.string.estatus)}: ${etiquetaEstatus(r.estatus)}")
            if (r.hora.isNotEmpty()) {
                Text("${stringResource(R.string.campo_hora)}: ${r.hora}")
            }

            Text(stringResource(R.string.historial), style = MaterialTheme.typography.titleMedium)
            historial.forEach { h ->
                Text("• ${etiquetaEstatus(h.estatus)} (${formato.format(Date(h.fecha))})")
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (r.estatus == "RECIBIDO") {
                    Button(onClick = {
                        vm.editar(r)
                        onEditar()
                    }) { Text(stringResource(R.string.editar)) }
                    OutlinedButton(onClick = { confirmar = true }) {
                        Text(stringResource(R.string.eliminar))
                    }
                }
                TextButton(onClick = onVolver) { Text(stringResource(R.string.volver)) }
            }
        }

        if (confirmar) {
            AlertDialog(
                onDismissRequest = { confirmar = false },
                text = { Text(stringResource(R.string.confirmar_eliminar)) },
                confirmButton = {
                    TextButton(onClick = {
                        confirmar = false
                        vm.eliminar(r.id, onVolver)
                    }) { Text(stringResource(R.string.eliminar)) }
                },
                dismissButton = {
                    TextButton(onClick = { confirmar = false }) {
                        Text(stringResource(R.string.cancelar))
                    }
                }
            )
        }
    }
}