package com.example.finsecureapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.finsecureapp.data.local.datastore.TokenManager
import com.example.finsecureapp.data.remote.dto.UserProfileResponse
import com.example.finsecureapp.data.repository.UserRepository
import com.example.finsecureapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UserViewModel(
    private val repository: UserRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _userState = MutableStateFlow<Resource<UserProfileResponse>?>(null)
    val userState: StateFlow<Resource<UserProfileResponse>?> = _userState

    fun loadUserProfile() {
        viewModelScope.launch {
            _userState.value = Resource.Loading

            val token = tokenManager.getToken().first()

            if (token.isNullOrEmpty()) {
                _userState.value = Resource.Error("Token not found")
                return@launch
            }

            _userState.value = repository.getMe(token)
        }
    }
}

class UserViewModelFactory(
    private val repository: UserRepository,
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(repository, tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}