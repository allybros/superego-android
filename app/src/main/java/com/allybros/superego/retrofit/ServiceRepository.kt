package com.allybros.superego.retrofit

import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor;
import androidx.lifecycle.MutableLiveData
import com.allybros.superego.unit.ConstantValues
import com.allybros.superego.unit.EditProfileRequest
import com.allybros.superego.unit.StatusResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class ServiceRepository {

    private var api: SegoAPI? = null
    var responseLiveData: MutableLiveData<StatusResponse?>? = null

    companion object {
        private const val BASE_URL = ConstantValues.API_ROOT
    }

    init {
        responseLiveData = MutableLiveData<StatusResponse?>()
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

    fun editProfile(request: EditProfileRequest) {
        api?.editProfile(request)
            ?.enqueue(object : Callback<StatusResponse?> {
                override fun onResponse(
                    call: Call<StatusResponse?>?,
                    response: Response<StatusResponse?>
                ) {
                    if (response.body() != null) {
                        responseLiveData?.postValue(response.body())
                    }
                }

                override fun onFailure(call: Call<StatusResponse?>?, t: Throwable?) {
                    responseLiveData?.postValue(null)
                }
            })
    }
}