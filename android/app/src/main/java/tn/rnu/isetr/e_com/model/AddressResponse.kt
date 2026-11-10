
package tn.rnu.isetr.e_com.model

// Réponse de récupération des adresses
data class AddressResponse(
    val message: String,
    val addresses: List<AddressItem>
)

// Une adresse dans la liste
data class AddressItem(
    val address: String,
    val is_default: Boolean
)

// Données envoyées au Backend
data class AddressRequest(
    val address: String,
    val city: String,
    val postal_code: String,
    val country: String
)

// Réponse après ajout ou modification
data class AddressSaveResponse(
    val message: String,
    val address: String
)
