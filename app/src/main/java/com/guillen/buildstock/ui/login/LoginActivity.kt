package com.guillen.buildstock.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.guillen.buildstock.R
import com.guillen.buildstock.databinding.ActivityLoginBinding
import com.guillen.buildstock.ui.dashboard.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()

        binding.btnEnter.setOnClickListener {
            val email = binding.etUser.text.toString()
            val pass = binding.etPassword.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                // 1. Corregido el nombre de la función
                viewModel.login(email, pass)
            } else {
                Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        // 2. Observamos el estado del login (Boolean)
        viewModel.loginStatus.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Login OK, verificando perfil...", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // 3. Observamos los errores para mostrarlos en el Toast
        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }

        // 4. Observamos si está cargando para ocultar/mostrar elementos (Opcional)
        viewModel.isLoading.observe(this) { loading ->
            // Puedes ocultar el botón o mostrar un ProgressBar aquí
            binding.btnEnter.isEnabled = !loading
        }
    }
}