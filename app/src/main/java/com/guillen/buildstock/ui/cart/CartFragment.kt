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
import com.guillen.buildstock.R
import com.guillen.buildstock.data.model.Loan
import com.guillen.buildstock.data.model.Tool
import com.guillen.buildstock.data.repository.AuthRepository
import com.guillen.buildstock.data.repository.InventoryRepository
import com.guillen.buildstock.databinding.FragmentCartBinding
import kotlinx.coroutines.launch

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val inventoryRepository = InventoryRepository()
    private val authRepository = AuthRepository()
    private lateinit var cartAdapter: CartAdapter

    private var isRecogida = true
    private var currentUserLoans: List<Loan> = emptyList() // Guardar préstamos originales

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupToggleButtons()
        loadCartManagerItems() // Carga inicial
    }

    private fun setupToggleButtons() {
        binding.toggleTransactionType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                isRecogida = checkedId == R.id.btnRecogida
                if (isRecogida) {
                    binding.tvTransactionTypeLabel.text = "RECOGIDA"
                    binding.tvTransactionTypeLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.brand_orange))
                    loadCartManagerItems()
                } else {
                    binding.tvTransactionTypeLabel.text = "DEVOLUCIÓN"
                    binding.tvTransactionTypeLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.brand_green))
                    loadUserLoans()
                }
            }
        }
    }

    private fun loadCartManagerItems() {
        CartManager.cartItems.observe(viewLifecycleOwner) { items ->
            cartAdapter.updateList(items)
            updateTotals()
        }
    }

    private fun loadUserLoans() {
        viewLifecycleOwner.lifecycleScope.launch {
            val currentUser = authRepository.getUserProfile() // CORREGIDO
            if (currentUser != null) {
                currentUserLoans = inventoryRepository.getActiveLoansByUser(currentUser.name)
                // Mapeamos Loan a CartItem para que el adapter funcione sin cambios
                val itemsForAdapter = currentUserLoans.map { loan ->
                    CartItem(
                        tool = Tool(id = loan.toolId, name = loan.toolName), // Tool simplificado para UI
                        quantity = loan.quantity
                    )
                }
                cartAdapter.updateList(itemsForAdapter)
                updateTotals()
            } else {
                Toast.makeText(requireContext(), "Error: Usuario no identificado.", Toast.LENGTH_SHORT).show()
                cartAdapter.updateList(emptyList())
                updateTotals()
            }
        }
    }

    private fun confirmTransaction() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.btnConfirmTransaction.isEnabled = false
            val currentUser = authRepository.getUserProfile() // CORREGIDO

            if (currentUser == null) {
                Toast.makeText(requireContext(), "Error: No se pudo verificar el usuario.", Toast.LENGTH_LONG).show()
                binding.btnConfirmTransaction.isEnabled = true
                return@launch
            }

            val success = if (isRecogida) {
                val items = CartManager.cartItems.value ?: emptyList()
                if (items.isEmpty()) {
                    Toast.makeText(requireContext(), "El carrito está vacío.", Toast.LENGTH_SHORT).show()
                    binding.btnConfirmTransaction.isEnabled = true
                    return@launch
                }
                inventoryRepository.processPickupTransaction(items, currentUser.id, currentUser.name) // CORREGIDO
            } else {
                if (currentUserLoans.isEmpty()) {
                    Toast.makeText(requireContext(), "No hay préstamos para devolver.", Toast.LENGTH_SHORT).show()
                    binding.btnConfirmTransaction.isEnabled = true
                    return@launch
                }
                inventoryRepository.processReturnTransaction(currentUserLoans)
            }

            if (success) {
                Toast.makeText(requireContext(), "¡Transacción exitosa!", Toast.LENGTH_LONG).show()
                if (isRecogida) {
                    CartManager.clearCart()
                } else {
                    loadUserLoans() // Recargar la lista de préstamos
                }
            } else {
                Toast.makeText(requireContext(), "Error al procesar la transacción.", Toast.LENGTH_LONG).show()
            }
            binding.btnConfirmTransaction.isEnabled = true
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(mutableListOf(), 
            onQuantityChanged = {_,_ -> /* No-op en devolución */}, 
            onItemRemoved = {_ -> /* No-op en devolución */}
        )
        binding.rvCartItems.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCartItems.adapter = cartAdapter

        binding.btnConfirmTransaction.setOnClickListener { confirmTransaction() }
    }

    private fun updateTotals() {
        val total = cartAdapter.getCartList().sumOf { it.quantity }
        binding.tvTotalItems.text = total.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}