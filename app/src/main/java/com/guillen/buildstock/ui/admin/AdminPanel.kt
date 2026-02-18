package com.guillen.buildstock.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.guillen.buildstock.databinding.ActivityAdminPanelBinding

class AdminPanelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminPanelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminPanelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarAdmin.setNavigationIcon(android.R.drawable.ic_media_previous)
        binding.toolbarAdmin.setNavigationOnClickListener { finish() }

        // Navegar a Inventario
        binding.cardAdminInventory.setOnClickListener {
            startActivity(Intent(this, AdminInventoryActivity::class.java))
        }

        // Navegar a Usuarios
        binding.cardAdminUsers.setOnClickListener {
            startActivity(Intent(this, AdminUsersActivity::class.java))
        }
    }
}