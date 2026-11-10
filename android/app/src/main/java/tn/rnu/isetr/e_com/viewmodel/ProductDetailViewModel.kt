package tn.rnu.isetr.e_com.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tn.rnu.isetr.e_com.model.Product
import tn.rnu.isetr.e_com.repository.ProductDetailRepository

class ProductDetailViewModel : ViewModel() {

    private val repository = ProductDetailRepository()

    val product = MutableLiveData<Product>()
    val error = MutableLiveData<String>()

    fun loadProduct(id: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getProduct(id)
                if (response.isSuccessful) {
                    product.value = response.body()
                } else {
                    error.value = "Erreur lors du chargement du produit"
                }
            } catch (e: Exception) {
                error.value = e.message ?: "Erreur de connexion"
            }
        }
    }
}