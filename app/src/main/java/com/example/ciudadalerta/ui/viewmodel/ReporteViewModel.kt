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

data class FormularioState(
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: Categoria = Categoria.BACHE,
    val urgente: Boolean = false,
    val hora: String = "",
    val editando: Boolean = false
)

class ReporteViewModel(private val repo: ReporteRepository) : ViewModel() {

    val reportes: StateFlow<List<ReporteEntity>> = repo.obtenerReportes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    var form by mutableStateOf(FormularioState())
        private set

    // Reporte original cuando se está editando (null = reporte nuevo)
    private var original: ReporteEntity? = null

    fun onTitulo(v: String) { form = form.copy(titulo = v) }
    fun onDescripcion(v: String) { form = form.copy(descripcion = v) }
    fun onCategoria(v: Categoria) { form = form.copy(categoria = v) }
    fun onUrgente(v: Boolean) { form = form.copy(urgente = v) }
    fun onHora(v: String) { form = form.copy(hora = v) }

    fun reporte(id: Int) = repo.obtenerReporte(id)
    fun historial(id: Int) = repo.historial(id)

    fun nuevo() {
        original = null
        form = FormularioState()
    }

    fun editar(r: ReporteEntity) {
        original = r
        form = FormularioState(
            titulo = r.titulo,
            descripcion = r.descripcion,
            categoria = Categoria.entries.firstOrNull { it.name == r.categoria } ?: Categoria.BACHE,
            urgente = r.urgente,
            hora = r.hora,
            editando = true
        )
    }

    fun guardar(onListo: () -> Unit) {
        val f = form
        val base = original
        viewModelScope.launch {
            if (base == null) {
                repo.crearReporte(
                    ReporteEntity(
                        titulo = f.titulo.trim(),
                        descripcion = f.descripcion.trim(),
                        categoria = f.categoria.name,
                        hora = f.hora,
                        urgente = f.urgente
                    )
                )
            } else {
                repo.actualizarReporte(
                    base.copy(
                        titulo = f.titulo.trim(),
                        descripcion = f.descripcion.trim(),
                        categoria = f.categoria.name,
                        hora = f.hora,
                        urgente = f.urgente
                    )
                )
            }
            nuevo()
            onListo()
        }
    }

    fun eliminar(id: Int, onListo: () -> Unit) {
        viewModelScope.launch {
            repo.eliminarReporte(id)
            onListo()
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