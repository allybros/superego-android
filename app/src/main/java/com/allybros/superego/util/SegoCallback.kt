package com.allybros.superego.util

import com.allybros.superego.unit.SegoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class SegoCallback : Callback<SegoResponse> {
    abstract override fun onResponse(call: Call<SegoResponse>, response: Response<SegoResponse>)

    override fun onFailure(call: Call<SegoResponse>, t: Throwable) {

    }
}