package com.guillen.buildstock.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.guillen.buildstock.data.model.Tool

object CartManager {

    private val _cartItems = MutableLiveData<MutableList<CartItem>>(mutableListOf())
    val cartItems: LiveData<MutableList<CartItem>> = _cartItems

    fun addToCart(tool: Tool) {
        val currentList = _cartItems.value ?: mutableListOf()
        val existingItem = currentList.find { it.tool.id == tool.id }

        if (existingItem != null) {
            if (existingItem.quantity < tool.stock) {
                existingItem.quantity++
            }
        } else {
            currentList.add(CartItem(tool, 1))
        }

        // Asignamos una nueva lista para forzar la actualización visual
        _cartItems.value = ArrayList(currentList)
    }

    fun updateQuantity(tool: Tool, newQuantity: Int) {
        val currentList = _cartItems.value ?: mutableListOf()
        val item = currentList.find { it.tool.id == tool.id }

        if (item != null && newQuantity <= tool.stock && newQuantity > 0) {
            item.quantity = newQuantity
            _cartItems.value = ArrayList(currentList)
        }
    }

    fun removeFromCart(tool: Tool) {
        val currentList = _cartItems.value ?: mutableListOf()
        currentList.removeAll { it.tool.id == tool.id }
        _cartItems.value = ArrayList(currentList)
    }

    fun clearCart() {
        _cartItems.value = mutableListOf()
    }
}