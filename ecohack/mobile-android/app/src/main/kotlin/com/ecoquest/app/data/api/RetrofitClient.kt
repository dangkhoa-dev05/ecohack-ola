package com.ecoquest.app.data.api

import com.ecoquest.app.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val token = AuthTokenStore.getToken()
                val request = chain.request().newBuilder().apply {
                    if (!token.isNullOrBlank()) {
                        header("Authorization", "Bearer $token")
                    }
                }.build()
                chain.proceed(request)
            }
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG)
                        HttpLoggingInterceptor.Level.BODY
                    else
                        HttpLoggingInterceptor.Level.NONE
                }
            )
            .build()
    }

    private val baseUrls: List<String> by lazy {
        listOf(
            BuildConfig.BASE_URL,
            "http://10.0.2.2:8080/",
            "http://10.0.3.2:8080/"
        ).distinct()
    }

    private fun createApi(baseUrl: String): EcoQuestApi {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EcoQuestApi::class.java)
    }

    val api: EcoQuestApi by lazy {
        createApi(BuildConfig.BASE_URL)
    }

    suspend fun <T> withFallback(block: suspend (EcoQuestApi) -> T): T {
        var lastException: IOException? = null

        for (baseUrl in baseUrls) {
            try {
                return block(createApi(baseUrl))
            } catch (exception: IOException) {
                lastException = exception
            }
        }

        throw IOException(
            "Failed to connect to backend. Tried: ${baseUrls.joinToString()}",
            lastException
        )
    }
}
