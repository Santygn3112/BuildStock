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

// Fragmento principal de la aplicación con resumen y accesos directos
class HomeFragment : Fragment() {

    // Enlace con la vista XML
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Repositorio de inventario
    private val repository = InventoryRepository()

    // Inflado de la vista del fragmento
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Configuración inicial de la vista una vez creada
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        loadStatistics()
    }

    // Carga y muestra estadísticas del inventario
    private fun loadStatistics() {
        lifecycleScope.launch {
            // Solicita los conteos a la base de datos
            val availableCount = repository.getToolsCountByStatus("disponible")
            val inUseCount = repository.getToolsCountByStatus("en uso")

            // Actualiza los contadores en la interfaz
            binding.tvAvailableCount.text = availableCount.toString()
            binding.tvInUseCount.text = inUseCount.toString()
        }
    }

    // Configura los listeners para las tarjetas de categorías y búsqueda
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
            // Navega a la pantalla de búsqueda
            val intent = Intent(requireContext(), SearchActivity::class.java)
            startActivity(intent)
        }
    }

    // Abre la actividad de detalle de categoría
    private fun openCategory(categoryName: String) {
        val intent = Intent(requireContext(), CategoryDetailActivity::class.java).apply {
            putExtra("CATEGORY_NAME", categoryName)
        }
        startActivity(intent)
    }

    // Limpieza del binding al destruir la vista
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Actualiza las estadísticas al volver al fragmento
    override fun onResume() {
        super.onResume()
        loadStatistics()
    }
}
