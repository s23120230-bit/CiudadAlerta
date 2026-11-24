package com.example.ciudadalerta.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.ciudadalerta.R
import com.example.ciudadalerta.data.local.FotoStorage
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.io.File
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ---------- Imagen guardada en la memoria de la app ----------
@Composable
fun ImagenLocal(ruta: String, modifier: Modifier = Modifier) {
    val imagen by produceState<ImageBitmap?>(initialValue = null, key1 = ruta) {
        value = withContext(Dispatchers.IO) {
            try {
                BitmapFactory.decodeFile(ruta)?.asImageBitmap()
            } catch (e: Throwable) {
                null
            }
        }
    }
    imagen?.let {
        Image(
            bitmap = it,
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}

// ---------- Ubicación actual (solo se llama con el permiso concedido) ----------
@SuppressLint("MissingPermission")
private fun obtenerUbicacionActual(context: Context, alTerminar: (Location?) -> Unit) {
    try {
        LocationServices.getFusedLocationProviderClient(context)
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { alTerminar(it) }
            .addOnFailureListener { alTerminar(null) }
    } catch (e: SecurityException) {
        alTerminar(null)
    }
}

private fun tienePermisoUbicacion(context: Context): Boolean =
    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

private fun formatoCoordenadas(lat: Double, lon: Double): String =
    String.format(Locale.US, "%.5f, %.5f", lat, lon)

// Abre la app de mapas del teléfono; si no hay, abre el navegador
fun abrirMapa(context: Context, lat: Double, lon: Double, titulo: String) {
    val mapa = Intent(Intent.ACTION_VIEW, Uri.parse("geo:$lat,$lon?q=$lat,$lon(${Uri.encode(titulo)})"))
    try {
        context.startActivity(mapa)
    } catch (e: ActivityNotFoundException) {
        val web = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lon")
        )
        try {
            context.startActivity(web)
        } catch (e2: ActivityNotFoundException) {
            // Sin app de mapas ni navegador: no hay nada que hacer
        }
    }
}

// ---------- Sección del formulario: foto y ubicación ----------
@Composable
fun SeccionFotoUbicacion(
    fotoPath: String?,
    latitud: Double?,
    longitud: Double?,
    onFoto: (String?) -> Unit,
    onUbicacion: (Double?, Double?) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var procesando by remember { mutableStateOf(false) }
    var buscando by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf<Int?>(null) }
    var rutaTemporal by rememberSaveable { mutableStateOf<String?>(null) }

    val galeria = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                procesando = true
                val ruta = withContext(Dispatchers.IO) { FotoStorage.importarDesdeUri(context, uri) }
                if (ruta != null) onFoto(ruta)
                procesando = false
            }
        }
    }

    val camara = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { tomada ->
        val temporal = rutaTemporal?.let { File(it) }
        if (tomada && temporal != null) {
            scope.launch {
                procesando = true
                val ruta = withContext(Dispatchers.IO) {
                    FotoStorage.importarDesdeArchivo(context, temporal)
                }
                if (ruta != null) onFoto(ruta)
                procesando = false
            }
        } else {
            temporal?.delete()
        }
        rutaTemporal = null
    }

    val buscar = {
        buscando = true
        mensaje = null
        obtenerUbicacionActual(context) { ubicacion ->
            buscando = false
            if (ubicacion != null) {
                onUbicacion(ubicacion.latitude, ubicacion.longitude)
            } else {
                mensaje = R.string.ubic_no_disponible
            }
        }
    }

    val permisoUbicacion = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        val concedido = permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (concedido) buscar() else mensaje = R.string.ubic_permiso_denegado
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        // ----- Foto -----
        Text(stringResource(R.string.seccion_foto), style = MaterialTheme.typography.titleSmall)
        if (fotoPath != null) {
            ImagenLocal(
                ruta = fotoPath,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }
        if (procesando) {
            Text(stringResource(R.string.procesando_foto), style = MaterialTheme.typography.bodySmall)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = {
                    galeria.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                enabled = !procesando
            ) { Text(stringResource(R.string.foto_galeria)) }

            OutlinedButton(
                onClick = {
                    val temporal = FotoStorage.archivoTemporal(context)
                    rutaTemporal = temporal.absolutePath
                    try {
                        camara.launch(FotoStorage.uriDe(context, temporal))
                    } catch (e: ActivityNotFoundException) {
                        temporal.delete()
                        rutaTemporal = null
                    }
                },
                enabled = !procesando
            ) { Text(stringResource(R.string.foto_camara)) }

            if (fotoPath != null) {
                TextButton(onClick = { onFoto(null) }) { Text(stringResource(R.string.quitar)) }
            }
        }

        // ----- Ubicación -----
        Text(stringResource(R.string.campo_ubicacion), style = MaterialTheme.typography.titleSmall)
        if (latitud != null && longitud != null) {
            Text(formatoCoordenadas(latitud, longitud))
        }
        mensaje?.let {
            Text(
                stringResource(it),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = {
                    if (tienePermisoUbicacion(context)) {
                        buscar()
                    } else {
                        permisoUbicacion.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                enabled = !buscando
            ) {
                Text(
                    stringResource(
                        if (buscando) R.string.buscando_ubicacion else R.string.usar_ubicacion
                    )
                )
            }
            if (latitud != null && longitud != null) {
                TextButton(onClick = { onUbicacion(null, null) }) {
                    Text(stringResource(R.string.quitar))
                }
            }
        }
    }
}

// ---------- Sección del detalle: foto, coordenadas y mapa ----------
@Composable
fun SeccionFotoUbicacionDetalle(
    fotoPath: String?,
    latitud: Double?,
    longitud: Double?,
    titulo: String
) {
    val context = LocalContext.current

    if (fotoPath != null && File(fotoPath).exists()) {
        ImagenLocal(
            ruta = fotoPath,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(12.dp))
        )
    }
    if (latitud != null && longitud != null) {
        Text("${stringResource(R.string.campo_ubicacion)}: ${formatoCoordenadas(latitud, longitud)}")
        OutlinedButton(onClick = { abrirMapa(context, latitud, longitud, titulo) }) {
            Text(stringResource(R.string.ver_en_mapa))
        }
    }
}