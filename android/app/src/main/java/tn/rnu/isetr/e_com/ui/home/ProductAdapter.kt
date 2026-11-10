
package tn.rnu.isetr.e_com.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tn.rnu.isetr.e_com.R
import tn.rnu.isetr.e_com.databinding.ItemProductBinding
import tn.rnu.isetr.e_com.model.Product
import tn.rnu.isetr.e_com.utils.FavoriteManager

class ProductAdapter(
    private val products: List<Product>,
    private val onProductClick: (Product) -> Unit = {}
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ProductViewHolder(
            ItemProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {

        val product = products[position]
        val context = holder.itemView.context
        val favoriteManager = FavoriteManager(context)

        with(holder.binding) {

            tvProductName.text = product.name
            tvProductPrice.text = "${product.price} DT"

            Glide.with(context)
                .load(product.image_url)
                .placeholder(R.drawable.ic_shopping_bag)
                .error(R.drawable.ic_shopping_bag)
                .centerCrop()
                .into(ivProductImage)

            fun updateFavoriteIcon(isFavorite: Boolean) {
                btnFavorite.setImageResource(
                    if (isFavorite) R.drawable.ic_favorite_filled
                    else R.drawable.ic_favorite_border
                )
            }

            updateFavoriteIcon(favoriteManager.isFavorite(product.id))

            btnFavorite.setOnClickListener {
                val isFavorite = favoriteManager.toggleFavorite(product.id)
                updateFavoriteIcon(isFavorite)

                Toast.makeText(
                    context,
                    if (isFavorite) "Ajouté aux favoris ❤️"
                    else "Supprimé des favoris",
                    Toast.LENGTH_SHORT
                ).show()
            }

            root.setOnClickListener {
                onProductClick(product)
            }
        }
    }

    override fun getItemCount() = products.size
}
