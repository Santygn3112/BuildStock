package com.guillen.buildstock.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guillen.buildstock.data.model.Tool
import com.guillen.buildstock.data.repository.InventoryRepository
import kotlinx.coroutines.launch

class InventoryViewModel : ViewModel() {
    private val repository = InventoryRepository()

    private val _tools = MutableLiveData<List<Tool>>()
    val tools: LiveData<List<Tool>> = _tools

    fun fetchInventory() {
        viewModelScope.launch {
            val list = repository.getToolsList()
            _tools.value = list
        }
    }
}