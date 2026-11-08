package tn.rnu.isetr.e_com.repository

import tn.rnu.isetr.e_com.model.LoginRequest
import tn.rnu.isetr.e_com.remote.RetrofitClient

class AuthRepository {

    suspend fun login(email: String, password: String) =
        RetrofitClient.apiService.login(
            LoginRequest(email, password)
        )
}