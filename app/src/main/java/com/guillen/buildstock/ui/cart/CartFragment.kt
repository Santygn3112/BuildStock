package com.guillen.buildstock.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.guillen.buildstock.R
import com.guillen.buildstock.data.repository.InventoryRepository
import com.guillen.buildstock.data.repository.AuthRepository
import com.guillen.buildstock.databinding.FragmentCartBinding
import kotlinx.coroutines.launch

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val inventoryRepository = InventoryRepository()
    private val authRepository = AuthRepository() // Para sacar los datos del usuario actual
    private lateinit var cartAdapter: CartAdapter

    private var isRecogida = true // Estado del toggle

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupToggleButtons()

        binding.btnConfirmTransaction.setOnClickListener {
            confirmTransaction()
        }
    }

    private fun confirmTransaction() {
        val items = CartManager.cartItems.value ?: emptyList()

        if (items.isEmpty()) {
            Toast.makeText(requireContext(), "El carrito está vacío", Toast.LENGTH_SHORT).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            binding.btnConfirmTransaction.isEnabled = false

            // 1. Obtenemos quién está operando (para la opción 1 de "Desnormalización")
            val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
            val userProfile = authRepository.getAllUsers().find { it.email == currentUserEmail }

            val userName = userProfile?.name ?: "Usuario Desconocido"
            val userPhone = userProfile?.phone ?: "Sin teléfono"

            // 2. Ejecutamos el lote en Firebase
            val success = inventoryRepository.processTransaction(
                items = items,
                isRecogida = isRecogida,
                userName = userName,
                userPhone = userPhone
            )

            if (success) {
                Toast.makeText(requireContext(), "¡Transacción realizada con éxito!", Toast.LENGTH_LONG).show()
                CartManager.clearCart() // Vaciamos el Singleton
            } else {
                Toast.makeText(requireContext(), "Error al procesar. Revisa tu conexión.", Toast.LENGTH_LONG).show()
            }

            binding.btnConfirmTransaction.isEnabled = true
        }
    }

    private fun setupRecyclerView() {
        binding.rvCartItems.layoutManager = LinearLayoutManager(requireContext())

        CartManager.cartItems.observe(viewLifecycleOwner) { items ->
            cartAdapter = CartAdapter(
                cartList = items,
                onQuantityChanged = { tool, newQty -> CartManager.updateQuantity(tool, newQty) },
                onItemRemoved = { tool -> CartManager.removeFromCart(tool) }
            )
            binding.rvCartItems.adapter = cartAdapter
            updateTotals()
        }
    }

    private fun setupToggleButtons() {
        binding.toggleTransactionType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                isRecogida = checkedId == R.id.btnRecogida
                if (isRecogida) {
                    binding.tvTransactionTypeLabel.text = "RECOGIDA"
                    binding.tvTransactionTypeLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.brand_orange))
                } else {
                    binding.tvTransactionTypeLabel.text = "DEVOLUCIÓN"
                    binding.tvTransactionTypeLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.brand_green))
                }
            }
        }
    }

    private fun updateTotals() {
        val total = CartManager.cartItems.value?.sumOf { it.quantity } ?: 0
        binding.tvTotalItems.text = total.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}