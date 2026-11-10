package tn.rnu.isetr.e_com.repository

import tn.rnu.isetr.e_com.remote.RetrofitClient

class ProductsRepository {

    suspend fun getProducts() =
        RetrofitClient.apiService.getProducts()
}