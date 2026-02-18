package com.guillen.buildstock.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "operario",
    val phone: String = "" // <-- NUEVO CAMPO
)