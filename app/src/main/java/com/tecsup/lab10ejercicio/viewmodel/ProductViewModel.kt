package com.tecsup.lab10ejercicio.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lab10ejercicio.data.ProductModel
import com.tecsup.lab10ejercicio.data.ProductRepository
import com.tecsup.lab10ejercicio.data.ProductRequest
import kotlinx.coroutines.launch

data class ProductUiState(
    val productos: List<ProductModel> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null
)

class ProductViewModel(
    private val repositorio: ProductRepository = ProductRepository()
) : ViewModel() {

    var uiState by mutableStateOf(ProductUiState())
        private set

    // Mensaje con el verbo HTTP y el codigo de respuesta, se muestra en un Snackbar
    var mensaje by mutableStateOf<String?>(null)
        private set

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            uiState = uiState.copy(cargando = true, error = null)
            try {
                val respuesta = repositorio.listar()
                uiState = uiState.copy(productos = respuesta.products, cargando = false)
                mensaje = "GET /products → 200 · ${respuesta.products.size} de ${respuesta.total} productos"
            } catch (e: Exception) {
                uiState = uiState.copy(cargando = false, error = "No se pudo conectar con el API: ${e.message}")
            }
        }
    }

    fun buscar(id: Int): ProductModel? = uiState.productos.find { it.id == id }

    fun crear(producto: ProductRequest) {
        viewModelScope.launch {
            try {
                val respuesta = repositorio.crear(producto)
                val creado = respuesta.body()
                if (respuesta.isSuccessful && creado != null) {
                    // DummyJSON siempre responde con el id 195, si ya existe se usa el siguiente libre
                    val idLocal = if (uiState.productos.any { it.id == creado.id })
                        uiState.productos.maxOf { it.id } + 1 else creado.id
                    uiState = uiState.copy(productos = listOf(creado.copy(id = idLocal)) + uiState.productos)
                    mensaje = "POST /products/add → ${respuesta.code()} · creado con id ${creado.id}"
                } else {
                    mensaje = "POST /products/add → ${respuesta.code()}"
                }
            } catch (e: Exception) {
                mensaje = "Error al crear: ${e.message}"
            }
        }
    }

    fun actualizar(id: Int, producto: ProductRequest) {
        viewModelScope.launch {
            try {
                val anterior = buscar(id) ?: return@launch
                val respuesta = repositorio.actualizar(id, producto)
                val editado = anterior.copy(
                    title = producto.title,
                    description = producto.description,
                    price = producto.price,
                    category = producto.category
                )
                uiState = uiState.copy(productos = uiState.productos.map { if (it.id == id) editado else it })
                mensaje = if (respuesta.isSuccessful)
                    "PUT /products/$id → ${respuesta.code()} · actualizado"
                else
                    "PUT /products/$id → ${respuesta.code()} · el API no guarda productos nuevos, se actualizó en la app"
            } catch (e: Exception) {
                mensaje = "Error al actualizar: ${e.message}"
            }
        }
    }

    fun eliminar(id: Int) {
        viewModelScope.launch {
            try {
                val respuesta = repositorio.eliminar(id)
                uiState = uiState.copy(productos = uiState.productos.filter { it.id != id })
                mensaje = if (respuesta.isSuccessful)
                    "DELETE /products/$id → ${respuesta.code()} · isDeleted = ${respuesta.body()?.isDeleted}"
                else
                    "DELETE /products/$id → ${respuesta.code()} · el API no guarda productos nuevos, se eliminó en la app"
            } catch (e: Exception) {
                mensaje = "Error al eliminar: ${e.message}"
            }
        }
    }

    fun mensajeMostrado() {
        mensaje = null
    }
}
