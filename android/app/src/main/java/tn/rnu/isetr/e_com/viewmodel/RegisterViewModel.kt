package tn.rnu.isetr.e_com.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tn.rnu.isetr.e_com.repository.RegisterRepository

class RegisterViewModel : ViewModel() {

    private val repository = RegisterRepository()

    val registerResult = MutableLiveData<String>()
    val registerError = MutableLiveData<String>()

    fun register(fullName: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.register(fullName, email, password)
                if (response.isSuccessful) {
                    registerResult.value = response.body()?.message ?: "Inscription réussie"
                } else {
                    registerError.value = "Erreur lors de l'inscription"
                }
            } catch (e: Exception) {
                registerError.value = e.message ?: "Erreur de connexion"
            }
        }
    }
}