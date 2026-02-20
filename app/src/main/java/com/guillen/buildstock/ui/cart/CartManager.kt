package com.guillen.buildstock.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.guillen.buildstock.data.model.Tool

// Singleton para gestionar el estado global del carrito de compras
object CartManager {
    // Lista mutable de herramientas en el carrito
    private val _cartItems = MutableLiveData<List<Tool>>(emptyList())
    // Exposición inmutable de la lista
    val cartItems: LiveData<List<Tool>> get() = _cartItems

    // Añade una herramienta al carrito si no está presente
    fun addTool(tool: Tool) {
        val current = _cartItems.value ?: emptyList()
        if (current.none { it.id == tool.id }) {
            _cartItems.value = current + tool
        }
    }

    // Elimina una herramienta del carrito
    fun removeTool(tool: Tool) {
        val current = _cartItems.value ?: emptyList()
        _cartItems.value = current.filter { it.id != tool.id }
    }

    // Vacía completamente el carrito
    fun clearCart() {
        _cartItems.value = emptyList()
    }
}
