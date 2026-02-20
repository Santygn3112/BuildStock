package com.guillen.buildstock.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.guillen.buildstock.R
import com.guillen.buildstock.databinding.ActivityAdminPanelBinding

class AdminPanelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminPanelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminPanelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarAdmin.setNavigationIcon(R.drawable.baseline_arrow_back_ios_new_24)
        binding.toolbarAdmin.setNavigationOnClickListener { finish() }

        binding.cardAdminInventory.setOnClickListener {
            startActivity(Intent(this, AdminInventoryActivity::class.java))
        }

        binding.cardAdminUsers.setOnClickListener {
            startActivity(Intent(this, AdminUsersActivity::class.java))
        }
    }
}