
package tn.rnu.isetr.e_com.ui.profil

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tn.rnu.isetr.e_com.R
import tn.rnu.isetr.e_com.databinding.ItemOrderBinding
import tn.rnu.isetr.e_com.model.Order

class OrderAdapter(
    private val onClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    private val orders = mutableListOf<Order>()

    fun updateOrders(newOrders: List<Order>) {
        orders.clear()
        orders.addAll(newOrders)
        notifyDataSetChanged()
    }


    private fun setOrderStatus(
        status: String,
        binding: ItemOrderBinding
    ) {
        val (label, icon, color) = when (status) {
            "PENDING" -> Triple(
                "En attente",
                "⏳",
                R.color.orange
            )

            "PREPARING" -> Triple(
                "En préparation",
                "📦",
                R.color.blue
            )

            "SHIPPED" -> Triple(
                "Expédiée",
                "🚚",
                R.color.purple
            )

            "DELIVERED" -> Triple(
                "Livrée",
                "✓",
                R.color.green
            )

            "CANCELLED" -> Triple(
                "Annulée",
                "✕",
                R.color.red_primary
            )

            else -> Triple(
                status,
                "•",
                R.color.text_secondary
            )
        }

        binding.tvOrderStatus.text = "$icon $label"
        binding.tvOrderStatus.setTextColor(
            binding.root.context.getColor(color)
        )
    }


    inner class OrderViewHolder(
        val binding: ItemOrderBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: OrderViewHolder,
        position: Int
    ) {
        val order = orders[position]
        val b = holder.binding

        b.tvOrderId.text = "#CMD-${order.id}"
        b.tvOrderDate.text = order.created_at
        b.tvOrderItemsCount.text = "${order.items.size} articles"
        setOrderStatus(order.status, b)
        b.tvOrderTotal.text = "${order.total_amount} DT"

        b.cardOrder.setOnClickListener {
            onClick(order)
        }
    }

    override fun getItemCount(): Int = orders.size
}
