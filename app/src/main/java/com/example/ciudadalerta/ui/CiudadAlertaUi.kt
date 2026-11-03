package com.example.ciudadalerta.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ciudadalerta.R
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
import com.example.ciudadalerta.ui.viewmodel.EstadoSync
import com.example.ciudadalerta.ui.viewmodel.ReporteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CiudadAlertaUi() {
    val navController = rememberNavController()
    val vm: ReporteViewModel = viewModel(factory = ReporteViewModel.Factory)
    val auth: AuthViewModel = viewModel(factory = AuthViewModel.Factory)
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route
    val compacto = LocalConfiguration.current.screenWidthDp < 600
    val enLogin = rutaActual == RUTA_LOGIN
    val enRegistro = rutaActual == RUTA_REGISTRO
    val enDetalle = rutaActual?.startsWith(RUTA_DETALLE) == true
    val mostrarMenu = rutaActual != null && !enLogin && !enRegistro
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

    val tituloBarra: String = when {
        rutaActual == Destino.MisReportes.ruta -> stringResource(R.string.app_name)
        rutaActual == Destino.Nuevo.ruta -> stringResource(
            if (vm.form.editando) R.string.titulo_editar else R.string.menu_nuevo
        )
        rutaActual == Destino.Ajustes.ruta -> stringResource(R.string.menu_ajustes)
        enDetalle -> stringResource(R.string.detalle_titulo)
        enRegistro -> stringResource(R.string.registro_titulo)
        else -> stringResource(R.string.app_name)
    }

    Scaffold(
        topBar = {
            if (!enLogin) {
                CenterAlignedTopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (rutaActual == Destino.MisReportes.ruta) {
                                Icon(
                                    imageVector = Icons.Default.LocationCity,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                            }
                            Text(
                                text = tituloBarra,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    navigationIcon = {
                        if (enDetalle || enRegistro) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.volver)
                                )
                            }
                        }
                    },
                    actions = {
                        if (rutaActual == Destino.MisReportes.ruta) {
                            IconButton(
                                onClick = { vm.sincronizar() },
                                enabled = vm.estadoSync != EstadoSync.SINCRONIZANDO
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = stringResource(R.string.sincronizar),
                                    tint = if (vm.estadoSync == EstadoSync.SINCRONIZANDO) {
                                        MaterialTheme.colorScheme.outline
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    }
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        floatingActionButton = {
            if (rutaActual == Destino.MisReportes.ruta) {
                ExtendedFloatingActionButton(
                    onClick = { irA(Destino.Nuevo) },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text(stringResource(R.string.menu_nuevo)) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        bottomBar = {
            if (compacto && mostrarMenu) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    Destino.menu.forEach { d ->
                        NavigationBarItem(
                            selected = rutaActual == d.ruta,
                            onClick = { irA(d) },
                            icon = { Icon(d.icono, contentDescription = null) },
                            label = { Text(stringResource(d.titulo), fontWeight = FontWeight.Medium) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Row(Modifier.padding(padding)) {
            if (!compacto && mostrarMenu) {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.surface,
                    header = {
                        Icon(
                            imageVector = Icons.Default.LocationCity,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(16.dp).size(32.dp)
                        )
                    }
                ) {
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
                        onAbrir = { id -> navController.navigate("$RUTA_DETALLE/$id") },
                        onCrearNuevo = { irA(Destino.Nuevo) }
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
