package com.tuempresa.aprendeapp.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // CAMBIAR ESTA URL POR API DE MOCKAPI.IO
    private const val BASE_URL = "https://6906ed9db1879c890ed8582f.mockapi.io/api/v1/recursos"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}