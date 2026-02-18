package com.guillen.buildstock.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.guillen.buildstock.data.model.Tool
import com.guillen.buildstock.databinding.ItemCartToolBinding

data class CartItem(
    val tool: Tool,
    var quantity: Int = 1
)

class CartAdapter(
    private var cartList: MutableList<CartItem>,
    private val onQuantityChanged: (Tool, Int) -> Unit,
    private val onItemRemoved: (Tool) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: ItemCartToolBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CartItem) {
            binding.tvToolNameCart.text = item.tool.name
            binding.tvQuantity.text = item.quantity.toString()

            binding.btnPlus.setOnClickListener {
                if (item.quantity < item.tool.stock) {
                    // Avisamos al Manager de que queremos subir 1
                    onQuantityChanged(item.tool, item.quantity + 1)
                } else {
                    Toast.makeText(binding.root.context, "Límite de stock: ${item.tool.stock} uds", Toast.LENGTH_SHORT).show()
                }
            }

            binding.btnMinus.setOnClickListener {
                if (item.quantity > 1) {
                    // Avisamos al Manager de que queremos bajar 1
                    onQuantityChanged(item.tool, item.quantity - 1)
                }
            }

            binding.btnDelete.setOnClickListener {
                onItemRemoved(item.tool)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartToolBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartList[position])
    }

    override fun getItemCount(): Int = cartList.size

    fun getCartItems(): List<CartItem> = cartList
}