package com.guillen.buildstock.ui.main

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import com.guillen.buildstock.R
import com.guillen.buildstock.data.model.Tool
import com.guillen.buildstock.databinding.ActivityMainBinding
import com.guillen.buildstock.ui.cart.CartFragment
import com.guillen.buildstock.ui.home.HomeFragment
import com.guillen.buildstock.ui.profile.ProfileFragment
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Actividad contenedora principal de la aplicación
class MainActivity : AppCompatActivity() {

    // Enlace con la vista XML
    private lateinit var binding: ActivityMainBinding

    // Inicialización de la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
            androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
        )
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()

        // Carga el fragmento de inicio por defecto
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
    }

    // Configura la barra de navegación inferior
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

    // Reemplaza el fragmento actual en el contenedor
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

}
