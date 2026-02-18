package com.guillen.buildstock.data.model

data class Tool(
    val id: String = "",
    val name: String = "",
    val brandModel: String = "", // Para "Marca y Modelo"
    val category: String = "",
    val stock: Int = 0,
    val location: String = "",    // Para "Ubicación Física"
    val description: String = "", // Para "Descripción o notas"
    val status: String = "disponible",
    val lastUser: String = "",
    val lastPhone: String = ""
)