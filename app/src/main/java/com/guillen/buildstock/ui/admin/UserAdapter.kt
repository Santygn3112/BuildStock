package com.guillen.buildstock.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.guillen.buildstock.R
import com.guillen.buildstock.data.model.User
import com.guillen.buildstock.databinding.ItemUserBinding
class UserAdapter(
    private var users: List<User>,
    private val onEditClick: (User) -> Unit,
    private val onDeleteClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        val context = holder.itemView.context

        with(holder.binding) {
            tvUserName.text = user.name
            tvUserEmail.text = user.email ?: "Sin correo"

            val role = user.role.uppercase()
            tvUserRole.text = role

            if (role == "ADMIN") {
                tvUserRole.setTextColor(ContextCompat.getColor(context, R.color.brand_orange))
            } else {
                tvUserRole.setTextColor(ContextCompat.getColor(context, R.color.brand_navy))
            }

            btnEditUser.setOnClickListener { onEditClick(user) }
            btnDeleteUser.setOnClickListener { onDeleteClick(user) }
        }
    }

    override fun getItemCount() = users.size

    fun updateList(newList: List<User>) {
        this.users = newList
        notifyDataSetChanged()
    }
}