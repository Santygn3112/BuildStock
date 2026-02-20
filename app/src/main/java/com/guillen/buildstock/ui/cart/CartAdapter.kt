package com.guillen.buildstock.ui.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.guillen.buildstock.data.model.Tool
import com.guillen.buildstock.databinding.ItemCartToolBinding

// Adaptador para mostrar los elementos en el carrito de compras
class CartAdapter(
    private var tools: List<Tool>,
    // Callback cuando se elimina un ítem
    private val onItemRemoved: (Tool) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    // ViewHolder para el elemento del carrito
    class CartViewHolder(val binding: ItemCartToolBinding) : RecyclerView.ViewHolder(binding.root)

    // Crea la vista para cada elemento
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartToolBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    // Vincula los datos con la vista
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val tool = tools[position]

        with(holder.binding) {
            tvToolNameCart.text = tool.name

            // Oculta controles de cantidad ya que es unitario por herramienta
            btnMinus.visibility = View.GONE
            btnPlus.visibility = View.GONE
            tvQuantity.visibility = View.GONE

            btnDelete.setOnClickListener { onItemRemoved(tool) }
        }
    }

    // Devuelve el número total de ítems
    override fun getItemCount() = tools.size

    // Actualiza la lista del carrito
    fun updateList(newList: List<Tool>) {
        this.tools = newList
        notifyDataSetChanged()
    }

    // Obtiene la lista actual de herramientas en el adaptador
    fun getCartList(): List<Tool> = tools
}
