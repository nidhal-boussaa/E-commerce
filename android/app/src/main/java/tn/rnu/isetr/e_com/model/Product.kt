package tn.rnu.isetr.e_com.model

data class Product(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val stock: Int,
    val image_url: String?,
    val is_active: Int,
    val category: String
)