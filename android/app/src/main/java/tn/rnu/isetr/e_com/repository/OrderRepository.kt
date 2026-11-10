package tn.rnu.isetr.e_com.repository

import tn.rnu.isetr.e_com.remote.ApiService

class OrderRepository(private val api: ApiService) {

    suspend fun getMyOrders(token: String) =
        api.getMyOrders("Bearer $token")
}