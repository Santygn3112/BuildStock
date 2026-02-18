package com.guillen.buildstock.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.guillen.buildstock.data.repository.InventoryRepository
import com.guillen.buildstock.databinding.ActivityAdminInventoryBinding
import kotlinx.coroutines.launch

class AdminInventoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminInventoryBinding
    private val repository = InventoryRepository()
    private lateinit var adapter: AdminToolAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()

        // ID corregido: btnAddNewToolTop
        binding.btnAddNewToolTop.setOnClickListener {
            startActivity(Intent(this, AddToolActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadInventory()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarAdminInv) // ID corregido: toolbarAdminInv
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarAdminInv.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = AdminToolAdapter(
            tools = emptyList(),
            onEditClick = { tool ->
                val intent = Intent(this, AddToolActivity::class.java).apply {
                    putExtra("TOOL_ID", tool.id)
                }
                startActivity(intent)
            },
            onDeleteClick = { tool ->
                deleteTool(tool.id)
            }
        )
        // ID corregido: rvAdminTools
        binding.rvAdminTools.layoutManager = LinearLayoutManager(this)
        binding.rvAdminTools.adapter = adapter
    }

    private fun loadInventory() {
        binding.progressBar.visibility = View.VISIBLE // Ahora el ID existe en el XML
        lifecycleScope.launch {
            val tools = repository.getToolsList()
            adapter.updateList(tools)
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun deleteTool(id: String) {
        lifecycleScope.launch {
            val success = repository.deleteTool(id)
            if (success) {
                Toast.makeText(this@AdminInventoryActivity, "Herramienta eliminada", Toast.LENGTH_SHORT).show()
                loadInventory()
            } else {
                Toast.makeText(this@AdminInventoryActivity, "Error al eliminar", Toast.LENGTH_SHORT).show()
            }
        }
    }
}