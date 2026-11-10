package tn.rnu.isetr.e_com.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Response
import tn.rnu.isetr.e_com.model.Category
import tn.rnu.isetr.e_com.model.Product
import tn.rnu.isetr.e_com.repository.CategoriesRepository
import tn.rnu.isetr.e_com.repository.ProductsRepository

class HomeViewModel : ViewModel() {

    private val categoriesRepository = CategoriesRepository()
    private val productsRepository = ProductsRepository()

    val categories = MutableLiveData<List<Category>>()
    val categoryError = MutableLiveData<String>()

    val products = MutableLiveData<List<Product>>()
    val productError = MutableLiveData<String>()

    private fun <T> load(
        request: suspend () -> Response<List<T>>,
        result: MutableLiveData<List<T>>,
        error: MutableLiveData<String>,
        errorMessage: String
    ) {
        viewModelScope.launch {
            try {
                val response = request()
                if (response.isSuccessful) {
                    result.value = response.body() ?: emptyList()
                } else {
                    error.value = errorMessage
                }
            } catch (e: Exception) {
                error.value = e.message
            }
        }
    }

    fun loadCategories() = load(
        { categoriesRepository.getCategories() },
        categories, categoryError,
        "Erreur lors du chargement des catégories"
    )

    fun loadProducts() = load(
        { productsRepository.getProducts() },
        products, productError,
        "Erreur lors du chargement des produits"
    )
}