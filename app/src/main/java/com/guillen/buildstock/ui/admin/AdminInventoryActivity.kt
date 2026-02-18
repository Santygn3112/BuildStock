package com.guillen.buildstock.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.guillen.buildstock.data.repository.InventoryRepository
import com.guillen.buildstock.databinding.ActivityAdminInventoryBinding
import kotlinx.coroutines.launch

class AdminInventoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminInventoryBinding
    private val repository = InventoryRepository()
    private lateinit var adapter: AdminToolAdapter // Nuevo adaptador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarAdminInv.setNavigationIcon(android.R.drawable.ic_media_previous)
        binding.toolbarAdminInv.setNavigationOnClickListener { finish() }

        // El nuevo botón grande superior
        binding.btnAddNewToolTop.setOnClickListener {
            startActivity(Intent(this, AddToolActivity::class.java))
        }

        setupRecyclerView()
    }

    // En AdminInventoryActivity.kt, actualiza esta función:
    private fun setupRecyclerView() {
        adapter = AdminToolAdapter(
            tools = emptyList(),
            onEditClick = { tool ->
                // Al pulsar EDITAR, abrimos la actividad de añadir pero con datos
                val intent = Intent(this, AddToolActivity::class.java).apply {
                    putExtra("TOOL_ID", tool.id)
                    putExtra("TOOL_NAME", tool.name)
                    putExtra("TOOL_BRAND", tool.brandModel)
                    putExtra("TOOL_CATEGORY", tool.category)
                    putExtra("TOOL_STOCK", tool.stock)
                    putExtra("TOOL_LOCATION", tool.location)
                    putExtra("TOOL_DESC", tool.description)
                }
                startActivity(intent)
            },
            onDeleteClick = { tool ->
                confirmDelete(tool.id, tool.name)
            }
        )
        binding.rvAdminTools.adapter = adapter
    }

    private fun confirmDelete(toolId: String, toolName: String) {
        // Crítica técnica: Nunca borres directamente. Pide confirmación siempre.
        AlertDialog.Builder(this)
            .setTitle("Eliminar Herramienta")
            .setMessage("¿Estás seguro de que quieres borrar '$toolName'? Esta acción no se puede deshacer.")
            .setPositiveButton("Borrar") { _, _ ->
                lifecycleScope.launch {
                    val success = repository.deleteTool(toolId)
                    if (success) {
                        Toast.makeText(this@AdminInventoryActivity, "Herramienta eliminada", Toast.LENGTH_SHORT).show()
                        loadTools() // Recargar la lista
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun loadTools() {
        lifecycleScope.launch {
            val tools = repository.getToolsList()
            adapter.updateList(tools)
        }
    }

    override fun onResume() {
        super.onResume()
        loadTools()
    }
}