package com.guillen.buildstock.data.model

data class Tool(
    val id: String = "",            // ID del documento de Firestore (ej: rANC6...)
    val name: String = "",
    val brandModel: String = "",
    val category: String = "",
    val status: String = "",
    val imageUrl: String = "",
    val stock: Int = 0,
    val location: String = "",      // Asegúrate de arreglar el typo en Firestore
    val description: String = ""    // Asegúrate de arreglar el typo en Firestore
)