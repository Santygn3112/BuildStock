package com.guillen.buildstock.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.guillen.buildstock.R
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

        val categoryName = intent.getStringExtra("CATEGORY_NAME") ?: getString(R.string.title_no_category)

        setupToolbar(categoryName)
        setupRecyclerView()
        loadTools(categoryName)
    }

    private fun setupToolbar(categoryName: String) {
        binding.toolbarCategory.title = categoryName
        binding.toolbarCategory.setNavigationIcon(R.drawable.baseline_arrow_back_ios_new_24)
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

    private fun loadTools(categoryName: String) {
        lifecycleScope.launch {
            val tools = repository.getToolsByCategory(categoryName)
            adapter.updateList(tools)
            binding.toolbarCategory.subtitle = getString(R.string.subtitle_elements_count, tools.size)
        }
    }
}