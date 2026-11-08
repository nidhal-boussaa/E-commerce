package tn.rnu.isetr.e_com.model

data class LoginResponse(
    val message: String,
    val token: String,
    val user: User
)

data class User(
    val id: Int,
    val full_name: String,
    val email: String,
    val role: String
)