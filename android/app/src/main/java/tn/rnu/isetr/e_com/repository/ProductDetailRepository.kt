package tn.rnu.isetr.e_com.repository

import tn.rnu.isetr.e_com.remote.RetrofitClient

class ProductDetailRepository {

    suspend fun getProduct(id: Int) =
        RetrofitClient.apiService.getProduct(id)
}