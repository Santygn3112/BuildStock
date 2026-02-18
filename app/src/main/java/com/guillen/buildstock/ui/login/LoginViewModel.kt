package com.guillen.buildstock.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guillen.buildstock.data.model.User
import com.guillen.buildstock.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    // Cambiamos el nombre para que coincida con la llamada de abajo
    private val authRepository = AuthRepository()

    // 1. Cambiamos Result<Boolean> por Boolean para simplificar
    private val _loginStatus = MutableLiveData<Boolean>()
    val loginStatus: LiveData<Boolean> = _loginStatus

    // 2. Añadimos las variables que faltaban y que daban error
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _userProfile = MutableLiveData<User?>()
    val userProfile: LiveData<User?> = _userProfile

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            // Ahora 'success' es un Boolean directo del repo
            val success = authRepository.login(email, pass)

            if (success) {
                // Recuperamos el perfil para que el Fragment sepa si ir a Admin o Home
                val profile = authRepository.getUserProfile()
                _userProfile.value = profile
                _loginStatus.value = true
            } else {
                _loginStatus.value = false
                _errorMessage.value = "Correo o contraseña incorrectos"
            }
            _isLoading.value = false
        }
    }
}