package com.guillen.buildstock.ui.admin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.guillen.buildstock.data.repository.AuthRepository
import com.guillen.buildstock.databinding.ActivityAddUserBinding
import kotlinx.coroutines.launch

class AddUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddUserBinding
    private val authRepository = AuthRepository()
    private var userEmailToEdit: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarAddUser)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarAddUser.setNavigationOnClickListener { finish() }

        checkForEditMode()

        binding.btnCreateUser.setOnClickListener {
            saveUserData()
        }
    }

    private fun checkForEditMode() {
        userEmailToEdit = intent.getStringExtra("USER_EMAIL")

        if (userEmailToEdit != null) {
            supportActionBar?.title = "Editar Usuario"
            binding.btnCreateUser.text = "ACTUALIZAR USUARIO"

            binding.etUserName.setText(intent.getStringExtra("USER_NAME"))
            binding.etUserEmail.setText(userEmailToEdit)
            binding.etUserPhone.setText(intent.getStringExtra("USER_PHONE"))

            val role = intent.getStringExtra("USER_ROLE")
            if (role?.lowercase() == "admin") {
                binding.rbAdmin.isChecked = true
            } else {
                binding.rbOperario.isChecked = true
            }

            binding.etUserEmail.isEnabled = false
            binding.etUserPassword.isEnabled = false
            binding.etUserPassword.hint = "No modificable por seguridad"
        }
    }

    private fun saveUserData() {
        val name = binding.etUserName.text.toString()
        val phone = binding.etUserPhone.text.toString()
        val role = if (binding.rbAdmin.isChecked) "admin" else "operario"

        if (name.isBlank()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            // Mostramos un aviso de que estamos trabajando
            Toast.makeText(this@AddUserActivity, "Procesando...", Toast.LENGTH_SHORT).show()

            if (userEmailToEdit != null) {
                // MODO EDICIÓN
                val success = authRepository.updateUserProfile(userEmailToEdit!!, name, role, phone)
                if (success) {
                    Toast.makeText(this@AddUserActivity, "Usuario actualizado correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    // AHORA SÍ SALDRÁ AVISO SI FALLA
                    Toast.makeText(this@AddUserActivity, "Error al actualizar en la base de datos", Toast.LENGTH_LONG).show()
                }
            } else {
                // MODO CREACIÓN
                val email = binding.etUserEmail.text.toString()
                val password = binding.etUserPassword.text.toString()

                if (email.isBlank() || password.length < 6) {
                    Toast.makeText(this@AddUserActivity, "El correo es obligatorio y la clave debe tener 6 caracteres", Toast.LENGTH_LONG).show()
                    return@launch
                }

                val success = authRepository.registerUserAsAdmin(this@AddUserActivity, name, email, password, role, phone)
                if (success) {
                    Toast.makeText(this@AddUserActivity, "Usuario creado con éxito", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    // AHORA SÍ SALDRÁ AVISO SI FALLA (Ej: correo ya registrado)
                    Toast.makeText(this@AddUserActivity, "Error al crear: revisa si el correo ya existe", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}