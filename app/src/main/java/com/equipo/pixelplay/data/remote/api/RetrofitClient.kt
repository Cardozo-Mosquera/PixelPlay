package com.equipo.pixelplay.data.remote.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Cliente Retrofit para FreeToGame.
 * OkHttp con logging BODY + GsonConverterFactory.
 * Expone una única instancia perezosa de [PixelPlayApiService].
 */
object RetrofitClient {

    private const val BASE_URL = "https://www.freetogame.com/api/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val api: PixelPlayApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PixelPlayApiService::class.java)
    }
}
