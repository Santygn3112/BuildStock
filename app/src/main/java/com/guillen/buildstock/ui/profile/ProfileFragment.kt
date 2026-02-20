package com.guillen.buildstock.ui.profile

import android.content.Intent
import android.os.Bundle
import com.guillen.buildstock.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.guillen.buildstock.data.repository.AuthRepository
import com.guillen.buildstock.data.repository.InventoryRepository
import com.guillen.buildstock.databinding.FragmentProfileBinding
import com.guillen.buildstock.ui.auth.LoginActivity
import com.guillen.buildstock.ui.home.RecentMovementsActivity
import kotlinx.coroutines.launch

// Fragmento que muestra el perfil del usuario y opciones de cuenta
class ProfileFragment : Fragment() {

    // Enlace con la vista XML
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // Repositorios de datos
    private val authRepository = AuthRepository()
    private val inventoryRepository = InventoryRepository()

    // Inflado de la vista
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Configuración inicial
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUserProfile()
        setupListeners()
    }

    // Carga los datos del usuario y sus estadísticas
    private fun loadUserProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            val user = authRepository.getUserProfile()
            if (user != null) {
                binding.tvProfileName.text = user.name

                val roleCapitalized = user.role.replaceFirstChar { it.uppercase() }
                binding.tvProfileRole.text = roleCapitalized + getString(R.string.suffix_plant_location)
                binding.tvInitials.text = getInitials(user.name)

                // Control de visibilidad para opciones de administrador
                if (user.role.lowercase() == "admin") {
                    binding.btnAdminPanel.visibility = View.VISIBLE
                    binding.btnRecentMovements.visibility = View.VISIBLE
                } else {
                    binding.btnAdminPanel.visibility = View.GONE
                    binding.btnRecentMovements.visibility = View.GONE
                }

                val assignedTools = inventoryRepository.getToolsByUserId(user.id)
                binding.tvAssignedCount.text = assignedTools.size.toString()

                val movesToday = inventoryRepository.getTodayUserMovementsCount(user.id)
                binding.tvMovesCount.text = movesToday.toString()

            } else {
                binding.tvAssignedCount.text = "0"
                binding.tvMovesCount.text = "0"
            }
        }
    }

    // Configura los listeners de los botones
    private fun setupListeners() {
        binding.btnAdminPanel.setOnClickListener {
            val intent = Intent(requireContext(), com.guillen.buildstock.ui.admin.AdminPanelActivity::class.java)
            startActivity(intent)
        }

        binding.btnRecentMovements.setOnClickListener {
            val intent = Intent(requireContext(), RecentMovementsActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    // Genera las iniciales a partir del nombre completo
    private fun getInitials(fullName: String): String {
        val words = fullName.trim().split(" ")
        if (words.isEmpty()) return ""
        val firstInitial = words[0].take(1).uppercase()
        val secondInitial = if (words.size > 1) words[1].take(1).uppercase() else ""
        return firstInitial + secondInitial
    }

    // Limpieza de la vista
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
