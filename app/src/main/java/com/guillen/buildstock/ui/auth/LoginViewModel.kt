package com.guillen.buildstock.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guillen.buildstock.R
import com.guillen.buildstock.data.model.User
import com.guillen.buildstock.data.repository.AuthRepository
import kotlinx.coroutines.launch

// ViewModel que gestiona la lógica de negocio del inicio de sesión
class LoginViewModel : ViewModel() {

    // Repositorio para operaciones de autenticación
    private val authRepository = AuthRepository()

    // LiveData para notificar el éxito o fallo del login
    private val _loginStatus = MutableLiveData<Boolean>()
    val loginStatus: LiveData<Boolean> = _loginStatus

    // LiveData para indicar si hay una operación en curso
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData para exponer mensajes de error mediante recursos de cadena
    private val _errorMessage = MutableLiveData<Int?>()
    val errorMessage: LiveData<Int?> = _errorMessage

    // LiveData que contiene la información del usuario autenticado
    private val _userProfile = MutableLiveData<User?>()
    val userProfile: LiveData<User?> = _userProfile

    // Inicia el proceso de autenticación con correo y contraseña
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
                    _errorMessage.value = R.string.login_error_user_not_found
                }
            } else {
                _loginStatus.value = false
                _errorMessage.value = R.string.login_error_credentials
            }
            _isLoading.value = false
        }
    }
}
