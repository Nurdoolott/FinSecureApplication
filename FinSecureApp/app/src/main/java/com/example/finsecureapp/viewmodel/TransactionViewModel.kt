package com.example.finsecureapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.finsecureapp.data.local.datastore.TokenManager
import com.example.finsecureapp.data.remote.dto.TransferRequest
import com.example.finsecureapp.data.remote.dto.TransferResponse
import com.example.finsecureapp.data.repository.TransactionRepository
import com.example.finsecureapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val repository: TransactionRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _transferState = MutableStateFlow<Resource<TransferResponse>?>(null)
    val transferState: StateFlow<Resource<TransferResponse>?> = _transferState

    fun transfer(transferMethod: String, receiverValue: String, amount: Double) {
        viewModelScope.launch {
            _transferState.value = Resource.Loading

            val token = tokenManager.getToken().first()

            if (token.isNullOrEmpty()) {
                _transferState.value = Resource.Error("Token not found")
                return@launch
            }

            _transferState.value = repository.transfer(
                token = token,
                transferMethod = transferMethod,
                receiverValue = receiverValue,
                amount = amount
            )
        }
    }
}

class TransactionViewModelFactory(
    private val repository: TransactionRepository,
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            return TransactionViewModel(repository, tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}