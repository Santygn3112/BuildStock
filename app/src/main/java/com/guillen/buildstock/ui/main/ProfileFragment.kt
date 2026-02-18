package com.guillen.buildstock.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.guillen.buildstock.data.repository.AuthRepository
import com.guillen.buildstock.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch
import kotlin.jvm.java
import android.content.Intent
import com.guillen.buildstock.ui.login.LoginActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val authRepository = AuthRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUserProfile()
        setupListeners()
    }

    private fun loadUserProfile() {
        lifecycleScope.launch {
            val user = authRepository.getUserProfile()
            if (user != null) {
                binding.tvProfileName.text = user.name

                val roleCapitalized = user.role.replaceFirstChar { it.uppercase() }
                binding.tvProfileRole.text = "$roleCapitalized — Planta 2"
                binding.tvInitials.text = getInitials(user.name)

                // VALIDACIÓN CRÍTICA DE ROL
                if (user.role.lowercase() == "admin") {
                    binding.btnAdminPanel.visibility = View.VISIBLE
                }
            }

            binding.tvAssignedCount.text = "0"
            binding.tvMovesCount.text = "0"
        }
    }

    private fun setupListeners() {
        binding.btnAdminPanel.setOnClickListener {
            val intent = Intent(requireContext(), com.guillen.buildstock.ui.admin.AdminPanelActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {
            // 1. Cerramos sesión usando el import directo
            FirebaseAuth.getInstance().signOut()

            // 2. Navegamos al Login de forma limpia
            // ATENCIÓN: Si "LoginActivity" sale en rojo, pon el cursor encima y pulsa Alt + Enter
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
    private fun getInitials(fullName: String): String {
        val words = fullName.trim().split(" ")
        if (words.isEmpty()) return ""
        val firstInitial = words[0].take(1).uppercase()
        val secondInitial = if (words.size > 1) words[1].take(1).uppercase() else ""
        return firstInitial + secondInitial
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}