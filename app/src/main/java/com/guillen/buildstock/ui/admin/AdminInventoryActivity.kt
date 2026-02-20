package com.guillen.buildstock.ui.admin

import android.content.Intent
import android.os.Bundle
import com.guillen.buildstock.R
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.guillen.buildstock.data.repository.InventoryRepository
import com.guillen.buildstock.databinding.ActivityAdminInventoryBinding
import kotlinx.coroutines.launch

// Pantalla principal de administración del inventario
class AdminInventoryActivity : AppCompatActivity() {

    // Enlace con la vista XML
    private lateinit var binding: ActivityAdminInventoryBinding
    // Repositorio de inventario
    private val repository = InventoryRepository()
    // Adaptador para la lista de herramientas
    private lateinit var adapter: AdminToolAdapter

    // Configuración inicial de la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()

        binding.btnAddNewToolTop.setOnClickListener {
            startActivity(Intent(this, AddToolActivity::class.java))
        }
    }

    // Recarga el inventario cada vez que la actividad se vuelve visible
    override fun onResume() {
        super.onResume()
        loadInventory()
    }

    // Configura la barra de herramientas superior
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarAdminInv)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarAdminInv.setNavigationOnClickListener { finish() }
    }

    // Inicializa el RecyclerView y su adaptador con los callbacks de acción
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
        binding.rvAdminTools.layoutManager = LinearLayoutManager(this)
        binding.rvAdminTools.adapter = adapter
    }

    // Obtiene la lista de herramientas desde el repositorio
    private fun loadInventory() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            val tools = repository.getToolsList()
            adapter.updateList(tools)
            binding.progressBar.visibility = View.GONE
        }
    }

    // Elimina una herramienta seleccionada
    private fun deleteTool(id: String) {
        if (id.isEmpty()) return
        lifecycleScope.launch {
            val success = repository.deleteTool(id)
            if (success) {
                Toast.makeText(this@AdminInventoryActivity, R.string.msg_tool_deleted, Toast.LENGTH_SHORT).show()
                loadInventory()
            } else {
                Toast.makeText(this@AdminInventoryActivity, R.string.msg_error_delete, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
