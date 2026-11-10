package tn.rnu.isetr.e_com.model

data class UpdateProfileRequest(
    val full_name: String,
    val email: String,
    val phone: String?
)