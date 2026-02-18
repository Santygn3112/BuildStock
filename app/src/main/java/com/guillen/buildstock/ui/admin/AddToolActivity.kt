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

class AddToolActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddToolBinding
    private val repository = InventoryRepository()

    // LA LÍNEA DEL CONFLICTO: Perfectamente escrita e inicializada a null
    private var toolIdToEdit: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddToolBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupCategorySpinner()
        checkForEditMode()
        setupSaveButton()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarAddTool)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarAddTool.setNavigationOnClickListener { finish() }
    }

    private fun setupCategorySpinner() {
        ArrayAdapter.createFromResource(
            this,
            R.array.tool_categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = adapter
        }
    }

    private fun checkForEditMode() {
        toolIdToEdit = intent.getStringExtra("TOOL_ID")
        if (toolIdToEdit != null) {
            supportActionBar?.title = "Editar Herramienta"
            binding.btnSaveTool.text = "Actualizar Herramienta"

            binding.etName.setText(intent.getStringExtra("TOOL_NAME"))
            binding.etBrandModel.setText(intent.getStringExtra("TOOL_BRAND"))
            binding.etLocation.setText(intent.getStringExtra("TOOL_LOCATION"))
            binding.etStock.setText(intent.getIntExtra("TOOL_STOCK", 0).toString())
            binding.etDescription.setText(intent.getStringExtra("TOOL_DESC"))

            val category = intent.getStringExtra("TOOL_CATEGORY")
            val adapter = binding.spinnerCategory.adapter as ArrayAdapter<String>
            val position = adapter.getPosition(category)
            if (position >= 0) {
                binding.spinnerCategory.setSelection(position)
            }
        }
    }

    private fun setupSaveButton() {
        binding.btnSaveTool.setOnClickListener {
            if (binding.etName.text.isNullOrBlank() || binding.etStock.text.isNullOrBlank()) {
                Toast.makeText(this, "Nombre y Stock son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tool = Tool(
                id = toolIdToEdit ?: "",
                name = binding.etName.text.toString(),
                brandModel = binding.etBrandModel.text.toString(),
                category = binding.spinnerCategory.selectedItem.toString(),
                status = "disponible",
                stock = binding.etStock.text.toString().toIntOrNull() ?: 0,
                location = binding.etLocation.text.toString(),
                description = binding.etDescription.text.toString()
            )

            lifecycleScope.launch {
                val success = if (toolIdToEdit == null) {
                    repository.addTool(tool)
                } else {
                    repository.updateTool(tool)
                }

                if (success) {
                    Toast.makeText(this@AddToolActivity, "Guardado correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@AddToolActivity, "Error al guardar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}