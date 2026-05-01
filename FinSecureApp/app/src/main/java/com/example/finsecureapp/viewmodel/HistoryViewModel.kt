package com.example.finsecureapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.finsecureapp.data.local.datastore.TokenManager
import com.example.finsecureapp.data.remote.dto.TransactionHistoryItem
import com.example.finsecureapp.data.repository.TransactionRepository
import com.example.finsecureapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val repository: TransactionRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _historyState = MutableStateFlow<Resource<List<TransactionHistoryItem>>?>(null)
    val historyState: StateFlow<Resource<List<TransactionHistoryItem>>?> = _historyState

    fun loadHistory() {
        viewModelScope.launch {
            _historyState.value = Resource.Loading

            val token = tokenManager.getToken().first()

            if (token.isNullOrEmpty()) {
                _historyState.value = Resource.Error("Token not found")
                return@launch
            }

            _historyState.value = repository.getHistory(token)
        }
    }
}

class HistoryViewModelFactory(
    private val repository: TransactionRepository,
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(repository, tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}