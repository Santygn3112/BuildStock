package com.guillen.buildstock.ui.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.guillen.buildstock.data.model.Tool
import com.guillen.buildstock.databinding.ItemCartToolBinding

class CartAdapter(
    private var tools: List<Tool>,
    private val onItemRemoved: (Tool) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(val binding: ItemCartToolBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartToolBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val tool = tools[position]

        with(holder.binding) {
            tvToolNameCart.text = tool.name

            btnMinus.visibility = View.GONE
            btnPlus.visibility = View.GONE
            tvQuantity.visibility = View.GONE
            btnDelete.setOnClickListener { onItemRemoved(tool) }
        }
    }

    override fun getItemCount() = tools.size

    fun updateList(newList: List<Tool>) {
        this.tools = newList
        notifyDataSetChanged()
    }

    fun getCartList(): List<Tool> = tools
}