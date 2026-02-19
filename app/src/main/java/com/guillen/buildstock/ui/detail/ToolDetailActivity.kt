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
import com.google.firebase.firestore.FirebaseFirestore
import com.guillen.buildstock.R
import com.guillen.buildstock.data.model.User
import com.guillen.buildstock.data.repository.InventoryRepository
import com.guillen.buildstock.databinding.ActivityToolDetailBinding
import com.guillen.buildstock.ui.cart.CartManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
            tool?.let {
                binding.tvDetailName.text = it.name
                binding.tvDetailBrandModel.text = if (it.brandModel.isNotEmpty()) "Marca/Modelo: ${it.brandModel}" else ""

                binding.tvDetailCategory.text = it.category.ifEmpty { "Sin especificar" }
                binding.tvDetailLocation.text = it.location.ifEmpty { "Sin ubicación" }
                binding.tvDetailDescription.text = it.description.ifEmpty { "No hay descripción disponible." }

                val isDisponible = it.status.lowercase() == "disponible"

                if (isDisponible) {
                    binding.tvDetailStatus.text = "Disponible"
                    binding.tvDetailStatus.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(this@ToolDetailActivity, R.color.brand_green)
                    )

                    // Lógica para añadir al carrito
                    binding.btnAddToCart.visibility = View.VISIBLE
                    binding.btnCallUser.visibility = View.GONE

                    binding.btnAddToCart.setOnClickListener { _ ->
                        CartManager.addTool(it)
                        Toast.makeText(this@ToolDetailActivity, "Añadida al carrito: ${it.name}", Toast.LENGTH_SHORT).show()
                        finish() // Vuelve a la pantalla anterior automáticamente
                    }
                } else {
                    val estadoText = if (it.currentUserName.isNotEmpty()) {
                        "En uso por ${it.currentUserName}"
                    } else {
                        it.status.replaceFirstChar { char -> char.uppercase() }
                    }
                    binding.tvDetailStatus.text = estadoText
                    binding.tvDetailStatus.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(this@ToolDetailActivity, R.color.brand_orange)
                    )

                    // Lógica para el botón de llamada
                    if (it.currentUserId.isNotEmpty()) {
                        binding.btnAddToCart.visibility = View.GONE
                        binding.btnCallUser.visibility = View.VISIBLE

                        binding.btnCallUser.setOnClickListener { _ ->
                            callUser(it.currentUserId)
                        }
                    } else {
                        binding.btnAddToCart.visibility = View.GONE
                        binding.btnCallUser.visibility = View.GONE
                    }
                }
            }
        }
    }

    // Función que conecta con Firestore para buscar el teléfono del operario
    private fun callUser(userId: String) {
        lifecycleScope.launch {
            try {
                val db = FirebaseFirestore.getInstance()
                val doc = db.collection("users").document(userId).get().await()
                val user = doc.toObject(User::class.java)

                val phone = user?.phone ?: ""

                if (phone.isNotEmpty()) {
                    // ACTION_DIAL abre la app de teléfono sin hacer la llamada directa (más seguro, no requiere permisos)
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:$phone")
                    startActivity(intent)
                } else {
                    Toast.makeText(this@ToolDetailActivity, "Este operario no tiene teléfono registrado.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ToolDetailActivity, "Error al conectar con la base de datos.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}