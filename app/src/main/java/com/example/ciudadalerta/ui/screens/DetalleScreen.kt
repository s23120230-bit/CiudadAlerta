package com.example.ciudadalerta.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ciudadalerta.R
import com.example.ciudadalerta.ui.viewmodel.ReporteViewModel

@Composable
fun DetalleScreen(vm: ReporteViewModel, id: Int, onVolver: () -> Unit) {
    val reporte by remember(id) { vm.reporte(id) }
        .collectAsStateWithLifecycle(initialValue = null)

    reporte?.let { r ->
        Column(
            Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(r.titulo, style = MaterialTheme.typography.headlineSmall)
            Text(r.descripcion)
            Text("${stringResource(R.string.campo_categoria)}: ${etiquetaCategoria(r.categoria)}")
            Text("${stringResource(R.string.estatus)}: ${etiquetaEstatus(r.estatus)}")
            if (r.hora.isNotEmpty()) {
                Text("${stringResource(R.string.campo_hora)}: ${r.hora}")
            }
            Button(onClick = onVolver) { Text(stringResource(R.string.volver)) }
        }
    }
}