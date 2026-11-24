package com.example.ciudadalerta.data.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.InputStream

// Guarda las fotos del reporte en la memoria privada de la app,
// comprimidas y derechas, sin los datos de ubicación de la foto original.
object FotoStorage {
    private const val CARPETA = "fotos"
    private const val LADO_MAX = 1280
    private const val CALIDAD = 85

    private fun carpeta(context: Context): File =
        File(context.filesDir, CARPETA).apply { mkdirs() }

    // Archivo vacío donde la cámara escribirá la foto
    fun archivoTemporal(context: Context): File =
        File(carpeta(context), "tmp_${System.currentTimeMillis()}.jpg").apply { createNewFile() }

    fun uriDe(context: Context, archivo: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)

    // Foto elegida de la galería. Devuelve la ruta final o null si falló.
    fun importarDesdeUri(context: Context, uri: Uri): String? {
        val destino = File(carpeta(context), "foto_${System.currentTimeMillis()}.jpg")
        val ok = procesar({ context.contentResolver.openInputStream(uri) }, destino)
        return if (ok) destino.absolutePath else { destino.delete(); null }
    }

    // Foto tomada con la cámara. Procesa el temporal y lo borra.
    fun importarDesdeArchivo(context: Context, temporal: File): String? {
        val destino = File(carpeta(context), "foto_${System.currentTimeMillis()}.jpg")
        val ok = procesar({ temporal.inputStream() }, destino)
        temporal.delete()
        return if (ok) destino.absolutePath else { destino.delete(); null }
    }

    private fun procesar(abrir: () -> InputStream?, destino: File): Boolean = try {
        val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        abrir()?.use { BitmapFactory.decodeStream(it, null, limites) }
        val mayor = maxOf(limites.outWidth, limites.outHeight)

        if (mayor <= 0) {
            false
        } else {
            var muestra = 1
            while (mayor / muestra > LADO_MAX) muestra *= 2

            val opciones = BitmapFactory.Options().apply { inSampleSize = muestra }
            val bitmap = abrir()?.use { BitmapFactory.decodeStream(it, null, opciones) }

            if (bitmap == null) {
                false
            } else {
                val grados = abrir()?.use { rotacionExif(it) } ?: 0
                val resultado = if (grados != 0) {
                    val matriz = Matrix().apply { postRotate(grados.toFloat()) }
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matriz, true)
                } else {
                    bitmap
                }
                destino.outputStream().use {
                    resultado.compress(Bitmap.CompressFormat.JPEG, CALIDAD, it)
                }
                true
            }
        }
    } catch (e: Throwable) {
        false
    }

    private fun rotacionExif(entrada: InputStream): Int = try {
        when (
            ExifInterface(entrada).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        ) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    } catch (e: Exception) {
        0
    }
}