package com.allybros.superego.unit

import com.google.gson.annotations.SerializedName

data class EditProfileRequest(
    @SerializedName("session-token")
    val sessionToken: String,
    @SerializedName("new-username")
    val newUsername: String,
    @SerializedName("new-email")
    val newEmail: String,
    @SerializedName("new-user-bio")
    val newUserBio: String
)
