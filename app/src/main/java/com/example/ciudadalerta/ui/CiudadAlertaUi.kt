package com.example.ciudadalerta.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.ciudadalerta.ui.navigation.RUTA_LOGIN
import com.example.ciudadalerta.ui.navigation.RUTA_REGISTRO
import com.example.ciudadalerta.ui.screens.AjustesScreen
import com.example.ciudadalerta.ui.screens.DetalleScreen
import com.example.ciudadalerta.ui.screens.ListaReportesScreen
import com.example.ciudadalerta.ui.screens.LoginScreen
import com.example.ciudadalerta.ui.screens.NuevoReporteScreen
import com.example.ciudadalerta.ui.screens.RegistroScreen
import com.example.ciudadalerta.ui.viewmodel.AuthViewModel
import com.example.ciudadalerta.ui.viewmodel.ReporteViewModel

@Composable
fun CiudadAlertaUi() {
    val navController = rememberNavController()
    val vm: ReporteViewModel = viewModel(factory = ReporteViewModel.Factory)
    val auth: AuthViewModel = viewModel(factory = AuthViewModel.Factory)
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route
    val compacto = LocalConfiguration.current.screenWidthDp < 600
    val mostrarMenu = rutaActual != null && rutaActual != RUTA_LOGIN && rutaActual != RUTA_REGISTRO
    val sesionIniciada = auth.sesionIniciada

    // La pantalla inicial se decide una sola vez
    val inicio = remember {
        if (auth.sesionIniciada) Destino.MisReportes.ruta else RUTA_LOGIN
    }

    // Si inicia o cierra sesión, se redirige
    LaunchedEffect(sesionIniciada) {
        val ruta = navController.currentDestination?.route ?: return@LaunchedEffect
        val enAuth = ruta == RUTA_LOGIN || ruta == RUTA_REGISTRO
        if (sesionIniciada && enAuth) {
            navController.navigate(Destino.MisReportes.ruta) {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        } else if (!sesionIniciada && !enAuth) {
            navController.navigate(RUTA_LOGIN) {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    val irA: (Destino) -> Unit = { d ->
        if (d == Destino.Nuevo) vm.nuevo()
        navController.navigate(d.ruta) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        bottomBar = {
            if (compacto && mostrarMenu) {
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
            if (!compacto && mostrarMenu) {
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
                startDestination = inicio,
                modifier = Modifier.weight(1f)
            ) {
                composable(RUTA_LOGIN) {
                    LoginScreen(
                        auth = auth,
                        onIrARegistro = { navController.navigate(RUTA_REGISTRO) }
                    )
                }
                composable(RUTA_REGISTRO) {
                    RegistroScreen(
                        auth = auth,
                        onIrALogin = { navController.popBackStack() }
                    )
                }
                composable(Destino.MisReportes.ruta) {
                    ListaReportesScreen(
                        vm = vm,
                        onAbrir = { id -> navController.navigate("$RUTA_DETALLE/$id") }
                    )
                }
                composable(Destino.Nuevo.ruta) {
                    NuevoReporteScreen(
                        vm = vm,
                        onGuardado = {
                            navController.navigate(Destino.MisReportes.ruta) {
                                popUpTo(navController.graph.findStartDestination().id)
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable(Destino.Ajustes.ruta) {
                    AjustesScreen(
                        nombreUsuario = auth.nombreUsuario,
                        onCerrarSesion = { auth.cerrarSesion() }
                    )
                }
                composable(
                    route = "$RUTA_DETALLE/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.IntType })
                ) { backStackEntry ->
                    DetalleScreen(
                        vm = vm,
                        id = backStackEntry.arguments!!.getInt("id"),
                        onVolver = { navController.popBackStack() },
                        onEditar = { navController.navigate(Destino.Nuevo.ruta) }
                    )
                }
            }
        }
    }
}