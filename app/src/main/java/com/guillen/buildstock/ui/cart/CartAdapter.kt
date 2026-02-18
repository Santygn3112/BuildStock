package com.guillen.buildstock.ui.cart

import android.annotation.SuppressLint
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
                    onQuantityChanged(item.tool, item.quantity + 1)
                } else {
                    Toast.makeText(binding.root.context, "Límite de stock: ${item.tool.stock} uds", Toast.LENGTH_SHORT).show()
                }
            }

            binding.btnMinus.setOnClickListener {
                if (item.quantity > 1) {
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

    fun getCartList(): List<CartItem> = cartList

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<CartItem>) {
        cartList.clear()
        cartList.addAll(newList)
        notifyDataSetChanged()
    }

    fun updateQuantity(tool: Tool, newQuantity: Int) {
        val item = cartList.find { it.tool.id == tool.id }
        item?.let {
            it.quantity = newQuantity
            notifyItemChanged(cartList.indexOf(it))
        }
    }

    fun removeItem(tool: Tool) {
        val item = cartList.find { it.tool.id == tool.id }
        item?.let {
            val position = cartList.indexOf(it)
            cartList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}