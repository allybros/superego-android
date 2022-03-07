package com.allybros.superego.activity.editprofile

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.allybros.superego.base.SegoVM
import com.allybros.superego.unit.EditProfileRequest
import com.allybros.superego.unit.SegoResponse
import com.allybros.superego.unit.UploadImageRequest
import com.allybros.superego.util.SegoCallback
import retrofit2.Call
import retrofit2.Response


class EditProfileVM(application: Application) : SegoVM(application) {

    private val segoRepository = EditProfileRepository()
    val editProfileResponseLiveData: MutableLiveData<SegoResponse> = MutableLiveData<SegoResponse>()
    val uploadImageResponseLiveData: MutableLiveData<SegoResponse> = MutableLiveData<SegoResponse>()


    fun editProfile(request: EditProfileRequest){
        segoRepository.editProfile(request, object : SegoCallback() {
            override fun onResponse(call: Call<SegoResponse>, response: Response<SegoResponse>) {
                editProfileResponseLiveData.postValue(response.body())
            }
        })
    }

    fun uploadImage(request: UploadImageRequest){
        segoRepository.uploadImage(request, object : SegoCallback() {
            override fun onResponse(call: Call<SegoResponse>, response: Response<SegoResponse>) {
                uploadImageResponseLiveData.postValue(response.body())
            }
        })
    }
}