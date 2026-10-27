package com.tecsup.lab10ejercicio.data

// Capa de datos: el ViewModel pide los datos aqui y no conoce a Retrofit
class ProductRepository(private val api: ProductApiService = RetrofitClient.api) {

    suspend fun listar() = api.getProducts()

    suspend fun crear(producto: ProductRequest) = api.addProduct(producto)

    suspend fun actualizar(id: Int, producto: ProductRequest) = api.updateProduct(id, producto)

    suspend fun eliminar(id: Int) = api.deleteProduct(id)
}
