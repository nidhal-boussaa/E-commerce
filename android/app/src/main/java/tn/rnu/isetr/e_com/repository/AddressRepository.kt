
package tn.rnu.isetr.e_com.repository

import tn.rnu.isetr.e_com.model.AddressRequest
import tn.rnu.isetr.e_com.remote.RetrofitClient

class AddressRepository {

    // Récupérer les adresses
    suspend fun getAddresses(token: String) =
        RetrofitClient.apiService.getAddresses("Bearer $token")

    // Ajouter une adresse principale
    suspend fun addAddress(
        token: String,
        request: AddressRequest
    ) = RetrofitClient.apiService.addAddress(
        "Bearer $token",
        request
    )

    // Modifier l'adresse principale
    suspend fun updateAddress(
        token: String,
        request: AddressRequest
    ) = RetrofitClient.apiService.updateAddress(
        "Bearer $token",
        request
    )
}
