package com.equipo.pixelplay.data.remote.api

import okhttp3.Interceptor
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

    // FreeToGame rechaza algunas peticiones sin User-Agent; lo fijamos siempre.
    private val userAgentInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .header("User-Agent", "PixelPlay/1.0")
            .build()
        chain.proceed(request)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(userAgentInterceptor)
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
