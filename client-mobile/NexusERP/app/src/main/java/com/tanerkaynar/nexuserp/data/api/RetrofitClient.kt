package com.tanerkaynar.nexuserp.data.api

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private var appContext: Context? = null
    private var retrofit: Retrofit? = null
    private var apiService: ApiService? = null
    private const val BASE_URL = "https://minierp-api-0yyj.onrender.com/"

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    fun getApiService(): ApiService {
        if (apiService == null) {
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            apiService = retrofit!!.create(ApiService::class.java)
        }
        return apiService!!
    }
}