package tn.rnu.isetr.e_com.model

data class OrderResponse(
    val message: String,
    val orders: List<Order>
)