package tn.rnu.isetr.e_com.repository

import tn.rnu.isetr.e_com.model.RegisterRequest
import tn.rnu.isetr.e_com.remote.RetrofitClient

class RegisterRepository {

    suspend fun register(
        fullName: String,
        email: String,
        password: String
    ) = RetrofitClient.apiService.register(
        RegisterRequest(fullName, email, password)
    )
}