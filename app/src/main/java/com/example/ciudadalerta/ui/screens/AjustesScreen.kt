package com.example.ciudadalerta.ui.screens

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import com.example.ciudadalerta.R
import java.util.Locale

@Composable
fun AjustesScreen(nombreUsuario: String, onCerrarSesion: () -> Unit) {
    val actual = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        .ifEmpty { Locale.getDefault().language }.take(2)

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        if (nombreUsuario.isNotEmpty()) {
            Text(
                stringResource(R.string.sesion_de, nombreUsuario),
                style = MaterialTheme.typography.titleMedium
            )
        }
        OutlinedButton(onClick = onCerrarSesion) {
            Text(stringResource(R.string.cerrar_sesion))
        }

        Spacer(Modifier.height(24.dp))

        Text(stringResource(R.string.idioma), style = MaterialTheme.typography.headlineSmall)
        listOf("es" to R.string.idioma_es, "en" to R.string.idioma_en).forEach { (codigo, nombre) ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = actual == codigo,
                        onClick = {
                            AppCompatDelegate.setApplicationLocales(
                                LocaleListCompat.forLanguageTags(codigo)
                            )
                        },
                        role = Role.RadioButton
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = actual == codigo, onClick = null)
                Text(stringResource(nombre), Modifier.padding(start = 8.dp))
            }
        }
    }
}