package com.guillen.buildstock.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.guillen.buildstock.data.model.Tool
import com.guillen.buildstock.databinding.ItemCartToolBinding

// 1. Mini-modelo temporal para juntar la herramienta con su cantidad en el carrito
data class CartItem(
    val tool: Tool,
    var quantity: Int = 1
)

class CartAdapter(
    private var cartList: MutableList<CartItem>,
    private val onTotalChanged: () -> Unit // Aviso al Fragment para que recalcule el total
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: ItemCartToolBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CartItem, position: Int) {
            // Asignamos el nombre y la cantidad actual
            binding.tvToolNameCart.text = item.tool.name
            binding.tvQuantity.text = item.quantity.toString()

            // Lógica del botón SUMAR
            binding.btnPlus.setOnClickListener {
                // Aquí en el futuro validaremos que no supere el Stock disponible
                item.quantity++
                binding.tvQuantity.text = item.quantity.toString()
                onTotalChanged()
            }

            // Lógica del botón RESTAR
            binding.btnMinus.setOnClickListener {
                if (item.quantity > 1) {
                    item.quantity--
                    binding.tvQuantity.text = item.quantity.toString()
                    onTotalChanged()
                }
            }

            // Lógica del botón BORRAR
            binding.btnDelete.setOnClickListener {
                cartList.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
                notifyItemRangeChanged(adapterPosition, cartList.size)
                onTotalChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartToolBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartList[position], position)
    }

    override fun getItemCount(): Int = cartList.size

    // Función para leer la lista actual desde fuera
    fun getCartItems(): List<CartItem> = cartList
}