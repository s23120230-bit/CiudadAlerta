package com.example.ciudadalerta.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ciudadalerta.CiudadAlertaApp
import com.example.ciudadalerta.data.local.ReporteEntity
import com.example.ciudadalerta.data.model.Categoria
import com.example.ciudadalerta.data.repository.ReporteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

data class FormularioState(
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: Categoria = Categoria.BACHE,
    val urgente: Boolean = false,
    val hora: String = "",
    val fotoPath: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val editando: Boolean = false
)

enum class EstadoSync { INACTIVO, SINCRONIZANDO, OK, ERROR }

class ReporteViewModel(private val repo: ReporteRepository) : ViewModel() {
    val reportes: StateFlow<List<ReporteEntity>> = repo.obtenerReportes()
        .map { listaCompleta ->
            // Esto se ejecuta cada vez que hay un cambio.
            // Aquí lee el ID real del usuario en ese momento.
            val idActual = repo.usuarioActualId()
            listaCompleta.filter { it.usuarioId == idActual }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    var form by mutableStateOf(FormularioState())
        private set

    var estadoSync by mutableStateOf(EstadoSync.INACTIVO)
        private set

    // Reporte original cuando se está editando (null = reporte nuevo)
    private var original: ReporteEntity? = null

    private var fotoOriginal: String? = null

    // escucha que se active cuando el usuario entre o salga
    private val authListener = FirebaseAuth.AuthStateListener { auth: FirebaseAuth ->
        val user: FirebaseUser? = auth.currentUser
        if (user != null) {
            // Si hay un usuario logueado, dispara la sincronización
            sincronizar()
        }
    }

    // Empieza a escuchar en cuanto se crea el ViewModel
    init {
        FirebaseAuth.getInstance().addAuthStateListener(authListener)
    }

    // dejar de escuchar si el ViewModel se destruye
    override fun onCleared() {
        super.onCleared()
        FirebaseAuth.getInstance().removeAuthStateListener(authListener)
    }

    fun onTitulo(v: String) { form = form.copy(titulo = v) }
    fun onDescripcion(v: String) { form = form.copy(descripcion = v) }
    fun onCategoria(v: Categoria) { form = form.copy(categoria = v) }
    fun onUrgente(v: Boolean) { form = form.copy(urgente = v) }
    fun onHora(v: String) { form = form.copy(hora = v) }

    fun onFoto(ruta: String?) { form = form.copy(fotoPath = ruta) }

    fun onUbicacion(lat: Double?, lon: Double?) { form = form.copy(latitud = lat, longitud = lon) }

    fun foto(id: Int): Flow<String?> =
        repo.multimedia(id).map { lista -> lista.firstOrNull()?.uriLocal }

    fun reporte(id: Int) = repo.obtenerReporte(id)
    fun historial(id: Int) = repo.historial(id)

    fun nuevo() {
        original = null
        fotoOriginal = null
        form = FormularioState()
    }

    fun editar(r: ReporteEntity) {
        original = r
        fotoOriginal = null
        form = FormularioState(
            titulo = r.titulo,
            descripcion = r.descripcion,
            categoria = Categoria.entries.firstOrNull { it.name == r.categoria } ?: Categoria.BACHE,
            urgente = r.urgente,
            hora = r.hora,
            latitud = r.latitud,
            longitud = r.longitud,
            editando = true
        )
        // La foto vive en otra tabla, se carga aparte
        viewModelScope.launch {
            val ruta = repo.primeraFoto(r.id)
            if (original?.id == r.id) {
                fotoOriginal = ruta
                form = form.copy(fotoPath = ruta)
            }
        }
    }

    fun guardar(onListo: () -> Unit) {
        val f = form
        val base = original
        val fotoAnterior = fotoOriginal
        viewModelScope.launch {
            if (base == null) {
                val idNuevo = repo.crearReporte(
                    ReporteEntity(
                        titulo = f.titulo.trim(),
                        descripcion = f.descripcion.trim(),
                        categoria = f.categoria.name,
                        hora = f.hora,
                        latitud = f.latitud,
                        longitud = f.longitud,
                        urgente = f.urgente
                    )
                )
                repo.guardarFoto(idNuevo.toInt(), f.fotoPath)
            } else {
                repo.actualizarReporte(
                    base.copy(
                        titulo = f.titulo.trim(),
                        descripcion = f.descripcion.trim(),
                        categoria = f.categoria.name,
                        hora = f.hora,
                        latitud = f.latitud,
                        longitud = f.longitud,
                        urgente = f.urgente
                    )
                )
                if (f.fotoPath != fotoAnterior) repo.guardarFoto(base.id, f.fotoPath)
            }
            nuevo()
            onListo()                    // la interfaz no espera a la red
            repo.sincronizarPendientes() // intenta subir al servidor
        }
    }

    fun eliminar(id: Int, onListo: () -> Unit) {
        viewModelScope.launch {
            val remoteId = repo.eliminarLocal(id)
            onListo()
            repo.eliminarRemoto(remoteId)
        }
    }

    // Sube los pendientes y luego descarga lo del servidor
    fun sincronizar() {
        if (estadoSync == EstadoSync.SINCRONIZANDO) return
        viewModelScope.launch {
            estadoSync = EstadoSync.SINCRONIZANDO
            repo.sincronizarPendientes()
            val ok = repo.recuperarDeApi(repo.usuarioActualId())
            estadoSync = if (ok) EstadoSync.OK else EstadoSync.ERROR
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as CiudadAlertaApp
                ReporteViewModel(app.repository)
            }
        }
    }
}