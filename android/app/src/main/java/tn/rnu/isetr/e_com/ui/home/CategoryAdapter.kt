package tn.rnu.isetr.e_com.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import tn.rnu.isetr.e_com.R
import tn.rnu.isetr.e_com.databinding.ItemCategoryBinding
import tn.rnu.isetr.e_com.model.Category

class CategoryAdapter(
    private val categories: List<Category>,
    private val onCategoryClick: (String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition = 0

    class CategoryViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CategoryViewHolder(
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        val context = holder.itemView.context
        val selected = position == selectedPosition

        with(holder.binding) {
            tvCategoryName.text = category.name
            cardCategory.setCardBackgroundColor(
                ContextCompat.getColor(context, if (selected) R.color.red_primary else R.color.surface_white)
            )
            tvCategoryName.setTextColor(
                ContextCompat.getColor(context, if (selected) R.color.white else R.color.text_primary)
            )
        }

        holder.itemView.setOnClickListener {
            val newPosition = holder.bindingAdapterPosition
            if (newPosition == RecyclerView.NO_POSITION) return@setOnClickListener

            val oldPosition = selectedPosition
            selectedPosition = newPosition
            notifyItemChanged(oldPosition)
            notifyItemChanged(newPosition)

            onCategoryClick(categories[newPosition].name)
        }
    }

    override fun getItemCount() = categories.size
}