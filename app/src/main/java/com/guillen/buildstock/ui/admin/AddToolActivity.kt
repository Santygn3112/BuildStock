package com.guillen.buildstock.ui.admin

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.guillen.buildstock.R
import com.guillen.buildstock.data.model.Tool
import com.guillen.buildstock.data.repository.InventoryRepository
import com.guillen.buildstock.databinding.ActivityAddToolBinding
import kotlinx.coroutines.launch

// Actividad para añadir o editar herramientas en el inventario
class AddToolActivity : AppCompatActivity() {

    // Enlace con la vista XML
    private lateinit var binding: ActivityAddToolBinding
    // Repositorio para operaciones de base de datos
    private val repository = InventoryRepository()
    // Identificador de la herramienta si se está editando
    private var toolId: String? = null

    // Variables para mantener el estado actual de la herramienta
    private var currentStatus: String = "disponible"
    private var currentUserId: String = ""
    private var currentUserName: String = ""

    // Inicialización de la actividad y configuración de la interfaz
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddToolBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarAddTool)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarAddTool.setNavigationOnClickListener { finish() }

        toolId = intent.getStringExtra("TOOL_ID")
        setupSpinner()

        // Configura la interfaz para edición si existe un ID
        if (toolId != null) {
            binding.toolbarAddTool.setTitle(R.string.title_edit_tool)
            binding.btnSaveTool.setText(R.string.btn_update_data)
            loadToolData(toolId!!)
        }

        binding.btnSaveTool.setOnClickListener {
            saveTool()
        }
    }

    // Configura el selector de categorías
    private fun setupSpinner() {
        val categories = resources.getStringArray(R.array.tool_categories)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
    }

    // Carga los datos existentes de una herramienta para su edición
    private fun loadToolData(id: String) {
        lifecycleScope.launch {
            val tool = repository.getToolById(id)
            if (tool != null) {
                binding.etName.setText(tool.name)
                binding.etBrandModel.setText(tool.brandModel)
                binding.etLocation.setText(tool.location)
                binding.etDescription.setText(tool.description)

                currentStatus = tool.status
                currentUserId = tool.currentUserId
                currentUserName = tool.currentUserName

                val categories = resources.getStringArray(R.array.tool_categories)
                val categoryIndex = categories.indexOf(tool.category)
                if (categoryIndex >= 0) {
                    binding.spinnerCategory.setSelection(categoryIndex)
                }
            }
        }
    }

    // Guarda o actualiza la herramienta en la base de datos
    private fun saveTool() {
        val name = binding.etName.text.toString().trim()
        val brandModel = binding.etBrandModel.text.toString().trim()
        val category = binding.spinnerCategory.selectedItem.toString()
        val location = binding.etLocation.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()

        if (name.isEmpty()) {
            binding.etName.error = getString(R.string.error_name_required)
            return
        }

        val tool = Tool(
            id = toolId ?: "",
            name = name,
            brandModel = brandModel,
            category = category,
            location = location,
            description = description,
            status = currentStatus,
            currentUserId = currentUserId,
            currentUserName = currentUserName
        )

        lifecycleScope.launch {
            binding.btnSaveTool.isEnabled = false
            val success = if (toolId == null) {
                repository.addTool(tool)
            } else {
                repository.updateTool(tool)
            }

            if (success) {
                Toast.makeText(this@AddToolActivity, R.string.msg_tool_saved, Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this@AddToolActivity, R.string.error_firebase_save, Toast.LENGTH_SHORT).show()
                binding.btnSaveTool.isEnabled = true
            }
        }
    }
}
