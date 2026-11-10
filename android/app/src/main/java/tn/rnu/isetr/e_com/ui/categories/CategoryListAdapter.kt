package tn.rnu.isetr.e_com.ui.categories.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tn.rnu.isetr.e_com.databinding.ItemCategoryCardBinding
import tn.rnu.isetr.e_com.model.Category

class CategoryListAdapter(
    private val categories: List<Category>,
    private val productCounts: Map<Int, Int>,
    private val onCategoryClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(val binding: ItemCategoryCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CategoryViewHolder(
            ItemCategoryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        val count = productCounts[category.id] ?: 0

        with(holder.binding) {
            tvCategoryTitle.text = category.name
            tvCategoryItemCount.text = "$count produit${if (count == 1) "" else "s"}"
            root.setOnClickListener { onCategoryClick(category) }
        }
    }

    override fun getItemCount() = categories.size
}