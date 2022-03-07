package com.allybros.superego.activity.editprofile

import com.allybros.superego.retrofit.SegoRepository
import com.allybros.superego.unit.EditProfileRequest
import com.allybros.superego.unit.UploadImageRequest
import com.allybros.superego.util.SegoCallback

class EditProfileRepository: SegoRepository() {

    fun editProfile(request: EditProfileRequest, callback: SegoCallback) {
        api?.editProfile(request)?.enqueue(callback)
    }

    fun uploadImage(request: UploadImageRequest, callback: SegoCallback) {
        api?.uploadImage(request)?.enqueue(callback)
    }
}