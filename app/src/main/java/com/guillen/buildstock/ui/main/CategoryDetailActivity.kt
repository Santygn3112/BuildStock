package com.guillen.buildstock.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.guillen.buildstock.data.repository.InventoryRepository
import com.guillen.buildstock.databinding.ActivityCategoryDetailBinding
import com.guillen.buildstock.ui.cart.CartManager
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

        val categoryName = intent.getStringExtra("CATEGORY_NAME") ?: "Sin Categoría"

        setupToolbar(categoryName)
        setupRecyclerView()
        loadTools(categoryName)
    }

    private fun setupToolbar(categoryName: String) {
        binding.toolbarCategory.title = categoryName
        binding.toolbarCategory.setNavigationIcon(android.R.drawable.ic_media_previous)
        binding.toolbarCategory.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = ToolAdapter(
            tools = emptyList(),
            onItemClick = { tool ->
                val intent = Intent(this, ToolDetailActivity::class.java).apply {
                    putExtra("TOOL_ID", tool.id)
                }
                startActivity(intent)
            },
            onAddToCartClick = { tool ->
                // Verificación inicial de stock antes de enviar al Manager
                if (tool.stock > 0) {
                    CartManager.addToCart(tool)
                    Toast.makeText(this, "Añadido al carrito: ${tool.name}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Sin stock disponible", Toast.LENGTH_SHORT).show()
                }
            }
        )
        binding.rvCategoryTools.adapter = adapter
    }

    private fun loadTools(categoryName: String) {
        lifecycleScope.launch {
            val tools = repository.getToolsByCategory(categoryName)
            adapter.updateList(tools)
            binding.toolbarCategory.subtitle = "${tools.size} elementos"
        }
    }
}