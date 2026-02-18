package com.guillen.buildstock.ui.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.guillen.buildstock.R
import com.guillen.buildstock.databinding.ActivityMainBinding
import com.guillen.buildstock.ui.main.HomeFragment
import com.guillen.buildstock.ui.main.ProfileFragment
import androidx.lifecycle.lifecycleScope
import com.guillen.buildstock.ui.cart.CartFragment
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // Bloqueo del modo oscuro que pusimos antes
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
            androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
        )
        super.onCreate(savedInstanceState)
        binding = com.guillen.buildstock.databinding.ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 1. Configuramos el listener de la barra de navegación
        setupBottomNavigation()
        // 2. Cargamos el fragment de Inicio por defecto al abrir la app
        if (savedInstanceState == null) {
            loadFragment(com.guillen.buildstock.ui.main.HomeFragment())
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_cart -> {
                    loadFragment(CartFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    // Función crítica para el cambio de pantallas
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}