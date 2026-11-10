
package tn.rnu.isetr.e_com.ui.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import tn.rnu.isetr.e_com.R
import tn.rnu.isetr.e_com.databinding.FragmentProductDetailBinding
import tn.rnu.isetr.e_com.model.Product
import tn.rnu.isetr.e_com.viewmodel.ProductDetailViewModel

class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductDetailViewModel by viewModels()

    private var currentProduct: Product? = null
    private var quantity = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        val productId = arguments?.getInt("PRODUCT_ID", -1) ?: -1

        if (productId == -1) {
            Toast.makeText(requireContext(), "Produit introuvable", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        viewModel.product.observe(viewLifecycleOwner) { product ->
            product ?: return@observe
            currentProduct = product
            displayProduct(product)
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
            }
        }

        binding.btnMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                updateTotalPrice()
            }
        }

        binding.btnPlus.setOnClickListener {
            val product = currentProduct ?: return@setOnClickListener

            if (quantity < product.stock && product.is_active == 1) {
                quantity++
                updateTotalPrice()
            }
        }

        viewModel.loadProduct(productId)
    }

    private fun displayProduct(product: Product) = with(binding) {

        tvProductDetailName.text = product.name
        tvProductDetailPrice.text = "${product.price} DT"
        tvProductCategory.text = product.category
        tvProductDescription.text =
            product.description ?: "Aucune description disponible"

        Glide.with(this@ProductDetailFragment)
            .load(product.image_url)
            .placeholder(R.drawable.ic_shopping_bag)
            .error(R.drawable.ic_shopping_bag)
            .centerInside()
            .into(ivProductImageLarge)

        val available = product.stock > 0 && product.is_active == 1

        tvStockBadge.text = if (available) "✓ En stock" else "Indisponible"
        tvStockHint.text =
            if (available) "Maximum disponible : ${product.stock}"
            else "Stock indisponible"

        layoutQuantitySelector.isEnabled = available
        btnAddToCart.isEnabled = available

        if (!available) quantity = 0

        updateTotalPrice()
    }

    private fun updateTotalPrice() {
        val product = currentProduct ?: return

        binding.tvQuantity.text = quantity.toString()

        binding.tvTotalPrice.text =
            String.format("%.2f DT", product.price * quantity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
