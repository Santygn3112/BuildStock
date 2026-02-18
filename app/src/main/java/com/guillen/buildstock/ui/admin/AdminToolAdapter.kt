package com.guillen.buildstock.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.guillen.buildstock.data.model.Tool
import com.guillen.buildstock.databinding.ItemAdminToolBinding

class AdminToolAdapter(
    private var tools: List<Tool>,
    private val onEditClick: (Tool) -> Unit,
    private val onDeleteClick: (Tool) -> Unit
) : RecyclerView.Adapter<AdminToolAdapter.AdminToolViewHolder>() {

    class AdminToolViewHolder(val binding: ItemAdminToolBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminToolViewHolder {
        val binding = ItemAdminToolBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdminToolViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdminToolViewHolder, position: Int) {
        val tool = tools[position]

        with(holder.binding) {
            tvToolName.text = tool.name
            tvToolCategory.text = tool.category

            btnEditTool.setOnClickListener { onEditClick(tool) }
            btnDeleteTool.setOnClickListener { onDeleteClick(tool) }
        }
    }

    override fun getItemCount() = tools.size

    fun updateList(newList: List<Tool>) {
        this.tools = newList
        notifyDataSetChanged()
    }
}