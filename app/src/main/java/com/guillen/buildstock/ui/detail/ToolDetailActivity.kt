package com.guillen.buildstock.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.guillen.buildstock.databinding.ActivityToolDetailBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.guillen.buildstock.data.model.Tool
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ToolDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityToolDetailBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityToolDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recuperamos el ID que enviamos desde el MainActivity
        val toolId = intent.getStringExtra("TOOL_ID") ?: return

        loadToolDetails(toolId)
    }

    private fun loadToolDetails(id: String) {
        lifecycleScope.launch {
            val doc = db.collection("tools").document(id).get().await()
            val tool = doc.toObject(Tool::class.java)

            tool?.let {
                binding.tvDetailName.text = it.name
                binding.tvDetailDescription.text = it.description
                binding.tvDetailStatus.text = it.status
                // Aquí iría la carga de imagen con Glide
            }
        }
    }
}