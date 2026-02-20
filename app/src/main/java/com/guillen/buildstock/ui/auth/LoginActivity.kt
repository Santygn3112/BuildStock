package com.guillen.buildstock.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.guillen.buildstock.R
import com.guillen.buildstock.databinding.ActivityLoginBinding
import com.guillen.buildstock.ui.main.MainActivity

// Actividad principal para el inicio de sesión de usuarios
class LoginActivity : AppCompatActivity() {

    // Enlace con la vista XML mediante ViewBinding
    private lateinit var binding: ActivityLoginBinding
    // ViewModel encargado de la lógica de autenticación
    private val viewModel: LoginViewModel by viewModels()

    // Método de ciclo de vida donde se inicializa la UI
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()

        // Configuración del botón de ingreso
        binding.btnEnter.setOnClickListener {
            val email = binding.etUser.text.toString()
            val pass = binding.etPassword.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                viewModel.login(email, pass)
            } else {
                Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Configura los observadores para responder a cambios en el ViewModel
    private fun setupObservers() {
        // Observa el estado del login para navegar a la pantalla principal
        viewModel.loginStatus.observe(this) { success ->
            if (success) {
                Toast.makeText(this, R.string.login_success_verifying, Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // Muestra mensajes de error en caso de fallo
        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }

        // Gestiona la visibilidad del estado de carga
        viewModel.isLoading.observe(this) { loading ->
            binding.btnEnter.isEnabled = !loading
        }
    }
}
