package com.tecsup.lab10ejercicio.data

import com.google.gson.annotations.SerializedName

// GET /products no devuelve la lista directa, sino un objeto con la lista y la paginacion
data class ProductsResponse(
    @SerializedName("products") val products: List<ProductModel>,
    @SerializedName("total") val total: Int,
    @SerializedName("skip") val skip: Int,
    @SerializedName("limit") val limit: Int
)
