package com.guillen.buildstock.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.guillen.buildstock.data.model.Tool
import com.guillen.buildstock.ui.cart.CartItem
import kotlinx.coroutines.tasks.await

class InventoryRepository {
    private val db = FirebaseFirestore.getInstance()
    private val toolsCollection = db.collection("tools")

    // --- FUNCIONES DE ADMINISTRACIÓN Y LISTADO ---

    // Cambiado a 'getToolsList' para que coincida con tu AdminInventoryActivity
    suspend fun getToolsList(): List<Tool> {
        return try {
            val snapshot = toolsCollection.get().await()
            snapshot.toObjects(Tool::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getToolById(id: String): Tool? {
        return try {
            val snapshot = toolsCollection.document(id).get().await()
            snapshot.toObject(Tool::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getToolsByCategory(category: String): List<Tool> {
        return try {
            val snapshot = toolsCollection.whereEqualTo("category", category).get().await()
            snapshot.toObjects(Tool::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addTool(tool: Tool): Boolean {
        return try {
            val docRef = toolsCollection.document()
            val newTool = tool.copy(id = docRef.id)
            docRef.set(newTool).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateTool(tool: Tool): Boolean {
        return try {
            toolsCollection.document(tool.id).set(tool).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteTool(id: String): Boolean {
        return try {
            toolsCollection.document(id).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // --- FUNCIONES DE ESTADÍSTICAS ---

    suspend fun getToolsCountByStatus(status: String): Int {
        return try {
            val snapshot = toolsCollection.whereEqualTo("status", status).get().await()
            snapshot.size()
        } catch (e: Exception) {
            0
        }
    }

    // --- FUNCIÓN DEL CARRITO (TRANSACCIONES) ---

    suspend fun processTransaction(
        items: List<CartItem>,
        isRecogida: Boolean,
        userName: String,
        userPhone: String
    ): Boolean {
        return try {
            val batch = db.batch()
            for (item in items) {
                val toolRef = toolsCollection.document(item.tool.id)
                val newStock = if (isRecogida) item.tool.stock - item.quantity else item.tool.stock + item.quantity
                val newStatus = if (newStock <= 0) "en uso" else "disponible"

                val updates = mutableMapOf<String, Any>(
                    "stock" to newStock,
                    "status" to newStatus
                )

                if (isRecogida) {
                    updates["lastUser"] = userName
                    updates["lastPhone"] = userPhone
                } else {
                    updates["lastUser"] = ""
                    updates["lastPhone"] = ""
                }
                batch.update(toolRef, updates)
            }
            batch.commit().await()
            true
        } catch (e: Exception) {
            false
        }
    }
}