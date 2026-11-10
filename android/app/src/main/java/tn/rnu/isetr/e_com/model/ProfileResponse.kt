package tn.rnu.isetr.e_com.model

data class ProfileResponse(
    val message: String,
    val user: ProfileUser
)

data class ProfileUser(
    val id: Int,
    val full_name: String,
    val email: String,
    val phone: String?,
    val address: String?,
    val created_at: String?,
    val role: String
)