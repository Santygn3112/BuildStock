package com.guillen.buildstock.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.guillen.buildstock.data.model.Tool

object CartManager {
    private val _cartItems = MutableLiveData<List<Tool>>(emptyList())
    val cartItems: LiveData<List<Tool>> get() = _cartItems

    fun addTool(tool: Tool) {
        val current = _cartItems.value ?: emptyList()
        if (current.none { it.id == tool.id }) {
            _cartItems.value = current + tool
        }
    }

    fun removeTool(tool: Tool) {
        val current = _cartItems.value ?: emptyList()
        _cartItems.value = current.filter { it.id != tool.id }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }
}