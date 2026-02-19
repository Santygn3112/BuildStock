package com.guillen.buildstock.ui.main

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

class MovementAdapter(
    private var movements: List<Movement>
) : RecyclerView.Adapter<MovementAdapter.MovementViewHolder>() {

    class MovementViewHolder(val binding: ItemMovementBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovementViewHolder {
        val binding = ItemMovementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovementViewHolder, position: Int) {
        val movement = movements[position]
        val context = holder.itemView.context

        with(holder.binding) {
            tvMovementTool.text = movement.toolName
            tvMovementUser.text = "Operario: ${movement.userName}"
            tvMovementType.text = movement.type

            // Formatear la fecha
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            tvMovementDate.text = sdf.format(Date(movement.timestamp))

            // Color del badge según sea Recogida o Devolución
            val colorRes = if (movement.type == "RECOGIDA") R.color.brand_orange else R.color.brand_green
            tvMovementType.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, colorRes))
        }
    }

    override fun getItemCount() = movements.size

    fun updateList(newList: List<Movement>) {
        this.movements = newList
        notifyDataSetChanged()
    }
}