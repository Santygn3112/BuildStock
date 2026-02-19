package com.guillen.buildstock.ui.detail

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.guillen.buildstock.R
import com.guillen.buildstock.data.repository.InventoryRepository
import com.guillen.buildstock.databinding.ActivityToolDetailBinding
import com.guillen.buildstock.ui.cart.CartManager
import kotlinx.coroutines.launch

class ToolDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityToolDetailBinding
    private val repository = InventoryRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityToolDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolId = intent.getStringExtra("TOOL_ID")

        setSupportActionBar(binding.toolbarToolDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarToolDetail.setNavigationOnClickListener { finish() }

        if (toolId != null) {
            loadToolDetails(toolId)
        } else {
            finish()
        }
    }

    private fun loadToolDetails(id: String) {
        lifecycleScope.launch {
            val tool = repository.getToolById(id)

            // Le damos el nombre explícito 'selectedTool' en lugar de usar 'it'
            tool?.let { selectedTool ->
                binding.tvDetailName.text = selectedTool.name
                binding.tvDetailBrandModel.text = "Marca/Modelo: ${selectedTool.brandModel}"
                binding.tvDetailCategory.text = selectedTool.category
                binding.tvDetailLocation.text = selectedTool.location
                binding.tvDetailDescription.text = selectedTool.description

                // CARGA DE IMAGEN EN DETALLE
                if (!selectedTool.imageUrl.isNullOrEmpty()) {
                    binding.ivDetailImage.setPadding(0, 0, 0, 0)
                    com.bumptech.glide.Glide.with(this@ToolDetailActivity)
                        .load(selectedTool.imageUrl)
                        .centerCrop()
                        .into(binding.ivDetailImage)
                }

                val isDisponible = selectedTool.status.lowercase() == "disponible"
                if (isDisponible) {
                    binding.tvDetailStatus.text = "Disponible"
                    binding.tvDetailStatus.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(this@ToolDetailActivity, R.color.brand_green)
                    )
                    binding.btnAddToCart.visibility = View.VISIBLE

                    // Ahora pasamos 'selectedTool' explícitamente al carrito
                    binding.btnAddToCart.setOnClickListener {
                        CartManager.addTool(selectedTool)
                        Toast.makeText(this@ToolDetailActivity, "Añadido", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    binding.tvDetailStatus.text = "En uso por ${selectedTool.currentUserName}"
                    binding.tvDetailStatus.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(this@ToolDetailActivity, R.color.brand_orange)
                    )
                    binding.btnAddToCart.visibility = View.GONE
                }
            }
        }
    }
}