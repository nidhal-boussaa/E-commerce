package tn.rnu.isetr.e_com.model

data class RegisterRequest(
    val full_name: String,
    val email: String,
    val password: String
)