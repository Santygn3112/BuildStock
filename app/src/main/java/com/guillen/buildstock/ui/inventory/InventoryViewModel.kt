package com.guillen.buildstock.ui.inventory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guillen.buildstock.data.model.Tool
import com.guillen.buildstock.data.repository.InventoryRepository
import kotlinx.coroutines.launch

// ViewModel para la gestión del inventario
class InventoryViewModel : ViewModel() {
    // Repositorio de inventario
    private val repository = InventoryRepository()

    // LiveData que contiene la lista de herramientas
    private val _tools = MutableLiveData<List<Tool>>()
    val tools: LiveData<List<Tool>> = _tools

    // Solicita la lista completa de herramientas al repositorio
    fun fetchInventory() {
        viewModelScope.launch {
            val list = repository.getToolsList()
            _tools.value = list
        }
    }
}
