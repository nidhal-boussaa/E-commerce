package tn.rnu.isetr.e_com.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tn.rnu.isetr.e_com.model.ProfileUser
import tn.rnu.isetr.e_com.model.UpdateProfileRequest
import tn.rnu.isetr.e_com.repository.ProfileRepository

class ProfileViewModel : ViewModel() {

    private val repository = ProfileRepository()

    private val _profile = MutableLiveData<ProfileUser>()
    val profile: LiveData<ProfileUser> = _profile

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private fun launchLoading(block: suspend () -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            try {
                block()
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur de connexion"
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadProfile(token: String) = launchLoading {
        val response = repository.getProfile(token)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            _profile.value = body.user
        } else {
            _error.value = "Erreur : ${response.code()}"
        }
    }

    fun updateProfile(token: String, fullName: String, email: String, phone: String) =
        launchLoading {
            val request = UpdateProfileRequest(
                full_name = fullName,
                email = email,
                phone = phone.ifBlank { null }
            )
            val response = repository.updateProfile(token, request)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                _message.value = body.message
                loadProfile(token)
            } else {
                _error.value = "Erreur : ${response.code()}"
            }
        }
}