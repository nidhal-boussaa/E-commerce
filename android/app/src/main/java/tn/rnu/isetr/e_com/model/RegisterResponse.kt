package tn.rnu.isetr.e_com.model

data class RegisterResponse(
    val message: String,
    val token: String?,
    val user: User?
)