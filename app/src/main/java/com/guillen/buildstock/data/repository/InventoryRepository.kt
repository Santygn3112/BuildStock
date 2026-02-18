package com.guillen.buildstock.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.guillen.buildstock.data.model.Tool
import kotlinx.coroutines.tasks.await

class InventoryRepository {
    private val db = FirebaseFirestore.getInstance()

    // Función para leer todas las herramientas
    suspend fun getToolsList(): List<Tool> {
        return try {
            val snapshot = db.collection("tools").get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Tool::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            android.util.Log.e("REPO_ERROR", "Error al obtener lista", e)
            emptyList()
        }
    }

    // Función para añadir una nueva herramienta (La que faltaba)
    suspend fun addTool(tool: Tool): Boolean {
        return try {
            // Firebase genera un ID automático si usamos .add()
            db.collection("tools").add(tool).await()
            true
        } catch (e: Exception) {
            android.util.Log.e("REPO_ERROR", "Error al añadir herramienta", e)
            false
        }
    }

    // Función para obtener el detalle de una sola herramienta por ID
    suspend fun getToolById(id: String): Tool? {
        return try {
            val doc = db.collection("tools").document(id).get().await()
            doc.toObject(Tool::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }

    // Añade esto dentro de tu clase InventoryRepository
    suspend fun getToolsByCategory(category: String): List<Tool> {
        return try {
            // Usamos la consulta "whereEqualTo" para filtrar directamente en la nube
            val snapshot = db.collection("tools")
                .whereEqualTo("category", category)
                .get().await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Tool::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            android.util.Log.e("REPO_ERROR", "Error al filtrar por categoría", e)
            emptyList()
        }
    }

    suspend fun getToolsCountByStatus(status: String): Int {
        return try {
            // Filtramos en la nube, no en el móvil
            val snapshot = db.collection("tools")
                .whereEqualTo("status", status)
                .get()
                .await()

            snapshot.size()
        } catch (e: Exception) {
            android.util.Log.e("REPO_ERROR", "Error al contar estado: $status", e)
            0
        }
    }

    suspend fun deleteTool(toolId: String): Boolean {
        return try {
            db.collection("tools").document(toolId).delete().await()
            true
        } catch (e: Exception) {
            android.util.Log.e("REPO_ERROR", "Error al borrar herramienta", e)
            false
        }
    }
    // Añadir en InventoryRepository.kt
    suspend fun updateTool(tool: Tool): Boolean {
        return try {
            // Usamos el ID de la herramienta para saber qué documento actualizar
            db.collection("tools").document(tool.id).set(tool).await()
            true
        } catch (e: Exception) {
            android.util.Log.e("REPO_ERROR", "Error al actualizar herramienta", e)
            false
        }
    }
}