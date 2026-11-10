
package tn.rnu.isetr.e_com.model

data class OrderItem(
    val id: Long,
    val quantity: Int,
    val unit_price: Double,
    val product_name: String,
    val product_id: Long
)
