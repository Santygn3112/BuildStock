package com.guillen.buildstock.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.guillen.buildstock.R
import com.guillen.buildstock.data.repository.AuthRepository
import com.guillen.buildstock.databinding.ActivityAdminUsersBinding
import kotlinx.coroutines.launch

class AdminUsersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminUsersBinding
    private val authRepository = AuthRepository()
    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarAdminUsers.setNavigationIcon(R.drawable.baseline_arrow_back_ios_new_24)
        binding.toolbarAdminUsers.setNavigationOnClickListener { finish() }

        binding.btnAddNewUserTop.setOnClickListener {
            startActivity(Intent(this, AddUserActivity::class.java))
        }

        setupRecyclerView()
        loadUsers()
    }

    private fun setupRecyclerView() {
        adapter = UserAdapter(
            users = emptyList(),
            onEditClick = { user ->
                val intent = android.content.Intent(this, AddUserActivity::class.java).apply {
                    putExtra("USER_NAME", user.name)
                    putExtra("USER_EMAIL", user.email)
                    putExtra("USER_ROLE", user.role)
                    putExtra("USER_PHONE", user.phone)
                }
                startActivity(intent)
            },
            onDeleteClick = { user ->
                confirmDelete(user.email ?: "", user.name)
            }
        )
        binding.rvAdminUsers.adapter = adapter
    }

    private fun confirmDelete(userId: String, userName: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_delete_user_title)
            .setMessage(getString(R.string.dialog_delete_user_message, userName))
            .setPositiveButton(R.string.btn_delete) { _, _ ->
                lifecycleScope.launch {
                    val success = authRepository.deleteUser(userId)
                    if (success) {
                        Toast.makeText(this@AdminUsersActivity, R.string.msg_user_deleted, Toast.LENGTH_SHORT).show()
                        loadUsers()
                    } else {
                        Toast.makeText(this@AdminUsersActivity, R.string.msg_error_delete, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton(R.string.btn_cancel, null)
            .show()
    }

    private fun loadUsers() {
        lifecycleScope.launch {
            val users = authRepository.getAllUsers()
            adapter.updateList(users)
        }
    }

    override fun onResume() {
        super.onResume()
        loadUsers()
    }
}