package com.allybros.superego.retrofit

import com.allybros.superego.unit.EditProfileRequest
import com.allybros.superego.unit.SegoResponse
import com.allybros.superego.unit.UploadImageRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface SegoAPI {

    @POST("edit-profile.php")
    fun editProfile(@Body request: EditProfileRequest): Call<SegoResponse>

    @POST("upload.php")
    fun uploadImage(@Body request: UploadImageRequest): Call<SegoResponse>



}
