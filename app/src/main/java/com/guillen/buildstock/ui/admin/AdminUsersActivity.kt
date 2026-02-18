package com.guillen.buildstock.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
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

        binding.toolbarAdminUsers.setNavigationIcon(android.R.drawable.ic_media_previous)
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
                    putExtra("USER_PHONE", user.phone) // <-- PASAMOS EL TELÉFONO
                }
                startActivity(intent)
            },
            onDeleteClick = { user ->
                confirmDelete(user.email ?: "", user.name) // Pasamos el email como ID (o el campo que uses como clave)
            }
        )
        binding.rvAdminUsers.adapter = adapter
    }

    private fun confirmDelete(userId: String, userName: String) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Usuario")
            .setMessage("¿Estás seguro de que quieres borrar el acceso a '$userName'?")
            .setPositiveButton("Borrar") { _, _ ->
                lifecycleScope.launch {
                    val success = authRepository.deleteUser(userId)
                    if (success) {
                        Toast.makeText(this@AdminUsersActivity, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                        loadUsers() // Recargar la lista
                    } else {
                        Toast.makeText(this@AdminUsersActivity, "Error al eliminar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // 1. Crea esta función para cargar los datos
    private fun loadUsers() {
        lifecycleScope.launch {
            // Mostramos algún indicador de carga si quieres
            val users = authRepository.getAllUsers()
            adapter.updateList(users) // Asumiendo que tu adapter tiene updateList
        }
    }

    // 2. Sobrescribe el método onResume
    override fun onResume() {
        super.onResume()
        // Cada vez que volvemos a esta pantalla (al hacer finish() en la otra),
        // pedimos los usuarios de nuevo a Firebase.
        loadUsers()
    }
}