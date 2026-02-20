package com.guillen.buildstock.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.guillen.buildstock.data.repository.InventoryRepository
import com.guillen.buildstock.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val repository = InventoryRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        loadStatistics()
    }

    private fun loadStatistics() {
        lifecycleScope.launch {
            // Pedimos los conteos exactos a Firebase
            val availableCount = repository.getToolsCountByStatus("disponible")
            val inUseCount = repository.getToolsCountByStatus("en uso")

            // Actualizamos la interfaz gráfica
            binding.tvAvailableCount.text = availableCount.toString()
            binding.tvInUseCount.text = inUseCount.toString()
        }
    }

    private fun setupClickListeners() {
        binding.cardElectric.setOnClickListener {
            openCategory("Herramientas Eléctricas")
        }

        binding.cardManual.setOnClickListener {
            openCategory("Herramientas Manuales")
        }

        binding.cardConsumables.setOnClickListener {
            openCategory("Consumibles")
        }

        binding.cvSearch.setOnClickListener {
            // Abrimos el nuevo buscador
            val intent = Intent(requireContext(), SearchActivity::class.java)
            startActivity(intent)
        }
    }

    private fun openCategory(categoryName: String) {
        val intent = Intent(requireContext(), CategoryDetailActivity::class.java).apply {
            putExtra("CATEGORY_NAME", categoryName)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Refrescar los números si volvemos a esta pantalla
    override fun onResume() {
        super.onResume()
        loadStatistics()
    }
}