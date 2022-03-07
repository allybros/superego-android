package com.allybros.superego.unit

import com.google.gson.annotations.SerializedName

data class UploadImageRequest(
    @SerializedName("session-token")
    val sessionToken: String,

    @SerializedName("new-avatar-base64")
    val newAvatarBase64: String
)