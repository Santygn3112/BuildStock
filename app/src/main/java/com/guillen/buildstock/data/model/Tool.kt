package com.guillen.buildstock.data.model

import com.google.firebase.firestore.DocumentId

// Representa una herramienta dentro del sistema de inventario
data class Tool(
    // Identificador único del documento en Firestore
    @DocumentId
    val id: String = "",
    val name: String = "",
    val brandModel: String = "",
    val category: String = "",
    val location: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    // Estado actual de la herramienta
    val status: String = "disponible",
    // ID del usuario que tiene la herramienta actualmente
    val currentUserId: String = "",
    val currentUserName: String = ""
)
