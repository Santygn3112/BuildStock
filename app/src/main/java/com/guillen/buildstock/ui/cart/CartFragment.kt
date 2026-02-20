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
    private var toolsToReturn: MutableList<Tool> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupToggleButtons()
        loadPickupItems()
    }

    private fun setupToggleButtons() {
        binding.toggleTransactionType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                isRecogida = checkedId == R.id.btnRecogida
                if (isRecogida) {
                    binding.tvTransactionTypeLabel.setText(R.string.label_pickup)
                    binding.tvTransactionTypeLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.brand_orange))
                    loadPickupItems()
                } else {
                    binding.tvTransactionTypeLabel.setText(R.string.label_return)
                    binding.tvTransactionTypeLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.brand_green))
                    loadReturnItems()
                }
            }
        }
    }

    private fun loadPickupItems() {
        CartManager.cartItems.observe(viewLifecycleOwner) { items ->
            if (isRecogida) {
                cartAdapter.updateList(items)
                updateTotals(items.size)
            }
        }
    }

    private fun loadReturnItems() {
        viewLifecycleOwner.lifecycleScope.launch {
            val currentUser = authRepository.getUserProfile()
            if (currentUser != null && currentUser.id.isNotEmpty()) {
                toolsToReturn = inventoryRepository.getToolsByUserId(currentUser.id).toMutableList()
                cartAdapter.updateList(toolsToReturn)
                updateTotals(toolsToReturn.size)
            } else {
                val errorMsg = if (currentUser?.id?.isEmpty() == true)
                    getString(R.string.error_user_id_empty)
                else getString(R.string.msg_login_again)
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
                cartAdapter.updateList(emptyList())
            }
        }
    }

    private fun confirmTransaction() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.btnConfirmTransaction.isEnabled = false
            val currentUser = authRepository.getUserProfile()

            if (currentUser == null || currentUser.id.isEmpty()) {
                Toast.makeText(requireContext(), R.string.error_verify_identity, Toast.LENGTH_LONG).show()
                binding.btnConfirmTransaction.isEnabled = true
                return@launch
            }

            val success = if (isRecogida) {
                val items = CartManager.cartItems.value ?: emptyList()
                if (items.isEmpty()) {
                    Toast.makeText(requireContext(), R.string.msg_cart_empty, Toast.LENGTH_SHORT).show()
                    binding.btnConfirmTransaction.isEnabled = true
                    return@launch
                }
                inventoryRepository.processPickupTransaction(items, currentUser.id, currentUser.name)
            } else {
                val items = cartAdapter.getCartList()
                if (items.isEmpty()) {
                    Toast.makeText(requireContext(), R.string.msg_no_tools_return, Toast.LENGTH_SHORT).show()
                    binding.btnConfirmTransaction.isEnabled = true
                    return@launch
                }
                inventoryRepository.processReturnTransaction(items, currentUser.id, currentUser.name)
            }

            if (success) {
                Toast.makeText(requireContext(), R.string.msg_transaction_success, Toast.LENGTH_LONG).show()
                if (isRecogida) {
                    CartManager.clearCart()
                } else {
                    loadReturnItems()
                }
            } else {
                Toast.makeText(requireContext(), R.string.error_process_transaction, Toast.LENGTH_LONG).show()
            }
            binding.btnConfirmTransaction.isEnabled = true
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            tools = emptyList(),
            onItemRemoved = { tool ->
                if (isRecogida) {
                    CartManager.removeTool(tool)
                } else {
                    toolsToReturn.remove(tool)
                    cartAdapter.updateList(toolsToReturn)
                    updateTotals(toolsToReturn.size)
                }
            }
        )
        binding.rvCartItems.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCartItems.adapter = cartAdapter

        binding.btnConfirmTransaction.setOnClickListener { confirmTransaction() }
    }

    private fun updateTotals(count: Int) {
        binding.tvTotalItems.text = count.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}