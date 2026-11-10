
package tn.rnu.isetr.e_com.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import tn.rnu.isetr.e_com.R
import tn.rnu.isetr.e_com.databinding.FragmentCategoriesBinding
import tn.rnu.isetr.e_com.model.Category
import tn.rnu.isetr.e_com.model.Product
import tn.rnu.isetr.e_com.ui.categories.adapter.CategoryListAdapter
import tn.rnu.isetr.e_com.viewmodel.HomeViewModel

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by activityViewModels()

    private var allCategories = listOf<Category>()
    private var allProducts = listOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvCategoriesList.layoutManager =
            LinearLayoutManager(requireContext())

        observeViewModel()

        binding.etSearchCategory.doOnTextChanged { text, _, _, _ ->
            val search = text.toString().trim()
            displayCategories(
                allCategories.filter {
                    it.name.contains(search, ignoreCase = true)
                }
            )
        }

        viewModel.loadCategories()
        viewModel.loadProducts()
    }

    private fun observeViewModel() {

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            allCategories = categories
            binding.tvCategoryCount.text = "${categories.size} rayons"
            displayCategories(categories)
        }

        viewModel.products.observe(viewLifecycleOwner) { products ->
            allProducts = products
            displayCategories(allCategories)
        }

        viewModel.categoryError.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayCategories(categories: List<Category>) {

        val productCounts = categories.associate { category ->
            category.id to allProducts.count {
                it.category.equals(category.name, ignoreCase = true)
            }
        }

        binding.rvCategoriesList.adapter =
            CategoryListAdapter(categories, productCounts) { category ->

                val bundle = Bundle().apply {
                    putString("CATEGORY_NAME", category.name)
                }

                findNavController().navigate(R.id.nav_home, bundle)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
