package com.guillen.buildstock.ui.admin

import android.os.Bundle
import com.guillen.buildstock.R
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.guillen.buildstock.data.repository.AuthRepository
import com.guillen.buildstock.databinding.ActivityAddUserBinding
import kotlinx.coroutines.launch

// Actividad para registrar nuevos usuarios o editar existentes
class AddUserActivity : AppCompatActivity() {

    // Enlace con la vista XML
    private lateinit var binding: ActivityAddUserBinding
    // Repositorio de autenticación
    private val authRepository = AuthRepository()
    // Email del usuario si se está en modo edición
    private var userEmailToEdit: String? = null

    // Inicialización de componentes y listeners
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

    // Verifica si la actividad se abrió para editar un usuario existente
    private fun checkForEditMode() {
        userEmailToEdit = intent.getStringExtra("USER_EMAIL")

        if (userEmailToEdit != null) {
            supportActionBar?.setTitle(R.string.title_edit_user)
            binding.btnCreateUser.setText(R.string.btn_update_user)

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
            binding.etUserPassword.setHint(R.string.hint_password_locked)
        }
    }

    // Valida los campos y guarda la información del usuario en Firebase
    private fun saveUserData() {
        val name = binding.etUserName.text.toString()
        val phone = binding.etUserPhone.text.toString()
        val role = if (binding.rbAdmin.isChecked) "admin" else "operario"

        if (name.isBlank()) {
            Toast.makeText(this, R.string.error_name_required, Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            Toast.makeText(this@AddUserActivity, R.string.msg_processing, Toast.LENGTH_SHORT).show()

            if (userEmailToEdit != null) {
                val success = authRepository.updateUserProfile(userEmailToEdit!!, name, role, phone)
                if (success) {
                    Toast.makeText(this@AddUserActivity, R.string.msg_user_updated, Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@AddUserActivity, R.string.error_db_update, Toast.LENGTH_LONG).show()
                }
            } else {
                val email = binding.etUserEmail.text.toString()
                val password = binding.etUserPassword.text.toString()

                if (email.isBlank() || password.length < 6) {
                    Toast.makeText(this@AddUserActivity, R.string.error_email_password_req, Toast.LENGTH_LONG).show()
                    return@launch
                }

                val success = authRepository.registerUserAsAdmin(this@AddUserActivity, name, email, password, role, phone)
                if (success) {
                    Toast.makeText(this@AddUserActivity, R.string.msg_user_created, Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@AddUserActivity, R.string.error_user_creation, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
