package com.english.tutor.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.english.tutor.BuildConfig

object NetworkModule {

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        )
        .addInterceptor { chain: okhttp3.Interceptor.Chain ->
            try {
                chain.proceed(chain.request())
            } catch (e: Exception) {
                throw when (e) {
                    is java.net.SocketTimeoutException ->
                        java.net.SocketTimeoutException("⏱️ Timeout: ${e.message}")
                    is java.net.UnknownHostException ->
                        java.net.UnknownHostException("❌ Host: ${e.message}")
                    else -> e
                }
            }
        }
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)  // ⭐ Usa Constants
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: OllamaApi = retrofit.create(OllamaApi::class.java)
}