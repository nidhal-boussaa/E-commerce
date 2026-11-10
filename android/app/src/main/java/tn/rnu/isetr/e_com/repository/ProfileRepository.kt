package tn.rnu.isetr.e_com.repository

import tn.rnu.isetr.e_com.model.UpdateProfileRequest
import tn.rnu.isetr.e_com.remote.RetrofitClient

class ProfileRepository {

    suspend fun getProfile(token: String) =
        RetrofitClient.apiService.getProfile("Bearer $token")

    suspend fun updateProfile(
        token: String,
        request: UpdateProfileRequest
    ) = RetrofitClient.apiService.updateProfile(
        "Bearer $token",
        request
    )
}