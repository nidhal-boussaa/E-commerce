
package tn.rnu.isetr.e_com.ui.profil

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tn.rnu.isetr.e_com.databinding.ItemOrderProductBinding
import tn.rnu.isetr.e_com.model.OrderItem

class OrderProductAdapter(
    private val items: List<OrderItem>
) : RecyclerView.Adapter<OrderProductAdapter.ViewHolder>() {

    inner class ViewHolder(
        val binding: ItemOrderProductBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOrderProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val b = holder.binding

        b.tvProductName.text = item.product_name

        b.tvProductQtyPrice.text =
            "Quantité : ${item.quantity} × ${item.unit_price} DT"

        b.tvProductTotalPrice.text =
            "${item.quantity * item.unit_price} DT"
    }

    override fun getItemCount() = items.size
}
