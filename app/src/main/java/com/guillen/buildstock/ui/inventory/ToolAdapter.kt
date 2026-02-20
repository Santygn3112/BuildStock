package com.guillen.buildstock.ui.inventory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.guillen.buildstock.R
import com.guillen.buildstock.data.model.Tool
import com.guillen.buildstock.databinding.ItemToolBinding

// Adaptador para mostrar la lista de herramientas en la UI
class ToolAdapter(
    private var tools: List<Tool>,
    // Acción al pulsar sobre una herramienta
    private val onItemClick: (Tool) -> Unit,
    // Acción al añadir al carrito
    private val onAddToCartClick: (Tool) -> Unit
) : RecyclerView.Adapter<ToolAdapter.ToolViewHolder>() {

    // ViewHolder para el elemento de herramienta
    class ToolViewHolder(val binding: ItemToolBinding) : RecyclerView.ViewHolder(binding.root)

    // Crea la vista para cada elemento
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToolViewHolder {
        val binding = ItemToolBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ToolViewHolder(binding)
    }

    // Vincula los datos de la herramienta con la vista
    override fun onBindViewHolder(holder: ToolViewHolder, position: Int) {
        val tool = tools[position]
        val context = holder.itemView.context

        with(holder.binding) {
            tvToolName.text = tool.name
            tvToolBrandModel.text = tool.brandModel

            val isDisponible = tool.status.lowercase() == "disponible"

            // Carga la imagen de la herramienta usando Glide
            Glide.with(context)
                .load(tool.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .centerCrop()
                .into(ivToolImage)

            // Configura el estado visual y la disponibilidad del botón de añadir
            if (isDisponible) {
                tvToolStatus.text = context.getString(R.string.status_available)
                tvToolStatus.setTextColor(ContextCompat.getColor(context, R.color.brand_green))
                btnAddToCart.visibility = View.VISIBLE
                btnAddToCart.isEnabled = true
            } else {
                val statusText = if (tool.currentUserName.isNotEmpty()) {
                    context.getString(R.string.status_in_use_format, tool.currentUserName)
                } else {
                    tool.status.replaceFirstChar { it.uppercase() }
                }
                tvToolStatus.text = statusText

                val colorRes = when (tool.status.lowercase()) {
                    "en uso" -> R.color.brand_orange
                    "averiada" -> android.R.color.holo_red_dark
                    else -> android.R.color.darker_gray
                }
                tvToolStatus.setTextColor(ContextCompat.getColor(context, colorRes))

                btnAddToCart.visibility = View.GONE
            }

            root.setOnClickListener { onItemClick(tool) }
            btnAddToCart.setOnClickListener { onAddToCartClick(tool) }
        }
    }

    // Devuelve el número de herramientas
    override fun getItemCount() = tools.size

    // Actualiza la lista de datos
    fun updateList(newList: List<Tool>) {
        this.tools = newList
        notifyDataSetChanged()
    }
}
