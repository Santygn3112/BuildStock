package com.guillen.buildstock.ui.inventory

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

            tool?.let { selectedTool ->
                binding.tvDetailName.text = selectedTool.name
                binding.tvDetailBrandModel.text = getString(R.string.detail_brand_model_format, selectedTool.brandModel)
                binding.tvDetailCategory.text = selectedTool.category
                binding.tvDetailLocation.text = selectedTool.location
                binding.tvDetailDescription.text = selectedTool.description

                if (!selectedTool.imageUrl.isNullOrEmpty()) {
                    binding.ivDetailImage.setPadding(0, 0, 0, 0)
                    binding.ivDetailImage.imageTintList = null

                    Glide.with(this@ToolDetailActivity)
                        .load(selectedTool.imageUrl)
                        .centerCrop()
                        .into(binding.ivDetailImage)
                } else {
                    binding.ivDetailImage.setImageResource(android.R.drawable.ic_menu_gallery)
                    val tintColor = ContextCompat.getColor(this@ToolDetailActivity, R.color.brand_navy)
                    binding.ivDetailImage.imageTintList = ColorStateList.valueOf(tintColor)
                }

                val isDisponible = selectedTool.status.lowercase() == "disponible"
                if (isDisponible) {
                    binding.tvDetailStatus.setText(R.string.status_available)
                    binding.tvDetailStatus.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(this@ToolDetailActivity, R.color.brand_green)
                    )
                    binding.btnAddToCart.visibility = View.VISIBLE
                    binding.btnCallUser.visibility = View.GONE

                    binding.btnAddToCart.setOnClickListener {
                        CartManager.addTool(selectedTool)
                        Toast.makeText(this@ToolDetailActivity, R.string.msg_added_to_cart_simple, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    binding.tvDetailStatus.text = getString(R.string.status_in_use_by_format, selectedTool.currentUserName)
                    binding.tvDetailStatus.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(this@ToolDetailActivity, R.color.brand_orange)
                    )
                    binding.btnAddToCart.visibility = View.GONE

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
                    Toast.makeText(this@ToolDetailActivity, R.string.msg_no_phone, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ToolDetailActivity, R.string.error_connection, Toast.LENGTH_SHORT).show()
            }
        }
    }
}