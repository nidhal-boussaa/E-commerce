package tn.rnu.isetr.e_com.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tn.rnu.isetr.e_com.model.LoginResponse
import tn.rnu.isetr.e_com.repository.AuthRepository

class LoginViewModel : ViewModel() {

    private val repository = AuthRepository()

    val loginResult = MutableLiveData<LoginResponse>()
    val loginError = MutableLiveData<String>()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)
                val body = response.body()

                when {
                    !response.isSuccessful -> loginError.postValue("HTTP ${response.code()}")
                    body == null -> loginError.postValue("Réponse serveur vide")
                    else -> loginResult.postValue(body)
                }
            } catch (e: Exception) {
                loginError.postValue(e.message ?: "Erreur de connexion")
            }
        }
    }
}