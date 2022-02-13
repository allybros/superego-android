package com.allybros.superego.retrofit

import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit


object RetrofitService {
    private const val BASE_URL = "https://api.themoviedb.org"
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val `interface`: SegoAPI
        get() = retrofit.create(SegoAPI::class.java)
}