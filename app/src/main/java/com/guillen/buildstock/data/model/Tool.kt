
package com.guillen.buildstock.data.model

import com.google.firebase.firestore.DocumentId

data class Tool(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val brandModel: String = "",
    val category: String = "",
    val stock: Int = 0,
    val location: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val status: String = "disponible",
    val lastUser: String = "",
    val lastPhone: String = ""
)
