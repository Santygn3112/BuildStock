package com.guillen.buildstock.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.guillen.buildstock.R
import com.guillen.buildstock.data.model.Tool
import com.guillen.buildstock.databinding.FragmentCartBinding
import androidx.fragment.app.activityViewModels

class CartFragment : Fragment() {
    // El "cerebro" compartido correcto
    private val sharedCartViewModel: SharedCartViewModel by activityViewModels()
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            setupRecyclerView()
            setupToggleButtons()

            binding.btnConfirmTransaction.setOnClickListener {
                val totalItems = cartAdapter.getCartItems().sumOf { it.quantity }
                if (totalItems > 0) {
                    Toast.makeText(requireContext(), "Confirmando $totalItems herramientas...", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "El carrito está vacío", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            // Si algo explota al cargar, lo capturamos aquí para que no se cierre la app
            android.util.Log.e("CART_ERROR", "Error al cargar la vista", e)
            Toast.makeText(requireContext(), "Error visual: Revisa tus colores en colors.xml", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupRecyclerView() {
        // Inicializamos el adaptador con una lista vacía por ahora
        cartAdapter = CartAdapter(mutableListOf()) {
            // Este bloque se ejecuta cuando sumamos, restamos o borramos en la tarjeta
            updateTotals()
            // Podríamos avisar al ViewModel de los cambios aquí más adelante
        }

        binding.rvCartItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }

        // ¡AQUÍ ESTÁ LA MAGIA! Escuchamos al cerebro en tiempo real
        sharedCartViewModel.cartItems.observe(viewLifecycleOwner) { items ->
            // Si el ViewModel cambia, actualizamos el adaptador
            cartAdapter = CartAdapter(items) {
                updateTotals()
                // Falta enlazar los botones +- con el ViewModel, lo haremos enseguida
            }
            binding.rvCartItems.adapter = cartAdapter
            updateTotals()
        }
    }

    private fun setupToggleButtons() {
        binding.toggleTransactionType.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnRecogida -> {
                        binding.tvTransactionTypeLabel.text = "RECOGIDA"
                        try {
                            // Uso seguro de colores
                            binding.tvTransactionTypeLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.brand_orange))
                        } catch (e: Exception) { }
                    }
                    R.id.btnDevolucion -> {
                        binding.tvTransactionTypeLabel.text = "DEVOLUCIÓN"
                        try {
                            // Uso seguro de colores
                            binding.tvTransactionTypeLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.brand_green))
                        } catch (e: Exception) { }
                    }
                }
            }
        }
    }

    private fun updateTotals() {
        val totalElements = cartAdapter.getCartItems().sumOf { it.quantity }
        binding.tvTotalItems.text = totalElements.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}