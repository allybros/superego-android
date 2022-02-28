package com.allybros.superego.activity.editprofile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.allybros.superego.retrofit.ServiceRepository
import com.allybros.superego.unit.EditProfileRequest
import com.allybros.superego.unit.StatusResponse


class EditProfileVM(application: Application) : AndroidViewModel(application) {

    private val serviceRepository: ServiceRepository = ServiceRepository()
    val responseLiveData: MutableLiveData<StatusResponse?>? = serviceRepository.responseLiveData

    fun editProfile(request: EditProfileRequest){
        serviceRepository.editProfile(request)
    }
}