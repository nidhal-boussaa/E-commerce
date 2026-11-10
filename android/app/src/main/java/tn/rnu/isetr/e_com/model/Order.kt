package tn.rnu.isetr.e_com.model

data class Order(
    val id: Long,
    val total_amount: Double,
    val status: String,
    val delivery_address: String?,
    val payment_method: String,
    val created_at: String,
    val items: List<OrderItem>
)
