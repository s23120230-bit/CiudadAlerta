package com.example.ciudadalerta.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ciudadalerta.ui.navigation.Destino
import com.example.ciudadalerta.ui.navigation.RUTA_DETALLE
import com.example.ciudadalerta.ui.screens.AjustesScreen
import com.example.ciudadalerta.ui.screens.DetalleScreen
import com.example.ciudadalerta.ui.screens.ListaReportesScreen
import com.example.ciudadalerta.ui.screens.NuevoReporteScreen
import com.example.ciudadalerta.ui.viewmodel.ReporteViewModel

@Composable
fun CiudadAlertaUi() {
    val navController = rememberNavController()
    val vm: ReporteViewModel = viewModel(factory = ReporteViewModel.Factory)
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route
    val compacto = LocalConfiguration.current.screenWidthDp < 600

    val irA: (Destino) -> Unit = { d ->
        navController.navigate(d.ruta) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        bottomBar = {
            if (compacto) {
                NavigationBar {
                    Destino.menu.forEach { d ->
                        NavigationBarItem(
                            selected = rutaActual == d.ruta,
                            onClick = { irA(d) },
                            icon = { Icon(d.icono, contentDescription = null) },
                            label = { Text(stringResource(d.titulo)) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Row(Modifier.padding(padding)) {
            if (!compacto) {
                NavigationRail {
                    Destino.menu.forEach { d ->
                        NavigationRailItem(
                            selected = rutaActual == d.ruta,
                            onClick = { irA(d) },
                            icon = { Icon(d.icono, contentDescription = null) },
                            label = { Text(stringResource(d.titulo)) }
                        )
                    }
                }
            }
            NavHost(
                navController = navController,
                startDestination = Destino.MisReportes.ruta,
                modifier = Modifier.weight(1f)
            ) {
                composable(Destino.MisReportes.ruta) {
                    ListaReportesScreen(
                        vm = vm,
                        onAbrir = { id -> navController.navigate("$RUTA_DETALLE/$id") }
                    )
                }
                composable(Destino.Nuevo.ruta) {
                    NuevoReporteScreen(vm = vm, onGuardado = { irA(Destino.MisReportes) })
                }
                composable(Destino.Ajustes.ruta) { AjustesScreen() }
                composable(
                    route = "$RUTA_DETALLE/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.IntType })
                ) { backStackEntry ->
                    DetalleScreen(
                        vm = vm,
                        id = backStackEntry.arguments!!.getInt("id"),
                        onVolver = { navController.popBackStack() }
                    )    
                }
            }
        }
    }
}