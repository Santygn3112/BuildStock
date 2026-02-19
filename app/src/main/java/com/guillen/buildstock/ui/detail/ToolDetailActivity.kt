package com.guillen.buildstock.ui.detail

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.guillen.buildstock.R
import com.guillen.buildstock.data.model.Tool
import com.guillen.buildstock.data.model.User
import com.guillen.buildstock.data.repository.InventoryRepository
import com.guillen.buildstock.databinding.ActivityToolDetailBinding
import com.guillen.buildstock.ui.cart.CartManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await // ESTE ES EL IMPORT QUE TE FALTA

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

            tool?.let { selectedTool ->
                binding.tvDetailName.text = selectedTool.name
                binding.tvDetailBrandModel.text = "Marca/Modelo: ${selectedTool.brandModel}"
                binding.tvDetailCategory.text = selectedTool.category
                binding.tvDetailLocation.text = selectedTool.location
                binding.tvDetailDescription.text = selectedTool.description

                // Carga de imagen con Glide
                if (!selectedTool.imageUrl.isNullOrEmpty()) {
                    binding.ivDetailImage.setPadding(0, 0, 0, 0)
                    Glide.with(this@ToolDetailActivity)
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
                    binding.btnCallUser.visibility = View.GONE

                    binding.btnAddToCart.setOnClickListener {
                        CartManager.addTool(selectedTool)
                        Toast.makeText(this@ToolDetailActivity, "Añadida al carrito", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    // Estado: EN USO
                    binding.tvDetailStatus.text = "En uso por ${selectedTool.currentUserName}"
                    binding.tvDetailStatus.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(this@ToolDetailActivity, R.color.brand_orange)
                    )
                    binding.btnAddToCart.visibility = View.GONE

                    // Mostrar botón de llamada si hay un ID de usuario asignado
                    if (selectedTool.currentUserId.isNotEmpty()) {
                        binding.btnCallUser.visibility = View.VISIBLE
                        binding.btnCallUser.setOnClickListener {
                            callUser(selectedTool.currentUserId)
                        }
                    } else {
                        binding.btnCallUser.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun callUser(userId: String) {
        lifecycleScope.launch {
            try {
                val db = FirebaseFirestore.getInstance()
                val doc = db.collection("users").document(userId).get().await()
                val user = doc.toObject(User::class.java)
                val phone = user?.phone ?: ""

                if (phone.isNotEmpty()) {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:$phone")
                    startActivity(intent)
                } else {
                    Toast.makeText(this@ToolDetailActivity, "Este operario no tiene teléfono registrado.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ToolDetailActivity, "Error al obtener datos del operario.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}