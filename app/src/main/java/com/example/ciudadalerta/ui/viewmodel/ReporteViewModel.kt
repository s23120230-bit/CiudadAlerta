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
    val hora: String = ""
)

class ReporteViewModel(private val repo: ReporteRepository) : ViewModel() {

    val reportes: StateFlow<List<ReporteEntity>> = repo.obtenerReportes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    var form by mutableStateOf(FormularioState())
        private set

    fun onTitulo(v: String) { form = form.copy(titulo = v) }
    fun onDescripcion(v: String) { form = form.copy(descripcion = v) }
    fun onCategoria(v: Categoria) { form = form.copy(categoria = v) }
    fun onUrgente(v: Boolean) { form = form.copy(urgente = v) }
    fun onHora(v: String) { form = form.copy(hora = v) }

    fun reporte(id: Int) = repo.obtenerReporte(id)

    fun guardar(onListo: () -> Unit) {
        val f = form
        viewModelScope.launch {
            repo.crearReporte(
                ReporteEntity(
                    titulo = f.titulo.trim(),
                    descripcion = f.descripcion.trim(),
                    categoria = f.categoria.name,
                    hora = f.hora,
                    urgente = f.urgente
                )
            )
            form = FormularioState()
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