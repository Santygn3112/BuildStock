package com.guillen.buildstock.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.guillen.buildstock.data.model.Movement
import com.guillen.buildstock.data.repository.InventoryRepository
import com.guillen.buildstock.databinding.ActivityRecentMovementsBinding
import kotlinx.coroutines.launch

// Actividad para mostrar el historial reciente de movimientos
class RecentMovementsActivity : AppCompatActivity() {

    // Enlace con la vista XML
    private lateinit var binding: ActivityRecentMovementsBinding
    // Repositorio de inventario
    private val repository = InventoryRepository()
    // Adaptador para mostrar los movimientos
    private lateinit var adapter: MovementAdapter

    // Inicialización de la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecentMovementsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuración de la barra de herramientas
        binding.toolbarMovements.setNavigationIcon(android.R.drawable.ic_media_previous)
        binding.toolbarMovements.setNavigationOnClickListener { finish() }

        // Configuración del RecyclerView
        adapter = MovementAdapter(emptyList())
        binding.rvMovements.layoutManager = LinearLayoutManager(this)
        binding.rvMovements.adapter = adapter

        // Carga inicial de datos
        loadMovements()
    }

    // Obtiene y muestra los últimos movimientos registrados
    private fun loadMovements() {
        lifecycleScope.launch {
            val recentMovements = repository.getRecentMovements(8)
            adapter.updateList(recentMovements)
        }
    }
}
