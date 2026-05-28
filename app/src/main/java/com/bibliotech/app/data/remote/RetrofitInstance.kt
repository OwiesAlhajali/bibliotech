package com.bibliotech.app.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private const val BASE_URL = "https://openlibrary.org/"

    // إضافة OkHttpClient لزيادة وقت الاستجابة
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // زيادة وقت الاتصال
        .readTimeout(30, TimeUnit.SECONDS)    // زيادة وقت انتظار الداتا
        .build()

    val api: OpenLibraryApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // ربط الـ client بالـ Retrofit
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenLibraryApiService::class.java)
    }
}