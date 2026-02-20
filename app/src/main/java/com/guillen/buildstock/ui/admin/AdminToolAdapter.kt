package com.guillen.buildstock.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.guillen.buildstock.data.model.Tool
import com.guillen.buildstock.databinding.ItemAdminToolBinding

// Adaptador para mostrar la lista de herramientas en el panel de administración
class AdminToolAdapter(
    private var tools: List<Tool>,
    // Callback para la acción de editar
    private val onEditClick: (Tool) -> Unit,
    // Callback para la acción de eliminar
    private val onDeleteClick: (Tool) -> Unit
) : RecyclerView.Adapter<AdminToolAdapter.AdminToolViewHolder>() {

    // ViewHolder que mantiene las referencias a la vista de cada ítem
    class AdminToolViewHolder(val binding: ItemAdminToolBinding) : RecyclerView.ViewHolder(binding.root)

    // Crea la vista para cada elemento de la lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminToolViewHolder {
        val binding = ItemAdminToolBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdminToolViewHolder(binding)
    }

    // Vincula los datos de la herramienta con la vista
    override fun onBindViewHolder(holder: AdminToolViewHolder, position: Int) {
        val tool = tools[position]

        with(holder.binding) {
            tvToolName.text = tool.name
            tvToolCategory.text = tool.category

            btnEditTool.setOnClickListener { onEditClick(tool) }
            btnDeleteTool.setOnClickListener { onDeleteClick(tool) }
        }
    }

    // Devuelve el número total de herramientas en la lista
    override fun getItemCount() = tools.size

    // Actualiza la lista de herramientas y notifica al adaptador
    fun updateList(newList: List<Tool>) {
        this.tools = newList
        notifyDataSetChanged()
    }
}
