package com.tecsup.lab10ejercicio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.lab10ejercicio.ui.theme.Lab10EjercicioTheme
import com.tecsup.lab10ejercicio.view.Naranja
import com.tecsup.lab10ejercicio.view.ProductFormScreen
import com.tecsup.lab10ejercicio.view.ProductListScreen
import com.tecsup.lab10ejercicio.view.Verde
import com.tecsup.lab10ejercicio.viewmodel.ProductViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab10EjercicioTheme {
                TiendaApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TiendaApp(viewModel: ProductViewModel = viewModel()) {
    val navController = rememberNavController()
    val rutaActual = navController.currentBackStackEntryAsState().value?.destination?.route
    val snackbarHostState = remember { SnackbarHostState() }

    // Cada operacion deja un mensaje con el verbo y el codigo HTTP
    LaunchedEffect(viewModel.mensaje) {
        viewModel.mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.mensajeMostrado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tienda DummyJSON", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    if (rutaActual != "lista") {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                },
                actions = {
                    if (rutaActual == "lista") {
                        IconButton(onClick = { viewModel.cargar() }) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Recargar")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Verde,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            if (rutaActual == "lista") {
                FloatingActionButton(
                    onClick = { navController.navigate("formulario/0") },
                    containerColor = Naranja,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Nuevo producto")
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "lista",
            modifier = Modifier.padding(padding)
        ) {
            composable("lista") {
                ProductListScreen(viewModel) { id -> navController.navigate("formulario/$id") }
            }
            composable(
                "formulario/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) {
                ProductFormScreen(viewModel, it.arguments!!.getInt("id")) { navController.popBackStack() }
            }
        }
    }
}
