package com.guillen.buildstock.ui.admin

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.guillen.buildstock.data.model.Tool
import com.guillen.buildstock.data.repository.InventoryRepository
import com.guillen.buildstock.databinding.ActivityAddToolBinding
import kotlinx.coroutines.launch

class AddToolActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddToolBinding
    private val repository = InventoryRepository()
    private var toolId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddToolBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarAddTool)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarAddTool.setNavigationOnClickListener { finish() }

        toolId = intent.getStringExtra("TOOL_ID")
        setupSpinner()

        if (toolId != null) {
            binding.toolbarAddTool.title = "Editar Herramienta"
            binding.btnSaveTool.text = "ACTUALIZAR HERRAMIENTA"
            loadToolData(toolId!!)
        }

        binding.btnSaveTool.setOnClickListener {
            saveTool()
        }
    }

    private fun setupSpinner() {
        val categories = arrayOf("Herramientas Eléctricas", "Herramientas Manuales", "Consumibles", "Medición", "EPIS")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
    }

    private fun loadToolData(id: String) {
        lifecycleScope.launch {
            val tool = repository.getToolById(id)
            if (tool != null) {
                binding.etName.setText(tool.name)
                binding.etBrandModel.setText(tool.brandModel)
                binding.etStock.setText(tool.stock.toString())
                binding.etLocation.setText(tool.location)
                binding.etDescription.setText(tool.description)

                val categories = arrayOf("Herramientas Eléctricas", "Herramientas Manuales", "Consumibles", "Medición", "EPIS")
                val categoryIndex = categories.indexOf(tool.category)
                if (categoryIndex >= 0) {
                    binding.spinnerCategory.setSelection(categoryIndex)
                }
            }
        }
    }

    private fun saveTool() {
        val name = binding.etName.text.toString().trim()
        val brandModel = binding.etBrandModel.text.toString().trim()
        val category = binding.spinnerCategory.selectedItem.toString()
        val stockString = binding.etStock.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()

        val stock = stockString.toIntOrNull() ?: 0

        if (name.isEmpty()) {
            binding.etName.error = "El nombre es obligatorio"
            return
        }

        val tool = Tool(
            id = toolId ?: "", // CORREGIDO
            name = name,
            brandModel = brandModel,
            category = category,
            stock = stock,
            location = location,
            description = description,
            status = if (stock > 0) "disponible" else "en uso"
        )

        lifecycleScope.launch {
            binding.btnSaveTool.isEnabled = false
            val success = if (toolId == null) {
                repository.addTool(tool)
            } else {
                repository.updateTool(tool)
            }

            if (success) {
                Toast.makeText(this@AddToolActivity, "Operación realizada con éxito", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this@AddToolActivity, "Error al guardar en Firebase", Toast.LENGTH_SHORT).show()
                binding.btnSaveTool.isEnabled = true
            }
        }
    }
}