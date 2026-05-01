package com.example.finsecureapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.finsecureapp.data.local.datastore.TokenManager
import com.example.finsecureapp.data.remote.dto.BalanceResponse
import com.example.finsecureapp.data.repository.AccountRepository
import com.example.finsecureapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AccountViewModel(
    private val repository: AccountRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _balanceState = MutableStateFlow<Resource<BalanceResponse>?>(null)
    val balanceState: StateFlow<Resource<BalanceResponse>?> = _balanceState

    fun loadBalance() {
        viewModelScope.launch {
            _balanceState.value = Resource.Loading

            val token = tokenManager.getToken().first()

            if (token.isNullOrEmpty()) {
                _balanceState.value = Resource.Error("Token not found")
                return@launch
            }

            _balanceState.value = repository.getBalance(token)
        }
    }
}

class AccountViewModelFactory(
    private val repository: AccountRepository,
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
            return AccountViewModel(repository, tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}