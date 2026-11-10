package tn.rnu.isetr.e_com.repository

import tn.rnu.isetr.e_com.remote.RetrofitClient

class CategoriesRepository {

    suspend fun getCategories() =
        RetrofitClient.apiService.getCategories()
}