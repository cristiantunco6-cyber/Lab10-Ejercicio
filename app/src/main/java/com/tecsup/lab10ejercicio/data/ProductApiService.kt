package com.tecsup.lab10ejercicio.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApiService {
    @GET("products")
    suspend fun getProducts(
        @Query("limit") limit: Int = 20,
        @Query("select") select: String = "title,description,price,category,rating,thumbnail"
    ): ProductsResponse

    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: Int): Response<ProductModel>

    @Headers("Content-Type: application/json")
    @POST("products/add")
    suspend fun addProduct(@Body producto: ProductRequest): Response<ProductModel>

    @Headers("Content-Type: application/json")
    @PUT("products/{id}")
    suspend fun updateProduct(@Path("id") id: Int, @Body producto: ProductRequest): Response<ProductModel>

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Response<DeletedProduct>
}
