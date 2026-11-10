
package tn.rnu.isetr.e_com.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import tn.rnu.isetr.e_com.R
import tn.rnu.isetr.e_com.databinding.FragmentFavoritesBinding
import tn.rnu.isetr.e_com.model.Product
import tn.rnu.isetr.e_com.ui.favorites.adapter.FavoriteAdapter
import tn.rnu.isetr.e_com.utils.FavoriteManager
import tn.rnu.isetr.e_com.viewmodel.HomeViewModel

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private lateinit var favoriteManager: FavoriteManager
    private lateinit var favoriteAdapter: FavoriteAdapter

    private val viewModel: HomeViewModel by activityViewModels()

    private var favoriteProducts = mutableListOf<Product>()
    private var allProducts = listOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoriteManager = FavoriteManager(requireContext())

        binding.rvFavorites.layoutManager =
            LinearLayoutManager(requireContext())

        observeViewModel()

        binding.btnExploreProducts.setOnClickListener {
            findNavController().navigate(R.id.nav_home)
        }

        viewModel.loadProducts()
    }

    private fun observeViewModel() {

        viewModel.products.observe(viewLifecycleOwner) { products ->
            allProducts = products
            loadFavoriteProducts()
        }
    }

    private fun updateUi() {

        val count = favoriteProducts.size
        val empty = count == 0

        binding.tvFavoritesCount.text =
            if (count == 1) "$count article" else "$count articles"

        binding.rvFavorites.visibility =
            if (empty) View.GONE else View.VISIBLE

        binding.layoutEmptyState.visibility =
            if (empty) View.VISIBLE else View.GONE
    }

    private fun loadFavoriteProducts() {

        val favoriteIds = favoriteManager.getFavorites()

        favoriteProducts = allProducts
            .filter { it.id in favoriteIds }
            .toMutableList()

        updateUi()

        if (favoriteProducts.isEmpty()) return

        favoriteAdapter = FavoriteAdapter(
            favoriteProducts,

            onRemove = { product ->
                favoriteManager.removeFavorite(product.id)
                favoriteAdapter.removeProduct(product)
                updateUi()

                toast("Supprimé des favoris")
            },

            onAddToCart = { product ->
                toast("${product.name} : ajout au panier bientôt disponible")
            }
        )

        binding.rvFavorites.adapter = favoriteAdapter
    }

    private fun toast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()

        if (::favoriteManager.isInitialized && allProducts.isNotEmpty()) {
            loadFavoriteProducts()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
