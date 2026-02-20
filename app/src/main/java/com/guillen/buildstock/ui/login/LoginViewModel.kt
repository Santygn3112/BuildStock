package com.guillen.buildstock.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guillen.buildstock.data.model.User
import com.guillen.buildstock.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _loginStatus = MutableLiveData<Boolean>()
    val loginStatus: LiveData<Boolean> = _loginStatus

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

            val success = authRepository.login(email, pass)

            if (success) {
                val profile = authRepository.getUserProfile()
                if (profile != null) {
                    _userProfile.value = profile
                    _loginStatus.value = true
                } else {
                    authRepository.signOut()
                    _loginStatus.value = false
                    _errorMessage.value = "Usuario no encontrado o acceso revocado"
                }
            } else {
                _loginStatus.value = false
                _errorMessage.value = "Correo o contraseña incorrectos"
            }
            _isLoading.value = false
        }
    }
}