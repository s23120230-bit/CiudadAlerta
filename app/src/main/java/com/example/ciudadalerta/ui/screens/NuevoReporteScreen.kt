package com.example.ciudadalerta.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.ciudadalerta.R
import com.example.ciudadalerta.data.model.Categoria
import com.example.ciudadalerta.ui.viewmodel.ReporteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoReporteScreen(vm: ReporteViewModel, onGuardado: () -> Unit) {
    val form = vm.form
    var mostrarReloj by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(stringResource(R.string.menu_nuevo), style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = form.titulo,
            onValueChange = vm::onTitulo,
            label = { Text(stringResource(R.string.campo_titulo)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = form.descripcion,
            onValueChange = vm::onDescripcion,
            label = { Text(stringResource(R.string.campo_descripcion)) },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        Text(stringResource(R.string.campo_categoria), style = MaterialTheme.typography.titleSmall)
        Categoria.entries.forEach { cat ->
            Row(
                Modifier.fillMaxWidth().selectable(
                    selected = form.categoria == cat,
                    onClick = { vm.onCategoria(cat) },
                    role = Role.RadioButton
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = form.categoria == cat, onClick = null)
                Text(etiquetaCategoria(cat.name), Modifier.padding(start = 8.dp))
            }
        }

        Row(
            Modifier.fillMaxWidth().toggleable(
                value = form.urgente,
                onValueChange = vm::onUrgente,
                role = Role.Checkbox
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = form.urgente, onCheckedChange = null)
            Text(stringResource(R.string.urgente), Modifier.padding(start = 8.dp))
        }

        OutlinedButton(onClick = { mostrarReloj = true }) {
            Text(if (form.hora.isEmpty()) stringResource(R.string.elegir_hora) else form.hora)
        }

        Button(
            onClick = { vm.guardar(onGuardado) },
            enabled = form.titulo.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.guardar))
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