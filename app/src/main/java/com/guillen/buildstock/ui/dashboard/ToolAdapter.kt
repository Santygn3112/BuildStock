package com.guillen.buildstock.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.guillen.buildstock.R
import com.guillen.buildstock.data.model.Tool
import com.guillen.buildstock.databinding.ItemToolBinding

class ToolAdapter(
    private var tools: List<Tool>,
    private val onItemClick: (Tool) -> Unit,
    private val onAddToCartClick: (Tool) -> Unit // Nuevo listener para el botón del carrito
) : RecyclerView.Adapter<ToolAdapter.ToolViewHolder>() {



    class ToolViewHolder(val binding: ItemToolBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToolViewHolder {
        val binding = ItemToolBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ToolViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ToolViewHolder, position: Int) {
        val tool = tools[position]
        val context = holder.itemView.context

        with(holder.binding) {
            tvToolName.text = tool.name
            tvToolBrandModel.text = tool.brandModel

            // Lógica de estado y color
            val statusText = "${tool.status.replaceFirstChar { it.uppercase() }} (${tool.stock} ud.)"
            tvToolStatus.text = statusText

            when (tool.status.lowercase()) {
                "disponible" -> tvToolStatus.setTextColor(ContextCompat.getColor(context, R.color.brand_green))
                "en uso" -> tvToolStatus.setTextColor(ContextCompat.getColor(context, R.color.brand_orange))
                "averiada" -> tvToolStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
                else -> tvToolStatus.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
            }

            // Listener para el clic en la tarjeta (abre detalle)
            root.setOnClickListener { onItemClick(tool) }

            // Listener para el botón de añadir al carrito
            btnAddToCart.setOnClickListener { onAddToCartClick(tool) }

            // TODO: Aquí cargaremos la imagen con Glide/Coil usando tool.imageUrl
        }
    }

    override fun getItemCount() = tools.size

    fun updateList(newList: List<Tool>) {
        this.tools = newList
        notifyDataSetChanged()
    }
}