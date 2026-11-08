package tn.rnu.isetr.e_com.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tn.rnu.isetr.e_com.repository.AuthRepository

class LoginViewModel : ViewModel() {

    private val repository = AuthRepository()

    val loginResult = MutableLiveData<String>()
    val loginError = MutableLiveData<String>()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)

                android.util.Log.d(
                    "LOGIN_TEST",
                    "HTTP CODE: ${response.code()}"
                )

                android.util.Log.d(
                    "LOGIN_TEST",
                    "BODY: ${response.body()}"
                )

                if (response.isSuccessful) {
                    loginResult.value = response.body()?.token
                } else {
                    loginError.value = "HTTP ${response.code()}"
                }

            } catch (e: Exception) {
                android.util.Log.e("LOGIN_TEST", "ERROR", e)
                loginError.value = e.toString()
            }
        }
    }
}