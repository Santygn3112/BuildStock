package com.guillen.buildstock.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.guillen.buildstock.R
import com.guillen.buildstock.data.repository.InventoryRepository
import com.guillen.buildstock.databinding.ActivityCategoryDetailBinding
import com.guillen.buildstock.ui.cart.CartManager
import com.guillen.buildstock.ui.inventory.ToolAdapter
import com.guillen.buildstock.ui.inventory.ToolDetailActivity
import kotlinx.coroutines.launch

// Actividad para mostrar las herramientas de una categoría específica
class CategoryDetailActivity : AppCompatActivity() {

    // Enlace con la vista XML
    private lateinit var binding: ActivityCategoryDetailBinding
    // Repositorio de inventario
    private val repository = InventoryRepository()
    // Adaptador para mostrar la lista de herramientas
    private lateinit var adapter: ToolAdapter

    // Inicialización de la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val categoryName = intent.getStringExtra("CATEGORY_NAME") ?: getString(R.string.title_no_category)

        setupToolbar(categoryName)
        setupRecyclerView()
        loadTools(categoryName)
    }

    // Configura la barra de herramientas con el nombre de la categoría
    private fun setupToolbar(categoryName: String) {
        binding.toolbarCategory.title = categoryName
        binding.toolbarCategory.setNavigationIcon(R.drawable.baseline_arrow_back_ios_new_24)
        binding.toolbarCategory.setNavigationOnClickListener {
            finish()
        }
    }

    // Inicializa el RecyclerView y define las acciones de los elementos
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
                if (tool.status.lowercase() == "disponible") {
                    CartManager.addTool(tool)
                    Toast.makeText(this, getString(R.string.msg_added_to_cart_format, tool.name), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, R.string.msg_tool_not_available, Toast.LENGTH_SHORT).show()
                }
            }
        )
        binding.rvCategoryTools.adapter = adapter
    }

    // Carga las herramientas correspondientes a la categoría seleccionada
    private fun loadTools(categoryName: String) {
        lifecycleScope.launch {
            val tools = repository.getToolsByCategory(categoryName)
            adapter.updateList(tools)
            binding.toolbarCategory.subtitle = getString(R.string.subtitle_elements_count, tools.size)
        }
    }
}
