package com.example.ciudadalerta.data.remote

import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // MockAPI:      "https://TU_ID.mockapi.io/api/v1/"
    // API local:    "http://10.0.2.2:5000/api/"   (10.0.2.2 = tu computadora vista desde el emulador)
    const val BASE_URL = "https://TU_ID.mockapi.io/api/v1/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val http = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    val reporteApi: ReporteApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(http)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ReporteApi::class.java)
    }
}