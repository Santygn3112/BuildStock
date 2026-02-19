package com.guillen.buildstock.data.model

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "operario",
    val phone: String = ""
)