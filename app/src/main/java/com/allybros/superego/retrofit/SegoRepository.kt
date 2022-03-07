package com.allybros.superego.retrofit

import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor;
import androidx.lifecycle.MutableLiveData
import com.allybros.superego.unit.ConstantValues
import com.allybros.superego.unit.EditProfileRequest
import com.allybros.superego.unit.SegoResponse
import com.allybros.superego.util.SegoCallback
import retrofit2.Retrofit

abstract class SegoRepository {

    protected var api: SegoAPI? = null

    companion object {
        private const val BASE_URL = ConstantValues.API_ROOT
    }

    init {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SegoAPI::class.java)
    }

}