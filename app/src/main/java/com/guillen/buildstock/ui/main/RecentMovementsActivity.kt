package com.guillen.buildstock.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.guillen.buildstock.data.repository.InventoryRepository
import com.guillen.buildstock.databinding.ActivityRecentMovementsBinding
import kotlinx.coroutines.launch

class RecentMovementsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecentMovementsBinding
    private val repository = InventoryRepository()
    private lateinit var adapter: MovementAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecentMovementsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar la Toolbar
        binding.toolbarMovements.setNavigationIcon(android.R.drawable.ic_media_previous)
        binding.toolbarMovements.setNavigationOnClickListener { finish() }

        // Configurar el RecyclerView
        adapter = MovementAdapter(emptyList())
        binding.rvMovements.layoutManager = LinearLayoutManager(this)
        binding.rvMovements.adapter = adapter

        // Cargar los 8 últimos movimientos
        loadMovements()
    }

    private fun loadMovements() {
        lifecycleScope.launch {
            val recentMovements = repository.getRecentMovements(8) // Pasamos 8 como límite
            adapter.updateList(recentMovements)
        }
    }
}