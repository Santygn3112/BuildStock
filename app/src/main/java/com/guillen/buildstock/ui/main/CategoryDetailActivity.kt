package com.guillen.buildstock.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.guillen.buildstock.data.repository.InventoryRepository
import com.guillen.buildstock.databinding.ActivityCategoryDetailBinding
import com.guillen.buildstock.ui.dashboard.ToolAdapter
import com.guillen.buildstock.ui.detail.ToolDetailActivity
import kotlinx.coroutines.launch

class CategoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryDetailBinding
    private val repository = InventoryRepository()
    private lateinit var adapter: ToolAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recuperamos la categoría pulsada
        val categoryName = intent.getStringExtra("CATEGORY_NAME") ?: "Sin Categoría"

        setupToolbar(categoryName)
        setupRecyclerView()
        loadTools(categoryName)
    }

    private fun setupToolbar(categoryName: String) {
        binding.toolbarCategory.title = categoryName
        // Botón de retroceso nativo
        binding.toolbarCategory.setNavigationIcon(android.R.drawable.ic_media_previous) // Icono temporal
        binding.toolbarCategory.setNavigationOnClickListener {
            finish() // Cierra la pantalla y vuelve al Dashboard
        }
    }

    private fun setupRecyclerView() {
        // Ahora pasamos los parámetros de forma explícita para que Kotlin no se confunda
        adapter = ToolAdapter(
            tools = emptyList(),
            onItemClick = { tool ->
                // Acción 1: Abrir el detalle de la herramienta
                val intent = Intent(this, ToolDetailActivity::class.java).apply {
                    putExtra("TOOL_ID", tool.id)
                }
                startActivity(intent)
            },
            onAddToCartClick = { tool ->
                // Acción 2: Botón de añadir al carrito
                // Por ahora mostramos un mensaje, más adelante lo guardaremos en la base de datos
                android.widget.Toast.makeText(this, "Añadido al carrito: ${tool.name}", android.widget.Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvCategoryTools.adapter = adapter
    }

    private fun loadTools(categoryName: String) {
        lifecycleScope.launch {
            val tools = repository.getToolsByCategory(categoryName)
            adapter.updateList(tools)

            // Actualizamos el subtítulo con la cantidad de elementos
            binding.toolbarCategory.subtitle = "${tools.size} elementos"
        }
    }
}