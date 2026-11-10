package tn.rnu.isetr.e_com.ui.favorites.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tn.rnu.isetr.e_com.R
import tn.rnu.isetr.e_com.databinding.ItemFavoriteBinding
import tn.rnu.isetr.e_com.model.Product

class FavoriteAdapter(
    private val products: MutableList<Product>,
    private val onRemove: (Product) -> Unit,
    private val onAddToCart: (Product) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    class FavoriteViewHolder(val binding: ItemFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        FavoriteViewHolder(
            ItemFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val product = products[position]
        val context = holder.itemView.context
        val available = product.stock > 0 && product.is_active == 1

        with(holder.binding) {
            tvFavoriteName.text = product.name
            tvFavoriteCategory.text = product.category
            tvFavoritePrice.text = "${product.price} DT"

            tvFavoriteStock.text = if (available) "✓ En stock" else "Indisponible"
            tvFavoriteStock.visibility = View.VISIBLE

            Glide.with(context)
                .load(product.image_url)
                .placeholder(R.drawable.ic_shopping_bag)
                .error(R.drawable.ic_shopping_bag)
                .centerCrop()
                .into(ivFavoriteProduct)

            btnRemoveFavorite.setOnClickListener { onRemove(product) }

            btnAddToCart.setOnClickListener {
                if (available) onAddToCart(product)
                else Toast.makeText(context, "Produit indisponible", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = products.size

    fun removeProduct(product: Product) {
        val position = products.indexOfFirst { it.id == product.id }
        if (position == -1) return
        products.removeAt(position)
        notifyItemRemoved(position)
    }
}