package com.allybros.superego.retrofit

import com.allybros.superego.unit.EditProfileRequest
import com.allybros.superego.unit.StatusResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query


interface SegoAPI {

    @POST("/edit-profile.php")
    fun editProfile(@Body request: EditProfileRequest): Call<StatusResponse>



}
