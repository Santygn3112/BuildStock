package com.guillen.buildstock.ui.home

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.guillen.buildstock.R
import com.guillen.buildstock.data.model.Movement
import com.guillen.buildstock.databinding.ItemMovementBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Adaptador para visualizar el historial de movimientos de herramientas
class MovementAdapter(
    private var movements: List<Movement>
) : RecyclerView.Adapter<MovementAdapter.MovementViewHolder>() {

    // ViewHolder que contiene la vista de un movimiento individual
    class MovementViewHolder(val binding: ItemMovementBinding) : RecyclerView.ViewHolder(binding.root)

    // Crea la vista para cada elemento de la lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovementViewHolder {
        val binding = ItemMovementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovementViewHolder(binding)
    }

    // Asigna los datos del movimiento a la vista
    override fun onBindViewHolder(holder: MovementViewHolder, position: Int) {
        val movement = movements[position]
        val context = holder.itemView.context

        with(holder.binding) {
            tvMovementTool.text = movement.toolName
            tvMovementUser.text = "Operario: ${movement.userName}"
            tvMovementType.text = movement.type

            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            tvMovementDate.text = sdf.format(Date(movement.timestamp))

            val colorRes = if (movement.type == "RECOGIDA") R.color.brand_orange else R.color.brand_green
            tvMovementType.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, colorRes))
        }
    }

    // Devuelve el número total de movimientos
    override fun getItemCount() = movements.size

    // Actualiza la lista de movimientos y refresca la vista
    fun updateList(newList: List<Movement>) {
        this.movements = newList
        notifyDataSetChanged()
    }
}
