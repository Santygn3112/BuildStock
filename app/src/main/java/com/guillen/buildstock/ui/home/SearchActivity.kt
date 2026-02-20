package com.guillen.buildstock.ui.home

import android.content.Intent
import android.os.Bundle
import com.guillen.buildstock.R
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.guillen.buildstock.data.model.Tool
import com.guillen.buildstock.data.repository.InventoryRepository
import com.guillen.buildstock.databinding.ActivitySearchBinding
import com.guillen.buildstock.ui.cart.CartManager
import com.guillen.buildstock.ui.inventory.ToolAdapter
import com.guillen.buildstock.ui.inventory.ToolDetailActivity
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val repository = InventoryRepository()
    private lateinit var adapter: ToolAdapter

    private var allTools: List<Tool> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarSearch.setNavigationIcon(android.R.drawable.ic_media_previous)
        binding.toolbarSearch.setNavigationOnClickListener { finish() }

        setupRecyclerView()
        setupSearchListener()

        binding.etSearchInput.requestFocus()

        loadAllTools()
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
                    Toast.makeText(this, R.string.msg_tool_not_available_short, Toast.LENGTH_SHORT).show()
                }
            }
        )
        binding.rvSearchResults.layoutManager = LinearLayoutManager(this)
        binding.rvSearchResults.adapter = adapter
    }

    private fun setupSearchListener() {
        binding.etSearchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filterTools(s.toString())
            }
        })
    }

    private fun loadAllTools() {
        lifecycleScope.launch {
            allTools = repository.getToolsList()
            adapter.updateList(allTools)
        }
    }

    private fun filterTools(query: String) {
        if (query.isEmpty()) {
            adapter.updateList(allTools)
            return
        }

        val lowerCaseQuery = query.lowercase()
        val filteredList = allTools.filter { tool ->
            tool.name.lowercase().contains(lowerCaseQuery) ||
                    tool.brandModel.lowercase().contains(lowerCaseQuery) ||
                    tool.category.lowercase().contains(lowerCaseQuery)
        }
        adapter.updateList(filteredList)
    }
}