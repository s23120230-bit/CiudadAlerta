package com.example.ciudadalerta.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ciudadalerta.R
import com.example.ciudadalerta.ui.viewmodel.ReporteViewModel

@Composable
fun ListaReportesScreen(vm: ReporteViewModel, onAbrir: (Int) -> Unit) {
    val reportes by vm.reportes.collectAsStateWithLifecycle()

    if (reportes.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.sin_reportes))
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(reportes, key = { it.id }) { r ->
                Card(Modifier.fillMaxWidth().clickable { onAbrir(r.id) }) {
                    Column(Modifier.padding(16.dp)) {
                        Text(r.titulo, style = MaterialTheme.typography.titleMedium)
                        Text(etiquetaCategoria(r.categoria))
                        Text(
                            etiquetaEstatus(r.estatus),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}