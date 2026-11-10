package tn.rnu.isetr.e_com.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tn.rnu.isetr.e_com.remote.RetrofitClient
import tn.rnu.isetr.e_com.repository.OrderRepository
import tn.rnu.isetr.e_com.model.Order

class OrderViewModel : ViewModel() {

    private val repository = OrderRepository(RetrofitClient.apiService)

    var orders = androidx.lifecycle.MutableLiveData<List<Order>>()

    var error = androidx.lifecycle.MutableLiveData<String>()

    fun loadOrders(token: String) {
        viewModelScope.launch {
            try {
                val response = repository.getMyOrders(token)

                if (response.isSuccessful) {
                    orders.value = response.body()?.orders ?: emptyList()
                } else {
                    error.value = "Erreur lors du chargement des commandes"
                }
            } catch (e: Exception) {
                error.value = e.message
            }
        }
    }
}