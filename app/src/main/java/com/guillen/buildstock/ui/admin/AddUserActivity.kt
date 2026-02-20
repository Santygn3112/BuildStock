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