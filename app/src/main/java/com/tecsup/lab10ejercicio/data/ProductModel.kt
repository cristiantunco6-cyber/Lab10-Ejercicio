package com.tecsup.lab10ejercicio.data

import com.google.gson.annotations.SerializedName

// Producto que devuelve el API de DummyJSON
data class ProductModel(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("title") val title: String = "",
    @SerializedName("description") val description: String? = "",
    @SerializedName("price") val price: Double = 0.0,
    @SerializedName("category") val category: String? = "",
    @SerializedName("rating") val rating: Double? = null,
    @SerializedName("thumbnail") val thumbnail: String? = null
)

// Cuerpo que se envia en el POST y en el PUT
data class ProductRequest(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Double,
    @SerializedName("category") val category: String
)

// Respuesta del DELETE: el producto con isDeleted y deletedOn
data class DeletedProduct(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("isDeleted") val isDeleted: Boolean,
    @SerializedName("deletedOn") val deletedOn: String?
)
