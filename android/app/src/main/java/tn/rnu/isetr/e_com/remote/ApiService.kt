package tn.rnu.isetr.e_com.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import tn.rnu.isetr.e_com.model.LoginRequest
import tn.rnu.isetr.e_com.model.LoginResponse

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>
}