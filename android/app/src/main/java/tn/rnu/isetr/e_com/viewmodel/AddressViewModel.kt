
package tn.rnu.isetr.e_com.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tn.rnu.isetr.e_com.model.AddressItem
import tn.rnu.isetr.e_com.model.AddressRequest
import tn.rnu.isetr.e_com.model.AddressSaveResponse
import tn.rnu.isetr.e_com.repository.AddressRepository
import retrofit2.Response

class AddressViewModel : ViewModel() {

    private val repository = AddressRepository()

    private val _addresses = MutableLiveData<List<AddressItem>>()
    val addresses: LiveData<List<AddressItem>> = _addresses

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _saveSuccess = MutableLiveData<String?>()
    val saveSuccess: LiveData<String?> = _saveSuccess

    // Récupérer les adresses
    fun loadAddresses(token: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val response = repository.getAddresses(token)

                if (response.isSuccessful && response.body() != null) {
                    _addresses.value = response.body()!!.addresses
                } else {
                    _error.value = "Erreur : ${response.code()}"
                }

            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur de connexion"

            } finally {
                _loading.value = false
            }
        }
    }

    // Ajouter une adresse principale
    fun addAddress(token: String, request: AddressRequest) {
        saveAddress {
            repository.addAddress(token, request)
        }
    }

    // Modifier l'adresse principale
    fun updateAddress(token: String, request: AddressRequest) {
        saveAddress {
            repository.updateAddress(token, request)
        }
    }

    // Enregistrer l'adresse
    private fun saveAddress(
        action: suspend () -> Response<AddressSaveResponse>
    ) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _saveSuccess.value = null

            try {
                val response = action()

                if (response.isSuccessful && response.body() != null) {
                    _saveSuccess.value = response.body()!!.message
                } else {
                    _error.value = "Erreur : ${response.code()}"
                }

            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur de connexion"

            } finally {
                _loading.value = false
            }
        }
    }
    fun clearSaveSuccess() {
        _saveSuccess.value = null
    }
}
