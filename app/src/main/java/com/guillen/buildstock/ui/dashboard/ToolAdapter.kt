package com.guillen.buildstock.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.guillen.buildstock.R
import com.guillen.buildstock.data.model.Tool
import com.guillen.buildstock.databinding.ItemToolBinding

class ToolAdapter(
    private var tools: List<Tool>,
    private val onItemClick: (Tool) -> Unit,
    private val onAddToCartClick: (Tool) -> Unit
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
            // IDs REALES de tu item_tool.xml
            tvToolName.text = tool.name
            tvToolBrandModel.text = tool.brandModel

            // Lógica de estado actualizada (Sin cantidades)
            val isDisponible = tool.status.lowercase() == "disponible"

            // CARGA DE IMAGEN CON GLIDE
            com.bumptech.glide.Glide.with(context)
                .load(tool.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery) // Imagen mientras carga
                .error(android.R.drawable.ic_menu_report_image)   // Imagen si el link está roto
                .centerCrop()
                .into(ivToolImage)

            if (isDisponible) {
                tvToolStatus.text = "Disponible"
                tvToolStatus.setTextColor(ContextCompat.getColor(context, R.color.brand_green))
                // Si está disponible, permitimos añadir al carrito
                btnAddToCart.visibility = View.VISIBLE
                btnAddToCart.isEnabled = true
            } else {
                // Si está en uso, mostramos quién la tiene (si el nombre no está vacío)
                val statusText = if (tool.currentUserName.isNotEmpty()) {
                    "En uso (${tool.currentUserName})"
                } else {
                    tool.status.replaceFirstChar { it.uppercase() }
                }
                tvToolStatus.text = statusText

                // Colores según estado
                val colorRes = when (tool.status.lowercase()) {
                    "en uso" -> R.color.brand_orange
                    "averiada" -> android.R.color.holo_red_dark
                    else -> android.R.color.darker_gray
                }
                tvToolStatus.setTextColor(ContextCompat.getColor(context, colorRes))

                // Si no está disponible, no se puede añadir al carrito para recoger
                btnAddToCart.visibility = View.GONE
            }

            // Listeners
            root.setOnClickListener { onItemClick(tool) }
            btnAddToCart.setOnClickListener { onAddToCartClick(tool) }

            // TODO: Cargar imagen con Glide/Coil usando tool.imageUrl si existe
        }
    }

    override fun getItemCount() = tools.size

    fun updateList(newList: List<Tool>) {
        this.tools = newList
        notifyDataSetChanged()
    }
}