package com.guillen.buildstock.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.guillen.buildstock.data.model.Tool

class SharedCartViewModel : ViewModel() {

    // La lista viva de herramientas en el carrito
    private val _cartItems = MutableLiveData<MutableList<CartItem>>(mutableListOf())
    val cartItems: LiveData<MutableList<CartItem>> = _cartItems

    // Función para añadir desde el Inventario
    fun addToCart(tool: Tool) {
        val currentList = _cartItems.value ?: mutableListOf()

        // Buscamos si la herramienta ya está en el carrito
        val existingItem = currentList.find { it.tool.id == tool.id }

        if (existingItem != null) {
            // Si ya está y hay stock suficiente, sumamos 1
            if (existingItem.quantity < tool.stock) {
                existingItem.quantity++
            }
        } else {
            // Si es nueva, la metemos con cantidad 1
            currentList.add(CartItem(tool, 1))
        }

        // Avisamos a la interfaz de que hay cambios
        _cartItems.value = currentList
    }

    // Función para actualizar cantidades desde los botones + y - del carrito
    fun updateQuantity(tool: Tool, newQuantity: Int) {
        val currentList = _cartItems.value ?: mutableListOf()
        val item = currentList.find { it.tool.id == tool.id }

        if (item != null) {
            item.quantity = newQuantity
            _cartItems.value = currentList
        }
    }

    // Función para la papelera
    fun removeFromCart(tool: Tool) {
        val currentList = _cartItems.value ?: mutableListOf()
        currentList.removeAll { it.tool.id == tool.id }
        _cartItems.value = currentList
    }

    // Para vaciar el carrito después de confirmar la transacción
    fun clearCart() {
        _cartItems.value = mutableListOf()
    }
}