package com.guillen.buildstock.data.model

import com.google.firebase.firestore.DocumentId

// Representa un usuario registrado en la aplicación
data class User(
    // Identificador único del usuario
    @DocumentId
    val id: String = "",
    val name: String = "",
    val email: String = "",
    // Rol del usuario dentro del sistema
    val role: String = "operario",
    val phone: String = ""
)
