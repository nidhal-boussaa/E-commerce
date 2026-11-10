
package tn.rnu.isetr.e_com.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import tn.rnu.isetr.e_com.R
import tn.rnu.isetr.e_com.databinding.FragmentHomeBinding
import tn.rnu.isetr.e_com.model.Category
import tn.rnu.isetr.e_com.model.Product
import tn.rnu.isetr.e_com.ui.home.adapter.CategoryAdapter
import tn.rnu.isetr.e_com.ui.home.adapter.ProductAdapter
import tn.rnu.isetr.e_com.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by activityViewModels()

    private var allProducts = listOf<Product>()
    private var selectedCategory = "Tous"
    private var searchText = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(
            inflater, container, false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialiser la catégorie sélectionnée
        selectedCategory = arguments?.getString("CATEGORY_NAME") ?: "Tous"

        // Initialiser les RecyclerView
        binding.rvCategories.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.rvProducts.layoutManager =
            GridLayoutManager(requireContext(), 2)

        // Navigation vers Categories
        binding.tvSeeAllCategories.setOnClickListener {
            findNavController().navigate(R.id.nav_categories)
        }

        // Observer les données
        observeViewModel()

        // Recherche des produits
        binding.etSearch.doOnTextChanged { text, _, _, _ ->

            searchText = text.toString().trim()

            filterProducts()
        }

        // Charger les données
        viewModel.loadCategories()
        viewModel.loadProducts()
    }

    private fun observeViewModel() {

        viewModel.categories.observe(viewLifecycleOwner) { categories ->

            binding.rvCategories.adapter =
                CategoryAdapter(listOf(Category(-1, "Tous")) + categories) { name ->

                    selectedCategory = name

                    filterProducts()
                }
        }

        viewModel.products.observe(viewLifecycleOwner) { products ->

            allProducts = products

            filterProducts()
        }

        val showError = { error: String? ->

            Toast.makeText(
                requireContext(),
                error,
                Toast.LENGTH_SHORT
            ).show()
        }

        viewModel.categoryError.observe(viewLifecycleOwner, showError)
        viewModel.productError.observe(viewLifecycleOwner, showError)
    }

    private fun filterProducts() {

        binding.rvProducts.adapter = ProductAdapter(
            allProducts.filter {
                (selectedCategory == "Tous" ||
                        it.category.equals(selectedCategory, ignoreCase = true)) &&
                        it.name.contains(searchText, ignoreCase = true)
            }
        ) { product ->

            findNavController().navigate(
                R.id.action_global_product_detail,
                bundleOf("PRODUCT_ID" to product.id)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
